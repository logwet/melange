package me.logwet.melange.commands.commands.divine;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.commands.commands.AbstractCommand;
import me.logwet.melange.commands.commands.Command;
import me.logwet.melange.commands.source.CommandSource;

public class AddCommand extends AbstractCommand {
    @Override
    public LiteralArgumentBuilder<CommandSource> getBuilder() {
        LiteralArgumentBuilder<CommandSource> builder = literal("add");
        for (Command command : CommandManager.COMMANDS) {
            if (command.getType() == Type.DIVINE) {
                builder.then(command.getBuilder());
            }
        }
        return builder;
    }
}
