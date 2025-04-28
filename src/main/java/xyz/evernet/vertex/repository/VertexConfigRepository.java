package xyz.evernet.vertex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.evernet.vertex.model.VertexConfig;

@Repository
public interface VertexConfigRepository extends JpaRepository<VertexConfig, String> {

    boolean existsByKey(String key);
}
