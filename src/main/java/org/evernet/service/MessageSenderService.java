package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.bean.MessageGatewayAddress;
import org.evernet.enums.MessageStatus;
import org.evernet.model.Message;
import org.evernet.model.MessageGateway;
import org.evernet.request.MessageContentRequest;
import org.evernet.request.SendMessageRequest;
import org.evernet.response.SendMessageResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageSenderService {

    private final MessageGatewayService messageGatewayService;

    private final MessageService messageService;

    public SendMessageResponse send(String messageGatewayIdentifier, SendMessageRequest request, String nodeIdentifier, String actorAddress) {
        MessageGateway messageGateway = messageGatewayService.get(messageGatewayIdentifier, nodeIdentifier, actorAddress);

        MessageContentRequest requestMessage = request.getMessage();

        Instant sendAt = request.getSendAfterSeconds() == null ? Instant.now() : Instant.now().plus(request.getSendAfterSeconds(), ChronoUnit.SECONDS);

        List<Message> messages = new ArrayList<>();

        for (String recipientMessageGatewayAddress : request.getRecipientMessageGatewayAddresses()) {

            MessageGatewayAddress recipientMessageGateway = MessageGatewayAddress.fromString(recipientMessageGatewayAddress);

            Message message = Message.builder()
                    .senderMessageGatewayIdentifier(messageGateway.getIdentifier())
                    .senderNodeIdentifier(nodeIdentifier)
                    .senderActorAddress(actorAddress)
                    .topic(requestMessage.getTopic())
                    .headers(requestMessage.getHeaders())
                    .payload(requestMessage.getPayload())
                    .recipientVertexEndpoint(recipientMessageGateway.getNodeAddress().getVertexEndpoint())
                    .recipientNodeIdentifier(recipientMessageGateway.getNodeAddress().getNodeIdentifier())
                    .recipientMessageGatewayIdentifier(recipientMessageGateway.getMessageGatewayIdentifier())
                    .sendAt(sendAt)
                    .expiresAt(Instant.now().plus(messageGateway.getMessageExpirySeconds(), ChronoUnit.SECONDS))
                    .status(MessageStatus.QUEUED)
                    .build();

            messages.add(message);
        }

        messages = messageService.save(messages);
        return SendMessageResponse.builder().messages(messages).build();
    }
}
