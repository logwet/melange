package me.logwet.melange.commands.provider;

import lombok.Getter;

public interface CommandProvider {
    String getName();

    String getPrefix();

    Type getType();

    enum Type {
        MELANGE("Melange"),
        TWITCH("Twitch");

        @Getter private final String name;

        Type(String name) {
            this.name = name;
        }
    }
}
