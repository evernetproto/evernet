package org.evernet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.auth.AuthenticatedAdminController;
import org.evernet.model.MessageTypeHandler;
import org.evernet.request.MessageTypeHandlerRequest;
import org.evernet.service.MessageTypeHandlerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins/nodes/{nodeIdentifier}/messaging")
@RequiredArgsConstructor
public class MessageTypeHandlerAdminController extends AuthenticatedAdminController {

    private final MessageTypeHandlerService messageTypeHandlerService;

    @PostMapping("/handlers")
    public MessageTypeHandler create(@Valid @RequestBody MessageTypeHandlerRequest request,
                                     @RequestParam String actorAddress,
                                     @PathVariable String nodeIdentifier) {
        return messageTypeHandlerService.create(
                request,
                nodeIdentifier,
                actorAddress,
                getAdminIdentifier()
        );
    }

    @GetMapping("/handlers")
    public List<MessageTypeHandler> list(@RequestParam String messageTypeAddress,
                                         @RequestParam MessageTypeHandler.Location location,
                                         @RequestParam String actorAddress,
                                         @PathVariable String nodeIdentifier) {
        return messageTypeHandlerService.list(
                messageTypeAddress,
                location,
                nodeIdentifier,
                actorAddress
        );
    }

    @DeleteMapping("/handlers")
    public MessageTypeHandler delete(@PathVariable String nodeIdentifier,
                                     @RequestParam String actorAddress,
                                     @Valid @RequestBody MessageTypeHandlerRequest request) {
        return messageTypeHandlerService.delete(request, nodeIdentifier, actorAddress);
    }
}
