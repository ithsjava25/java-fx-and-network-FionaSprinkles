package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class NtfyConnectionImpl implements NtfyConnection {

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();
    private final String hostName;

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

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                //todo lägg in since-kod för att ta emot meddelanden sedan sist
                .uri(URI.create(hostName + "/mytopic/json"))
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
                        .forEach(messageHandler));
    }
}
