package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.MessageGateway;
import org.evernet.request.MessageGatewayCreationRequest;
import org.evernet.request.MessageGatewayUpdateRequest;
import org.evernet.service.MessageGatewayService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageGatewayController extends AuthenticatedActorController {

    private final MessageGatewayService messageGatewayService;

    @PostMapping("/messaging/gateways")
    public MessageGateway create(@Valid @RequestBody MessageGatewayCreationRequest request) {
        return messageGatewayService.create(request, getTargetNodeIdentifier(), getActorAddress());
    }

    @GetMapping("/messaging/gateways")
    public List<MessageGateway> list(Pageable pageable) {
        return messageGatewayService.list(getTargetNodeIdentifier(), getActorAddress(), pageable);
    }

    @GetMapping("/messaging/gateways/{messageGatewayIdentifier}")
    public MessageGateway get(@PathVariable String messageGatewayIdentifier) {
        return messageGatewayService.get(messageGatewayIdentifier, getTargetNodeIdentifier(), getActorAddress());
    }

    @PutMapping("/messaging/gateways/{messageGatewayIdentifier}")
    public MessageGateway update(@PathVariable String messageGatewayIdentifier, @Valid @RequestBody MessageGatewayUpdateRequest request) {
        return messageGatewayService.update(messageGatewayIdentifier, request, getTargetNodeIdentifier(), getActorAddress());
    }

    @DeleteMapping("/messaging/gateways/{messageGatewayIdentifier}")
    public MessageGateway delete(@PathVariable String messageGatewayIdentifier) {
        return messageGatewayService.delete(messageGatewayIdentifier, getTargetNodeIdentifier(), getActorAddress());
    }
}
