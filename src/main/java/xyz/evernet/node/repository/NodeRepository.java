package xyz.evernet.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.node.model.Node;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {

    boolean existsByIdentifier(String identifier);
}
