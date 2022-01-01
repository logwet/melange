package me.logwet.melange.commands.source;

import com.google.common.base.Objects;
import me.logwet.melange.commands.provider.CommandProvider.Type;

public class TwitchCommandSource implements CommandSource {
    private final String username;
    private final Role role;

    public TwitchCommandSource(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    @Override
    public String getUser() {
        return username;
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public Type getType() {
        return Type.TWITCH;
    }

    @Override
    public void sendSuccess(String message) {}

    @Override
    public void sendError(String message) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TwitchCommandSource that = (TwitchCommandSource) o;
        return Objects.equal(username, that.username) && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username, role);
    }
}
