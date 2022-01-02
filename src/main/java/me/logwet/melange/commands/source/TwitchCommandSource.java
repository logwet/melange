package me.logwet.melange.commands.source;

import com.google.common.base.Objects;
import me.logwet.melange.commands.provider.CommandProvider.Type;
import me.logwet.melange.commands.provider.TwitchCommandProvider;

public class TwitchCommandSource implements CommandSource {
    private final String username;
    private final Role role;
    private final TwitchCommandProvider provider;

    public TwitchCommandSource(String username, Role role, TwitchCommandProvider provider) {
        this.username = username;
        this.role = role;
        this.provider = provider;
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

    private void sendMessage(String message) {
        if (this.provider.isEnabled()) {
            assert this.provider.getTwitchClient() != null;
            this.provider
                    .getTwitchClient()
                    .getChat()
                    .sendMessage(this.provider.getChannelName(), message);
        }
    }

    @Override
    public void sendSuccess(String message) {
        sendMessage(message);
    }

    @Override
    public void sendError(String message) {
        sendMessage("[ERROR] " + message);
    }

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
