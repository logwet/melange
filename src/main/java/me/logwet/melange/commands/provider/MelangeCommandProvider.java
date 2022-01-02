package me.logwet.melange.commands.provider;

import me.logwet.melange.commands.CommandManager;

public class MelangeCommandProvider extends AbstractCommandProvider {
    public MelangeCommandProvider(CommandManager commandManager) {
        super("", commandManager);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Type getType() {
        return Type.MELANGE;
    }

    @Override
    public void close() {}
}
