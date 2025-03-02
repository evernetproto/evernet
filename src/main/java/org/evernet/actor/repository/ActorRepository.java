package org.evernet.actor.repository;

import org.evernet.actor.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {

    Boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);

    Optional<Actor> findByNodeIdentifierAndIdentifier(String nodeIdentifier, String identifier);
}
