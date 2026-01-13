package com.example;

import java.io.File;
import java.util.function.Consumer;

public interface NtfyConnection {

    public boolean send(String message);

    default boolean sendFile(File file) {
        throw new UnsupportedOperationException("File sending not supported.");
    }


    public void receive(Consumer<NtfyMessageDto> messageHandler);

}