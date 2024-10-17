package org.evernet.io.messaging.repository;

import org.evernet.io.messaging.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, String> {

}
