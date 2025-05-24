package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedNode;
import org.evernet.model.Message;
import org.evernet.response.MessageReceiverResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    public MessageReceiverResponse receive(List<Message> messages) {
        // TODO
        return MessageReceiverResponse.builder().build();
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
