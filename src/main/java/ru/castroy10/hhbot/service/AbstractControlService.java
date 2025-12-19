package ru.castroy10.hhbot.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractControlService {

    @Value("${server.port}")
    protected Integer BASE_PORT;
    protected static final InetAddress SERVER_HOST = InetAddress.getLoopbackAddress();
    @Autowired
    protected BlockingService blockingService;
    protected final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    protected Thread serverThread;
    
    protected abstract int getPortOffset();
    protected abstract void handleClient(final Socket socket);
    
    @PostConstruct
    public void init() {
        final int actualPort = BASE_PORT + getPortOffset();
        serverThread = Thread.ofVirtual().start(() -> startServer(actualPort));
    }

    @SneakyThrows
    private void startServer(final int port) {
        try (final ServerSocket serverSocket = new ServerSocket(port, 0, SERVER_HOST)) {
            while (!Thread.currentThread().isInterrupted()) {
                final Socket socket = serverSocket.accept();
                executorService.submit(() -> handleClient(socket));
            }
        }
    }

    @SneakyThrows
    @PreDestroy
    public void destroy() {
        serverThread.interrupt();
        executorService.shutdown();
        final boolean bool = executorService.awaitTermination(5, TimeUnit.SECONDS);
        log.info("Server {} stopped {}", Thread.currentThread().getName(), bool);
    }

}