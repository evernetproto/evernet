package org.evernet.node.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.evernet.common.auth.AuthenticatedAdminController;
import org.evernet.node.model.Node;
import org.evernet.node.request.NodeCreationRequest;
import org.evernet.node.service.NodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NodeController extends AuthenticatedAdminController {

    private final NodeService nodeService;

    @PostMapping("/nodes")
    public Node create(@Valid @RequestBody NodeCreationRequest request) throws Exception {
        return nodeService.create(request, getAdminIdentifier());
    }
}
