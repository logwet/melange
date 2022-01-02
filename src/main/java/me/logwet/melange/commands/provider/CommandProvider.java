package me.logwet.melange.commands.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface CommandProvider extends AutoCloseable {
    boolean isEnabled();

    String getName();

    String getPrefix();

    Type getType();

    @RequiredArgsConstructor
    enum Type {
        MELANGE("Melange"),
        TWITCH("Twitch");

        @Getter private final String name;
    }
}
