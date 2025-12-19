package ru.castroy10.hhbot.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.castroy10.hhbot.util.TimeUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final TokenService tokenService;
    private final MessageService messageService;
    @Value("${client.resume_id}")
    private String RESUME_ID;
    private final RestClient restClient = RestClient.create();

    @Scheduled(fixedDelay = 4, initialDelay = 1, timeUnit = TimeUnit.HOURS)
    public boolean updateResume() {
        if (!tokenService.isTokenValid()) {
            tokenService.getNewToken();
        }
        final ResponseEntity<String> response = restClient
                .post()
                .uri("https://api.hh.ru/resumes/{resumeId}/publish", RESUME_ID)
                .header("Authorization", "Bearer " + tokenService.getACCESS_TOKEN())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Resume updated successfully");
            if (TimeUtils.isWorkingTime() && !TimeUtils.isWeekend()) {
//                messageService.sendMessage("Resume updated successfully"); //TODO: убрать комментарий
            }
            return true;
        } else {
            log.error("Resume update failed");
//            messageService.sendMessage("Resume update failed");
            return false;
        }
    }

}
