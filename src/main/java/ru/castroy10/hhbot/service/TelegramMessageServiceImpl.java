package ru.castroy10.hhbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramMessageServiceImpl implements MessageService {

    private final RestClient restClient = RestClient.create();

    @Override
    @Async
    public void sendMessage(final String message) {
        restClient
                .get()
                .uri("http://localhost:8081/api/v1/tgbot/send?message=", message)
                .retrieve()
                .toEntity(String.class);
        log.info("Message sent to Telegram successfully");
    }

}