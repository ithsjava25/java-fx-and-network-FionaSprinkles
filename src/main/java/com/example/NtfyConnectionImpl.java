package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Path;
import java.nio.file.Files;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.function.Consumer;

public class NtfyConnectionImpl implements NtfyConnection {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;

    private final Path lastIdFile = Path.of("last_message_id.txt");

    public NtfyConnectionImpl() {
        Dotenv dotenv = Dotenv.load();
        hostName = Objects.requireNonNull(dotenv.get("HOST_NAME"));
    }

    public NtfyConnectionImpl(String hostName) {
        this.hostName = hostName;
    }


    @Override
    public boolean send(String message) {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .uri(URI.create(hostName + "/mytopic"))
                .build();
        try {
            //Todo handle long blocking send requests to not freeze the JavaFX thread

            var response = http.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (IOException e) {
            System.out.println("Error sending message");
        } catch (InterruptedException e) {
            System.out.println("Interrupted sending message");
        }
        return false;
    }

    @Override
    public void receive(Consumer<NtfyMessageDto> messageHandler) {

        String since = loadLastId();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(hostName + "/mytopic/json?since=" + since))
                .build();

        http.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> response.body()
                        .map(line -> {
                            try {
                                return mapper.readValue(line, NtfyMessageDto.class);
                            } catch (JsonProcessingException e) {
                                return null;
                            }
                        })
                        .filter(msg -> msg != null && msg.event().equals("message"))
                        .forEach(msg -> {
                            messageHandler.accept(msg);
                            saveLastId(msg.id());
                        }));
    }
private String loadLastId() {
    try {
        if (Files.exists(lastIdFile)) {
            return Files.readString(lastIdFile).trim();
        }
    } catch (IOException e) {
    }
    return "all";
}
private void saveLastId(String id) {
    try {
        Files.writeString(lastIdFile, id);
    } catch (IOException e) {}
}

}