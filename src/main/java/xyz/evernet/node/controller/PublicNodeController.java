package xyz.evernet.node.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.evernet.node.model.Node;
import xyz.evernet.node.service.NodeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PublicNodeController {

    private final NodeService nodeService;

    @GetMapping("/nodes")
    public List<Node> list(Pageable pageable) {
        return nodeService.listOpen(pageable);
    }

    @GetMapping("/nodes/{identifier}")
    public Node get(@PathVariable String identifier) {
        return nodeService.get(identifier);
    }
}
