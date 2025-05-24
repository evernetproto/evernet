package org.evernet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.evernet.bean.MessageGatewayAddress;
import org.evernet.bean.NodeAddress;
import org.evernet.enums.MessageStatus;
import org.evernet.model.Message;
import org.evernet.model.MessageGateway;
import org.evernet.model.Node;
import org.evernet.request.MessageContentRequest;
import org.evernet.request.SendMessageRequest;
import org.evernet.response.SendMessageResponse;
import org.evernet.util.Ed25519KeyHelper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSenderService {

    private final MessageGatewayService messageGatewayService;

    private final MessageService messageService;

    private final ConfigService configService;

    private final MessageReceiverService messageReceiverService;

    private final NodeService nodeService;

    private final RemoteNodeService remoteNodeService;

    public SendMessageResponse queue(String messageGatewayIdentifier, SendMessageRequest request, String nodeIdentifier, String actorAddress) {
        MessageGateway messageGateway = messageGatewayService.get(messageGatewayIdentifier, nodeIdentifier, actorAddress);
        MessageContentRequest requestMessage = request.getMessage();
        Instant sendAt = request.getSendAfterSeconds() == null ? Instant.now() : Instant.now().plus(request.getSendAfterSeconds(), ChronoUnit.SECONDS);

        List<Message> messages = new ArrayList<>();

        for (String recipientMessageGatewayAddress : request.getRecipientMessageGatewayAddresses()) {
            MessageGatewayAddress recipientMessageGateway = MessageGatewayAddress.fromString(recipientMessageGatewayAddress);

            Message message = Message.builder()
                    .senderMessageGatewayIdentifier(messageGateway.getIdentifier())
                    .senderNodeIdentifier(nodeIdentifier)
                    .senderVertexEndpoint(configService.getVertexEndpoint())
                    .senderActorAddress(actorAddress)
                    .topic(requestMessage.getTopic())
                    .headers(requestMessage.getHeaders())
                    .payload(requestMessage.getPayload())
                    .recipientVertexEndpoint(recipientMessageGateway.getNodeAddress().getVertexEndpoint())
                    .recipientNodeIdentifier(recipientMessageGateway.getNodeAddress().getNodeIdentifier())
                    .recipientMessageGatewayIdentifier(recipientMessageGateway.getMessageGatewayIdentifier())
                    .sendAt(sendAt)
                    .expiresAt(Instant.now().plus(messageGateway.getMessageExpirySeconds(), ChronoUnit.SECONDS))
                    .status(MessageStatus.SCHEDULED)
                    .build();

            messages.add(message);
        }

        messages = messageService.save(messages);
        return SendMessageResponse.builder().messages(messages).build();
    }

    public void sendUnsent() {
        List<Message> messages = messageService.peekUnsent(Pageable.ofSize(100));
        String vertexEndpoint = configService.getVertexEndpoint();

        List<Message> localMessages = new ArrayList<>();
        List<Message> remoteMessages = new ArrayList<>();

        for (Message message : messages) {
            if (vertexEndpoint.equals(message.getRecipientVertexEndpoint())) {
                localMessages.add(message);
            } else {
                remoteMessages.add(message);
            }
        }

        Map<String, MessageStatus> messageStatusMap = new HashMap<>();

        if (!localMessages.isEmpty()) {
            Map<String, MessageStatus> localMessageStatusMap = messageReceiverService.receive(localMessages).getMessageStatus();
            messageStatusMap.putAll(localMessageStatusMap);
        }

        if (!remoteMessages.isEmpty()) {
            Map<String, MessageStatus> remoteMessageStatusMap = deliverRemoteMessages(messages, vertexEndpoint);
            messageStatusMap.putAll(remoteMessageStatusMap);
        }

        for (Message message : messages) {
            MessageStatus newMessageStatus = messageStatusMap.get(message.getId());

            if (newMessageStatus != null) {
                message.setStatus(newMessageStatus);
            }
        }

        messageService.save(messages);
    }

    private Map<String, MessageStatus> deliverRemoteMessages(List<Message> messages, String senderVertexEndpoint) {
        Map<String, List<Message>> senderMessagesMap = new HashMap<>();

        for (Message message : messages) {
            List<Message> senderMessages = senderMessagesMap.getOrDefault(message.getSenderNodeIdentifier(), new ArrayList<>());
            senderMessages.add(message);
            senderMessagesMap.put(message.getSenderNodeIdentifier(), senderMessages);
        }

        Map<String, MessageStatus> statusMap = new HashMap<>();

        for (Map.Entry<String, List<Message>> entry : senderMessagesMap.entrySet()) {
            Map<String, MessageStatus> currentBatchStatus = deliverRemoteMessagesViaSenderNode(entry.getValue(), senderVertexEndpoint, entry.getKey());
            statusMap.putAll(currentBatchStatus);
        }

        return statusMap;
    }

    private Map<String, MessageStatus> deliverRemoteMessagesViaSenderNode(List<Message> messages, String senderVertexEndpoint, String senderNodeIdentifier) {
        Map<String, List<Message>> vertexMessagesMap = new HashMap<>();

        for (Message message : messages) {
            List<Message> vertexMessages = vertexMessagesMap.getOrDefault(message.getRecipientVertexEndpoint(), new ArrayList<>());
            vertexMessages.add(message);
            vertexMessagesMap.put(message.getRecipientVertexEndpoint(), vertexMessages);
        }

        Map<String, MessageStatus> statusMap = new HashMap<>();

        for (Map.Entry<String, List<Message>> entry : vertexMessagesMap.entrySet()) {
            Map<String, MessageStatus> currentBatchStatus = deliverRemoteMessagesViaSenderNodeToVertex(entry.getValue(), senderVertexEndpoint, senderNodeIdentifier, entry.getKey());
            statusMap.putAll(currentBatchStatus);
        }

        return statusMap;
    }

    private Map<String, MessageStatus> deliverRemoteMessagesViaSenderNodeToVertex(List<Message> messages, String senderVertexEndpoint, String senderNodeIdentifier, String recipientVertexEndpoint) {
        Map<String, List<Message>> nodeMessagesMap = new HashMap<>();

        for (Message message : messages) {
            List<Message> nodeMessages = nodeMessagesMap.getOrDefault(message.getRecipientNodeIdentifier(), new ArrayList<>());
            nodeMessages.add(message);
            nodeMessagesMap.put(message.getRecipientNodeIdentifier(), nodeMessages);
        }

        Map<String, MessageStatus> statusMap = new HashMap<>();

        for (Map.Entry<String, List<Message>> entry : nodeMessagesMap.entrySet()) {
            Map<String, MessageStatus> currentBatchStatus = deliverRemoteMessagesViaSenderNodeToVertexNode(entry.getValue(), senderVertexEndpoint, senderNodeIdentifier, recipientVertexEndpoint, entry.getKey());
            statusMap.putAll(currentBatchStatus);
        }

        return statusMap;
    }

    private Map<String, MessageStatus> deliverRemoteMessagesViaSenderNodeToVertexNode(List<Message> messages, String senderVertexEndpoint, String senderNodeIdentifier, String recipientVertexEndpoint, String recipientNodeIdentifier) {
        Node senderNode = nodeService.get(senderNodeIdentifier);
        NodeAddress recipientNodeAddress = NodeAddress.builder().vertexEndpoint(recipientVertexEndpoint).nodeIdentifier(recipientNodeIdentifier).build();
        NodeAddress senderNodeAddress = NodeAddress.builder().vertexEndpoint(senderVertexEndpoint).nodeIdentifier(senderNodeIdentifier).build();

        try {
            return remoteNodeService.sendMessages(messages, recipientNodeAddress, senderNodeAddress, Ed25519KeyHelper.stringToPrivateKey(senderNode.getSigningPrivateKey())).getMessageStatus();
        } catch (Exception e) {
            log.error("Error while sending messages to remote node {} on vertex {}",recipientNodeIdentifier, recipientVertexEndpoint, e);
            return messages.stream().collect(Collectors.toMap(Message::getId, _ -> MessageStatus.SOFT_FAILED, (_, b) -> b));
        }
    }
}
