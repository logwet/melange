package me.logwet.melange.commands.commands.divine;

import me.logwet.melange.commands.commands.AbstractAliasedCommand;

public abstract class AbstractDivineCommand extends AbstractAliasedCommand {
    protected AbstractDivineCommand(String root, String... aliases) {
        super(root, aliases);
    }

    @Override
    public Type getType() {
        return Type.DIVINE;
    }
}
