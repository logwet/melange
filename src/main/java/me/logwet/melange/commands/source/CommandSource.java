package me.logwet.melange.commands.source;

import lombok.Getter;
import me.logwet.melange.commands.provider.CommandProvider.Type;

public interface CommandSource {
    String getUser();

    Role getRole();

    Type getType();

    enum Role {
        NONE(0),
        BROADCASTER(1),
        MOD(2),
        VIP(3),
        USER(4);

        @Getter private final int priority;

        Role(int priority) {
            this.priority = priority;
        }
    }
}
