package me.logwet.melange.commands.provider;

import me.logwet.melange.commands.CommandManager;

public abstract class AbstractCommandProvider implements CommandProvider {
    protected final CommandManager commandManager;
    private final String prefix;

    public AbstractCommandProvider(String prefix, CommandManager commandManager) {
        this.prefix = prefix;
        this.commandManager = commandManager;
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
