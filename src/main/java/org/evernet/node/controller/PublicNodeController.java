package org.evernet.node.controller;

import lombok.RequiredArgsConstructor;
import org.evernet.node.model.Node;
import org.evernet.node.service.NodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PublicNodeController {

    private final NodeService nodeService;

    @GetMapping("/nodes")
    public Page<Node> list(Pageable pageable) {
        return nodeService.list(pageable);
    }

    @GetMapping("/nodes/{identifier}")
    public Node get(@PathVariable String identifier) {
        return nodeService.get(identifier);
    }
}
