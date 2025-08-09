package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.exception.ClientException;
import org.evernet.exception.NotAllowedException;
import org.evernet.exception.NotFoundException;
import org.evernet.model.MessageType;
import org.evernet.model.MessageTypeHandler;
import org.evernet.repository.MessageTypeHandlerRepository;
import org.evernet.request.MessageTypeHandlerRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageTypeHandlerService {

    private final MessageTypeHandlerRepository messageTypeHandlerRepository;

    private final MessageTypeService messageTypeService;

    private final TransmitterService transmitterService;

    private final ReceiverService receiverService;

    public MessageTypeHandler create(MessageTypeHandlerRequest request, String nodeIdentifier, String actorAddress, String creator) {
        switch (request.getLocation()) {
            case RECEIVER -> {
                if (!receiverService.exists(nodeIdentifier, actorAddress)) {
                    throw new NotAllowedException();
                }
            }

            case TRANSMITTER -> {
                if (!transmitterService.exists(nodeIdentifier, actorAddress)) {
                    throw new NotAllowedException();
                }
            }
        }

        if (messageTypeHandlerRepository.existsByMessageTypeAddressAndLocationAndHandlerTypeAndHandlerAddressAndNodeIdentifierAndActorAddress(
                request.getMessageTypeAddress(),
                request.getLocation(),
                request.getHandlerType(),
                request.getHandlerAddress(),
                nodeIdentifier,
                actorAddress
        )) {
            throw new ClientException(String.format("Handler %s of type %s already exists for message type %s at %s for actor %s on node %s",
                    request.getHandlerAddress(),
                    request.getHandlerType(),
                    request.getMessageTypeAddress(),
                    request.getLocation(),
                    actorAddress,
                    nodeIdentifier));
        }

        MessageType messageType = messageTypeService.get(nodeIdentifier, request.getMessageTypeAddress());

        MessageTypeHandler messageTypeHandler = MessageTypeHandler.builder()
                .messageTypeAddress(messageType.getAddress())
                .location(request.getLocation())
                .handlerType(request.getHandlerType())
                .handlerAddress(request.getHandlerAddress())
                .nodeIdentifier(nodeIdentifier)
                .actorAddress(actorAddress)
                .creator(creator)
                .build();

        return messageTypeHandlerRepository.save(messageTypeHandler);
    }

    public List<MessageTypeHandler> list(String messageTypeAddress, MessageTypeHandler.Location location, String nodeIdentifier, String actorAddress) {
        return messageTypeHandlerRepository.findByMessageTypeAddressAndLocationAndNodeIdentifierAndActorAddress(
                messageTypeAddress,
                location,
                nodeIdentifier,
                actorAddress
        );
    }

    public MessageTypeHandler delete(MessageTypeHandlerRequest request, String nodeIdentifier, String actorAddress) {
        MessageTypeHandler messageTypeHandler = messageTypeHandlerRepository.findByMessageTypeAddressAndLocationAndHandlerTypeAndHandlerAddressAndNodeIdentifierAndActorAddress(
                request.getMessageTypeAddress(),
                request.getLocation(),
                request.getHandlerType(),
                request.getHandlerAddress(),
                nodeIdentifier,
                actorAddress
        );

        if (messageTypeHandler == null) {
            throw new NotFoundException(String.format("Handler %s of type %s not found for message type %s at %s for actor %s on node %s",
                    request.getHandlerAddress(),
                    request.getHandlerType(),
                    request.getMessageTypeAddress(),
                    request.getLocation(),
                    actorAddress,
                    nodeIdentifier));
        }

        messageTypeHandlerRepository.delete(messageTypeHandler);
        return messageTypeHandler;
    }
}
