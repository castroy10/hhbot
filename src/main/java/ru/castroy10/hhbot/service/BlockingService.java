package ru.castroy10.hhbot.service;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockingService {

    private static final String UNBLOCK_PATH = "/api/v1/hhbot/unblock";
    @Setter
    private boolean isBlocked = false;
    private final ObjectProvider<ServletWebServerApplicationContext> contextProvider;

    public synchronized void shutdown() {
        manipulateWebServer(webServer -> {
            webServer.stop();
            log.info("Server shutdown from telnet/http");
        });
    }

    public synchronized void start() {
        manipulateWebServer(webServer -> {
            webServer.start();
            log.info("Server started from telnet/http");
        });
    }

    private void manipulateWebServer(final Consumer<WebServer> serverConsumer) {
        final ServletWebServerApplicationContext context = contextProvider.getIfAvailable();
        if (context != null) {
            final WebServer webServer = context.getWebServer();
            serverConsumer.accept(webServer);
        }
    }

}
