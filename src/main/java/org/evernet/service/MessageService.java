package org.evernet.service;

import lombok.RequiredArgsConstructor;
import org.evernet.model.Message;
import org.evernet.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> save(List<Message> messages) {
        return messageRepository.saveAll(messages);
    }
}
