package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.enums.MessageStatus;
import org.evernet.model.Message;
import org.evernet.repository.MessageRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private static final Object lock = new Object();

    public List<Message> save(List<Message> messages) {
        return messageRepository.saveAll(messages);
    }

    @Transactional
    public List<Message> peekUnsent(Pageable pageable) {
        synchronized (lock) {
            Instant now = Instant.now();
            List<Message> messages = messageRepository.findByStatusAndExpiresAtAfterAndSendAtBefore(MessageStatus.SCHEDULED, now, now, pageable);

            if (messages.isEmpty()) {
                return Collections.emptyList();
            }

            List<String> messageIds = messages.stream().map(Message::getId).toList();
            int result = messageRepository.lockAndMarkAsQueued(messageIds, now);

            if (result == 0) {
                return Collections.emptyList();
            }

            return messages;
        }
    }

    @Transactional
    public void updateSendStatus(String messageId, MessageStatus status) {
        messageRepository.updateSendStatus(messageId, status, Instant.now());
    }
}
