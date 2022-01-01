package me.logwet.melange.commands.commands.divine;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.melange.commands.source.CommandSource;

public class TestDivineCommand extends AbstractDivineCommand {
    public TestDivineCommand() {
        super("treasure", "tsure", "ts");
    }

    @Override
    protected LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral) {
        return rootLiteral
                .then(literal("test1").executes(context -> 1))
                .then(
                        literal("test2")
                                .executes(context -> 1)
                                .then(literal("test3").executes(context -> 1)));
    }
}
