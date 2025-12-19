package ru.castroy10.hhbot.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelnetControlService extends AbstractControlService {

    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String EXIT = "exit";

    @Override
    protected int getPortOffset() {
        return 1;
    }

    @Override
    @SneakyThrows
    protected void handleClient(final Socket socket) {
        try (socket;
             final BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {
            output.println("Connected. Commands: start | stop | exit");
            String message;
            while ((message = input.readLine()) != null) {
                if (EXIT.equalsIgnoreCase(message)) {
                    output.println("Goodbye!");
                    break;
                }
                switch (message.toLowerCase()) {
                    case START -> {
                        blockingService.start();
                        output.println("Server started");
                    }
                    case STOP -> {
                        blockingService.shutdown();
                        output.println("Server stopped");
                    }
                    default -> output.println("Unknown command");
                }
            }
        }
    }

}
