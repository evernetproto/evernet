package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedNode;
import org.evernet.controller.WebSocketHandler;
import org.evernet.enums.MessageStatus;
import org.evernet.exception.NotFoundException;
import org.evernet.model.Message;
import org.evernet.model.MessageGateway;
import org.evernet.response.MessageReceiverResponse;
import org.evernet.util.Json;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    private final MessageGatewayService messageGatewayService;

    public MessageReceiverResponse receive(List<Message> messages) {
        Map<String, MessageStatus> messageStatusMap = new HashMap<>();

        for (Message message : messages) {
            try {
                MessageGateway messageGateway = messageGatewayService.get(message.getRecipientMessageGatewayIdentifier(), message.getRecipientNodeIdentifier());
                WebSocketHandler.sendToActor(messageGateway.getActorAddress(), Json.encode(message));
                messageStatusMap.put(message.getId(), MessageStatus.SENT);
            } catch (NotFoundException e) {
                messageStatusMap.put(message.getId(), MessageStatus.HARD_FAILED);
            } catch (Exception e) {
                messageStatusMap.put(message.getId(), MessageStatus.SOFT_FAILED);
            }
        }

        return MessageReceiverResponse.builder().messageStatus(messageStatusMap).build();
    }

    public MessageReceiverResponse receive(List<Message> messages, AuthenticatedNode authenticatedNode) {
        List<Message> validMessages = new ArrayList<>();

        for (Message message : messages) {
            if (authenticatedNode.getNodeIdentifier().equals(message.getSenderNodeIdentifier()) && authenticatedNode.getNodeVertexEndpoint().equals(message.getSenderVertexEndpoint())) {
                validMessages.add(message);
            }
        }

        return receive(validMessages);
    }
}
