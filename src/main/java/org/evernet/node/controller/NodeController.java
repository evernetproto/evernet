package org.evernet.node.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.common.auth.AuthenticatedAdminController;
import org.evernet.node.model.Node;
import org.evernet.node.request.NodeCreationRequest;
import org.evernet.node.request.NodeUpdateRequest;
import org.evernet.node.service.NodeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController extends AuthenticatedAdminController {

    private final NodeService nodeService;

    @PostMapping("/nodes")
    public Node create(@Valid @RequestBody NodeCreationRequest request) throws Exception {
        return nodeService.create(request, getAdminIdentifier());
    }

    @PutMapping("/nodes/{identifier}")
    public Node update(@PathVariable String identifier, @Valid @RequestBody NodeUpdateRequest request) {
        return nodeService.update(identifier, request);
    }

    @PutMapping("/nodes/{identifier}/signing-keys")
    public Node resetSigningKeyPair(@PathVariable String identifier) throws Exception {
        return nodeService.resetSigningKeyPair(identifier);
    }

    @DeleteMapping("/nodes/{identifier}")
    public Node delete(@PathVariable String identifier) {
        return nodeService.delete(identifier);
    }
}
