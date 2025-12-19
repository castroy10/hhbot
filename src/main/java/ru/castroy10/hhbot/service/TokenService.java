package ru.castroy10.hhbot.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.castroy10.hhbot.model.HHTokenResponse;
import ru.castroy10.hhbot.model.TokensConfig;

@Service
@Slf4j
public class TokenService {

    @Value("${client.id}")
    private String CLIENT_ID;
    @Value("${client.secret}")
    private String CLIENT_SECRET;
    @Getter
    private String ACCESS_TOKEN;
    @Getter
    private String REFRESH_TOKEN;
    private final RestClient restClient = RestClient.create();
    private final FileService fileService;

    public TokenService(final FileService fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        final TokensConfig tokensConfig = fileService.loadTokens();
        if (tokensConfig != null) {
            this.ACCESS_TOKEN = tokensConfig.getAccessToken();
            this.REFRESH_TOKEN = tokensConfig.getRefreshToken();
            log.info("Tokens read successfully from configuration file");
        }
    }

    @SneakyThrows
    public boolean getInitToken(final String authCode) {
        final HHTokenResponse hhTokenResponse = restClient
                .post()
                .uri("https://api.hh.ru/token?grant_type=authorization_code&client_id={clientId}&client_secret={clientSecret}&code={authToken}&redirect_uri=https://ya.ru",
                     CLIENT_ID, CLIENT_SECRET, authCode)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(HHTokenResponse.class)
                .getBody();
        extractTokens(hhTokenResponse);
        log.info("Init token получен: {}", ACCESS_TOKEN);
        return true;
    }

    @SneakyThrows
    public void getNewToken() {
        final HHTokenResponse hhTokenResponse = restClient
                .post()
                .uri("https://api.hh.ru/token?grant_type=refresh_token&refresh_token={refreshToken}", REFRESH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(HHTokenResponse.class)
                .getBody();
        extractTokens(hhTokenResponse);
        log.info("Новые токены получены: access {}, refresh {}", ACCESS_TOKEN, REFRESH_TOKEN);
    }

    public boolean isTokenValid() {
        final ResponseEntity<String> entity;
        try {
            entity = restClient
                    .get()
                    .uri("https://api.hh.ru/me")
                    .header("Authorization", "Bearer " + ACCESS_TOKEN)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatus.FORBIDDEN)) {
                log.info("Token is not valid, need to get new token");
                return false;
            }
        } catch (final Exception e) {
            log.error("Failed to check token", e);
            return true;
        }
        return true;
    }

    public String getAuthUrl() {
        return "https://hh.ru/oauth/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri=https://ya.ru";
    }

    private void extractTokens(final HHTokenResponse hhTokenResponse) {
        try {
            ACCESS_TOKEN = hhTokenResponse.getAccessToken();
            REFRESH_TOKEN = hhTokenResponse.getRefreshToken();
        } catch (final Exception e) {
            log.error("Failed to extract tokens", e);
            throw new RuntimeException(e);
        }
        fileService.saveTokens(ACCESS_TOKEN, REFRESH_TOKEN);
    }

}
