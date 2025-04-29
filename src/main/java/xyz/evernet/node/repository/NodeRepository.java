package xyz.evernet.node.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.node.model.Node;

import java.util.List;
import java.util.Optional;

@Repository
public interface NodeRepository extends JpaRepository<Node, String> {

    boolean existsByIdentifier(String identifier);

    Optional<Node> findByIdentifier(String identifier);

    List<Node> findByOpen(Boolean open, Pageable pageable);
}
