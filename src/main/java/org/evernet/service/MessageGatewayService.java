package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.MessageGateway;
import org.evernet.repository.MessageGatewayRepository;
import org.evernet.request.MessageGatewayCreationRequest;
import org.evernet.request.MessageGatewayUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageGatewayService {

    private final MessageGatewayRepository messageGatewayRepository;

    public MessageGateway create(MessageGatewayCreationRequest request, String nodeIdentifier, String actorAddress) {
        if (messageGatewayRepository.existsByNodeIdentifierAndIdentifier(nodeIdentifier, request.getIdentifier())) {
            throw new ClientException(String.format("Message gateway %s already exists on node %s", request.getIdentifier(), nodeIdentifier));
        }

        MessageGateway messageGateway = MessageGateway.builder()
                .nodeIdentifier(nodeIdentifier)
                .identifier(request.getIdentifier())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .messageExpirySeconds(request.getMessageExpirySeconds())
                .actorAddress(actorAddress)
                .build();

        return messageGatewayRepository.save(messageGateway);
    }

    public List<MessageGateway> list(String nodeIdentifier, String actorAddress, Pageable pageable) {
        return messageGatewayRepository.findByNodeIdentifierAndActorAddress(nodeIdentifier, actorAddress, pageable);
    }

    public MessageGateway get(String messageGatewayIdentifier, String nodeIdentifier, String actorAddress) {
        MessageGateway messageGateway = messageGatewayRepository.findByIdentifierAndNodeIdentifierAndActorAddress(messageGatewayIdentifier, nodeIdentifier, actorAddress);

        if (messageGateway == null) {
            throw new NotFoundException(String.format("Message gateway %s not found on node %s", messageGatewayIdentifier, nodeIdentifier));
        }

        return messageGateway;
    }

    public MessageGateway update(String messageGatewayIdentifier, MessageGatewayUpdateRequest request, String nodeIdentifier, String actorAddress) {
        MessageGateway messageGateway = get(messageGatewayIdentifier, nodeIdentifier, actorAddress);

        if (StringUtils.hasText(request.getDisplayName())) {
            messageGateway.setDisplayName(request.getDisplayName());
        }

        if (request.getMessageExpirySeconds() != null) {
            messageGateway.setMessageExpirySeconds(request.getMessageExpirySeconds());
        }

        messageGateway.setDescription(request.getDescription());
        return messageGatewayRepository.save(messageGateway);
    }

    public MessageGateway delete(String messageGatewayIdentifier, String nodeIdentifier, String actorAddress) {
        MessageGateway messageGateway = get(messageGatewayIdentifier, nodeIdentifier, actorAddress);
        messageGatewayRepository.delete(messageGateway);
        return messageGateway;
    }
}
