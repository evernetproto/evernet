package org.evernet.node.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.core.auth.AuthenticatedAdminController;
import org.evernet.node.model.Node;
import org.evernet.node.request.NodeCreationRequest;
import org.evernet.node.request.NodeUpdateRequest;
import org.evernet.node.service.NodeService;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController extends AuthenticatedAdminController {

    private final NodeService nodeService;

    @PostMapping("/nodes")
    public Node create(@Valid @RequestBody NodeCreationRequest request) throws NoSuchAlgorithmException {
        return nodeService.create(request, getAdmin());
    }

    @PutMapping("/nodes/{identifier}")
    public Node update(@PathVariable String identifier, @Valid @RequestBody NodeUpdateRequest request) {
        return nodeService.update(identifier, request);
    }

    @DeleteMapping("/nodes/{identifier}")
    public Node delete(@PathVariable String identifier) {
        return nodeService.delete(identifier);
    }

    @PutMapping("/nodes/{identifier}/signing-keys")
    public Node resetSigningKeys(@PathVariable String identifier) throws NoSuchAlgorithmException {
        return nodeService.resetSigningKeys(identifier);
    }
}
