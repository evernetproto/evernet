package org.evernet.actor.repository;

import org.evernet.actor.model.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {

    Boolean existsByIdentifierAndNodeIdentifier(String identifier, String nodeIdentifier);
}
