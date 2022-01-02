package me.logwet.melange.commands.source;

import com.github.twitch4j.common.enums.CommandPermission;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.logwet.melange.Melange;
import me.logwet.melange.commands.provider.CommandProvider.Type;
import me.logwet.melange.config.Config;
import org.jetbrains.annotations.Nullable;

public interface CommandSource {
    String getUser();

    Role getRole();

    Type getType();

    void sendSuccess(String message);

    void sendError(String message);

    @RequiredArgsConstructor
    enum Role {
        OWNER(0, CommandPermission.OWNER),
        BROADCASTER(1, CommandPermission.BROADCASTER),
        MODERATOR(2, CommandPermission.MODERATOR),
        VIP(3, CommandPermission.VIP),
        FOUNDER(4, CommandPermission.FOUNDER),
        SUBSCRIBER(5, CommandPermission.SUBSCRIBER),
        EVERYONE(6, CommandPermission.EVERYONE);

        private static final ImmutableMap<Integer, Role> PRIORITY_ROLE_MAP;
        private static final ImmutableMap<CommandPermission, Role> COMMAND_PERMISSION_ROLE_MAP;

        static {
            Builder<Integer, Role> builder1 = ImmutableMap.builder();
            Builder<CommandPermission, Role> builder2 = ImmutableMap.builder();

            for (Role role : Role.values()) {
                builder1.put(role.priority, role);
                builder2.put(role.commandPermission, role);
            }

            PRIORITY_ROLE_MAP = builder1.build();
            COMMAND_PERMISSION_ROLE_MAP = builder2.build();
        }

        @Getter private final int priority;
        private final CommandPermission commandPermission;

        @Nullable
        public static Role fromPriority(int priority) {
            return PRIORITY_ROLE_MAP.get(priority);
        }

        @Nullable
        public static Role fromCommandPermission(CommandPermission commandPermission) {
            return COMMAND_PERMISSION_ROLE_MAP.get(commandPermission);
        }

        public static Role getMinRole() {
            Role minRole = null;

            try {
                int priority =
                        Melange.submit(() -> (int) Config.getProperty("twitch_min_role")).get();
                minRole = fromPriority(priority);
            } catch (InterruptedException | ExecutionException e) {
                Melange.LOGGER.error("Unable to get min role property", e);
            }

            if (Objects.isNull(minRole)) {
                minRole = Role.BROADCASTER;
            }

            return minRole;
        }
    }
}
