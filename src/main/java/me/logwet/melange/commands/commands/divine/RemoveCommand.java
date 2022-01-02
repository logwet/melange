package me.logwet.melange.commands.commands.divine;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.commands.commands.Command;
import me.logwet.melange.commands.commands.alias.AbstractAliasCommand;
import me.logwet.melange.commands.source.CommandSource;

public class RemoveCommand extends AbstractAliasCommand {
    public RemoveCommand() {
        super("remove", "delete");
    }

    @Override
    protected LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral) {
        for (Command command : CommandManager.COMMANDS) {
            if (command.getType() == Type.DIVINE) {
                rootLiteral.then(
                        command.getRootBuilder().executes(((DivineCommand) command)::remove));
            }
        }
        return rootLiteral;
    }
}
