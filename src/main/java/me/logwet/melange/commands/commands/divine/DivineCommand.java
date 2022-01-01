package me.logwet.melange.commands.commands.divine;

import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.commands.Command;
import me.logwet.melange.commands.source.CommandSource;

public interface DivineCommand extends Command {
    int add(CommandContext<CommandSource> context);

    int remove(CommandContext<CommandSource> context);
}
