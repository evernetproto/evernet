package xyz.evernet.node.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import xyz.evernet.core.auth.AuthenticatedAdminController;
import xyz.evernet.node.model.Node;
import xyz.evernet.node.request.NodeCreationRequest;
import xyz.evernet.node.request.NodeUpdateRequest;
import xyz.evernet.node.service.NodeService;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController extends AuthenticatedAdminController {

    private final NodeService nodeService;

    @PostMapping("/nodes")
    public Node create(@Valid @RequestBody NodeCreationRequest request) throws NoSuchAlgorithmException {
        return nodeService.create(request, getAdminIdentifier());
    }

    @GetMapping("/nodes/all")
    public List<Node> listAll(Pageable pageable) {
        return nodeService.listAll(pageable);
    }

    @PutMapping("/nodes/{identifier}")
    public Node update(@PathVariable String identifier, @Valid @RequestBody NodeUpdateRequest request) {
        return nodeService.update(identifier, request);
    }

    @PutMapping("/nodes/{identifier}/signing-key")
    public Node resetSigningKey(@PathVariable String identifier) throws NoSuchAlgorithmException {
        return nodeService.resetSigningKey(identifier);
    }

    @DeleteMapping("/nodes/{identifier}")
    public Node delete(@PathVariable String identifier) {
        return nodeService.delete(identifier);
    }
}
