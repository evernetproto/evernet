package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.request.SendMessageRequest;
import org.evernet.response.SendMessageResponse;
import org.evernet.service.MessageSenderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageSenderController extends AuthenticatedActorController {

    private final MessageSenderService messageSenderService;

    @PostMapping("/messaging/gateways/{messageGatewayIdentifier}/send")
    public SendMessageResponse send(@Valid @RequestBody SendMessageRequest request, @PathVariable String messageGatewayIdentifier) {
        return messageSenderService.queue(messageGatewayIdentifier, request, getTargetNodeIdentifier(), getActorAddress());
    }
}
