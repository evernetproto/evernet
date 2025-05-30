package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedNodeController;
import org.evernet.request.MessageReceiverRequest;
import org.evernet.response.MessageReceiverResponse;
import org.evernet.service.MessageReceiverService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageReceiverController extends AuthenticatedNodeController {

    private final MessageReceiverService messageReceiverService;

    @PostMapping("/messaging/receive")
    public MessageReceiverResponse receive(@Valid @RequestBody MessageReceiverRequest request) {
        return messageReceiverService.receive(request.getMessages(), getNode());
    }
}
