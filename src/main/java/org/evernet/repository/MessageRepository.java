package org.evernet.repository;

import org.evernet.enums.MessageStatus;
import org.evernet.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findByStatusAndExpiresAtAfterAndSendAtBefore(MessageStatus status, Instant expiresAtAfter, Instant sendAtBefore, Pageable pageable);

    @Modifying
    @Query("UPDATE Message m SET m.status = 'QUEUED', m.queuedAt = :now WHERE m.id IN :ids AND m.status = 'SCHEDULED'")
    int lockAndMarkAsQueued(@Param("ids") List<String> ids, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE Message m SET m.status = :status, m.sendAttemptedAt = :now WHERE m.id = :id")
    void updateSendStatus(@Param("id") String id, @Param("status") MessageStatus status, @Param("now") Instant now);
}
