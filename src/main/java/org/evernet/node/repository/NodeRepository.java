package org.evernet.node.repository;

import org.evernet.node.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {

    Boolean existsByIdentifier(String identifier);
}
