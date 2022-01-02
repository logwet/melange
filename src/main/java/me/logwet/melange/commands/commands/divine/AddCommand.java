package me.logwet.melange.commands.commands.divine;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.commands.commands.Command;
import me.logwet.melange.commands.commands.alias.AbstractAliasCommand;
import me.logwet.melange.commands.source.CommandSource;

public class AddCommand extends AbstractAliasCommand {
    public AddCommand() {
        super("add", "put");
    }

    @Override
    protected LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral) {
        for (Command command : CommandManager.COMMANDS) {
            if (command.getType() == Type.DIVINE) {
                rootLiteral.then(command.getBuilder());
            }
        }
        return rootLiteral;
    }
}
