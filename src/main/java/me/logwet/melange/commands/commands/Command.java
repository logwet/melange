package me.logwet.melange.commands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.melange.commands.source.CommandSource;

public interface Command {
    LiteralArgumentBuilder<CommandSource> getBuilder();

    void register(CommandDispatcher<CommandSource> dispatcher);

    default Type getType() {
        return Type.OTHER;
    }

    enum Type {
        DIVINE,
        OTHER
    }
}
