package ru.castroy10.hhbot.service;

import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.springframework.stereotype.Service;
import ru.castroy10.hhbot.model.TokensConfig;

@Service
@Slf4j
public class FileService {

    private final String CONFIG_FILE_NAME = "hhbot.cfg";
    private final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";
    private final String REFRESH_TOKEN_KEY = "REFRESH_TOKEN";

    public void saveTokens(final String accessToken, final String refreshToken) {
        try {
            final Properties properties = new Properties();
            properties.setProperty(ACCESS_TOKEN_KEY, accessToken);
            properties.setProperty(REFRESH_TOKEN_KEY, refreshToken);

            final Path configPath = Paths.get(CONFIG_FILE_NAME).toAbsolutePath();
            try (final OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "HH Bot Configuration - Token Storage");
                log.info("Tokens saved successfully to {}", configPath);
            }
        } catch (final Exception e) {
            log.error("Failed to save tokens to config file", e);
        }
    }

    public TokensConfig loadTokens() {
        try {
            final Path configPath = Paths.get(CONFIG_FILE_NAME).toAbsolutePath();
            if (!Files.exists(configPath)) {
                log.info("Config file {} does not exist", configPath);
                return null;
            }

            final Properties properties = new Properties();
            try (final InputStream input = Files.newInputStream(configPath)) {
                properties.load(input);
            }

            final String accessToken = properties.getProperty(ACCESS_TOKEN_KEY);
            final String refreshToken = properties.getProperty(REFRESH_TOKEN_KEY);
            if (accessToken != null && refreshToken != null) {
                log.info("Tokens loaded successfully from {}", configPath);
                return new TokensConfig(accessToken, refreshToken);
            } else {
                log.warn("Tokens not found in config file {}", configPath);
                return null;
            }
        } catch (final Exception e) {
            log.error("Failed to load tokens from config file", e);
            return null;
        }
    }

}