package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedActorController;
import org.evernet.model.MessageTypeHandler;
import org.evernet.request.MessageTypeHandlerRequest;
import org.evernet.service.MessageTypeHandlerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messaging")
@RequiredArgsConstructor
public class MessageTypeHandlerController extends AuthenticatedActorController {

    private final MessageTypeHandlerService messageTypeHandlerService;

    @PostMapping("/handlers")
    public MessageTypeHandler create(@Valid @RequestBody MessageTypeHandlerRequest request) {
        return messageTypeHandlerService.create(
                request,
                getActorNodeIdentifier(),
                getActorAddress(),
                null
        );
    }

    @GetMapping("/handlers")
    public List<MessageTypeHandler> list(@RequestParam String messageTypeAddress,
                                         @RequestParam MessageTypeHandler.Location location) {
        return messageTypeHandlerService.list(
                messageTypeAddress,
                location,
                getTargetNodeIdentifier(),
                getActorAddress()
        );
    }

    @DeleteMapping("/handlers")
    public MessageTypeHandler delete(@Valid @RequestBody MessageTypeHandlerRequest request) {
        return messageTypeHandlerService.delete(request, getTargetNodeIdentifier(), getActorAddress());
    }
}
