package me.logwet.melange.commands.provider;

import com.mojang.brigadier.CommandDispatcher;
import me.logwet.melange.commands.source.CommandSource;

public class TwitchCommandProvider extends AbstractCommandProvider {
    public TwitchCommandProvider(String prefix, CommandDispatcher<CommandSource> dispatcher) {
        super(prefix, dispatcher);
    }

    @Override
    public Type getType() {
        return Type.TWITCH;
    }
}
