package org.evernet.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.evernet.service.MessageSenderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageScheduler {

    private final MessageSenderService messageSenderService;

    @Scheduled(fixedRate = 1000)
    public void sendUnsentMessages() {
        try {
            messageSenderService.sendUnsent();
        } catch (Exception e) {
            log.error("Error sending messages", e);
        }
    }
}
