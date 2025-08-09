package org.evernet.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.evernet.model.MessageTypeHandler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageTypeHandlerRepository extends JpaRepository<MessageTypeHandler, String> {

    boolean existsByMessageTypeAddressAndLocationAndHandlerTypeAndHandlerAddressAndNodeIdentifierAndActorAddress(@NotBlank(message = "Message type address is required") String messageTypeAddress, MessageTypeHandler.@NotNull(message = "Message type handler location is required") Location location, MessageTypeHandler.@NotNull(message = "Handler type is required") HandlerType handlerType, @NotBlank(message = "Handler address is required") String handlerAddress, String nodeIdentifier, String actorAddress);

    List<MessageTypeHandler> findByMessageTypeAddressAndLocationAndNodeIdentifierAndActorAddress(String messageTypeAddress, MessageTypeHandler.Location location, String nodeIdentifier, String actorAddress);

    MessageTypeHandler findByMessageTypeAddressAndLocationAndHandlerTypeAndHandlerAddressAndNodeIdentifierAndActorAddress(String messageTypeAddress, MessageTypeHandler.Location location, MessageTypeHandler.HandlerType handlerType, String handlerAddress, String nodeIdentifier, String actorAddress);
}
