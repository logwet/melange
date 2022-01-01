package me.logwet.melange.commands.provider;

import com.mojang.brigadier.CommandDispatcher;
import me.logwet.melange.commands.source.CommandSource;

public abstract class AbstractCommandProvider implements CommandProvider {
    private final String prefix;

    public AbstractCommandProvider(String prefix, CommandDispatcher<CommandSource> dispatcher) {
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getName() {
        return getType().getName();
    }
}
