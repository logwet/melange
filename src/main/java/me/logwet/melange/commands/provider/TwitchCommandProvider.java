package me.logwet.melange.commands.provider;

import ch.qos.logback.classic.Logger;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.mojang.brigadier.StringReader;
import java.util.Locale;
import java.util.Objects;
import lombok.Getter;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.commands.source.CommandSource.Role;
import me.logwet.melange.commands.source.TwitchCommandSource;
import me.logwet.melange.config.Config;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

public class TwitchCommandProvider extends AbstractCommandProvider {
    protected static final Logger LOGGER =
            (Logger) LoggerFactory.getLogger(TwitchCommandProvider.class);

    @Nullable @Getter protected final TwitchClient twitchClient;
    protected final boolean isConnected;

    @Nullable @Getter protected final String channelName;
    @Nullable protected final String userName;

    public TwitchCommandProvider(String prefix, CommandManager commandManager) {
        super(prefix, commandManager);

        String oauthToken = (String) Config.getProperty("twitch_oauth_token");
        channelName = (String) Config.getProperty("twitch_channel_name");
        userName = (String) Config.getProperty("twitch_user_name");

        if (Objects.nonNull(channelName) && !channelName.isEmpty()) {
            TwitchClientBuilder builder =
                    TwitchClientBuilder.builder()
                            .withEnableHelix(true)
                            .withEnableKraken(false)
                            .withEnableChat(true);

            if (Objects.nonNull(oauthToken)
                    && !oauthToken.isEmpty()
                    && Objects.nonNull(userName)
                    && !userName.isEmpty()) {
                OAuth2Credential credential = new OAuth2Credential("twitch", oauthToken);
                builder.withChatAccount(credential);
            }

            twitchClient = builder.build();

            twitchClient.getChat().joinChannel(channelName);

            twitchClient
                    .getChat()
                    .getEventManager()
                    .onEvent(ChannelMessageEvent.class, this::onMessage);

            isConnected = true;
        } else {
            isConnected = false;
            twitchClient = null;
        }
    }

    protected void onMessage(ChannelMessageEvent event) {
        if (event.getMessage().startsWith(getPrefix())) {
            LOGGER.info(
                    "["
                            + event.getChannel().getName()
                            + "] "
                            + event.getUser().getName()
                            + ": "
                            + event.getMessage());

            StringReader stringReader =
                    new StringReader(StringUtils.removeStart(event.getMessage(), getPrefix()));

            if (stringReader.canRead()) {
                Role role = null;
                int currentPriority = Integer.MAX_VALUE;
                for (CommandPermission commandPermission : event.getPermissions()) {
                    Role tempRole = Role.fromCommandPermission(commandPermission);
                    if (Objects.nonNull(tempRole)) {
                        if (tempRole.getPriority() < currentPriority) {
                            role = tempRole;
                        }
                    }
                }
                if (Objects.isNull(role)) {
                    role = Role.EVERYONE;
                }

                Role minRole = Role.getMinRole();

                CommandSource source =
                        new TwitchCommandSource(event.getUser().getName(), role, this);

                if (role.getPriority() <= minRole.getPriority()) {
                    LOGGER.info("Processing stringReader " + stringReader.getString());
                    commandManager.execute(stringReader, source);
                } else {
                    source.sendError(
                            "@"
                                    + source.getUser()
                                    + " you don't have minimum role "
                                    + minRole.name().toLowerCase(Locale.ROOT)
                                    + " to perform this command");
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (isConnected) {
            assert twitchClient != null;
            return twitchClient.getChat().isChannelJoined(channelName);
        }

        return false;
    }

    @Override
    public Type getType() {
        return Type.TWITCH;
    }

    @Override
    public void close() {
        if (Objects.nonNull(twitchClient)) {
            twitchClient.close();
        }
    }
}
