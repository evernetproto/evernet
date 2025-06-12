package org.evernet.repository;

import org.evernet.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    User findByNodeIdentifierAndIdentifier(String nodeIdentifier, String identifier);

    List<User> findByNodeIdentifier(String nodeIdentifier, Pageable pageable);
}
