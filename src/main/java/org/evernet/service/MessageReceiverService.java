package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedNode;
import org.evernet.model.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    public void receive(List<Message> messages) {
        // TODO
    }

    public void receive(List<Message> messages, AuthenticatedNode authenticatedNode) {
        List<Message> validMessages = new ArrayList<>();

        for (Message message : messages) {
            if (authenticatedNode.getNodeIdentifier().equals(message.getSenderNodeIdentifier()) && authenticatedNode.getNodeVertexEndpoint().equals(message.getSenderVertexEndpoint())) {
                validMessages.add(message);
            }
        }

        receive(validMessages);
    }
}
