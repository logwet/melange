package me.logwet.melange.commands.source;

import lombok.EqualsAndHashCode;
import me.logwet.melange.commands.provider.CommandProvider.Type;
import me.logwet.melange.commands.provider.TwitchCommandProvider;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TwitchCommandSource implements CommandSource {
    @EqualsAndHashCode.Include private final String username;
    @EqualsAndHashCode.Include private final Role role;

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
}
