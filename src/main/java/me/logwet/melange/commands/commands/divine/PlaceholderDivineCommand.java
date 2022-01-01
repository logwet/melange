package me.logwet.melange.commands.commands.divine;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.feature.PlaceholderFeature;

public class PlaceholderDivineCommand extends AbstractDivineCommand<PlaceholderFeature> {
    public PlaceholderDivineCommand() {
        super(PlaceholderFeature.class, "placeholder", "pholder", "ph");
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

    @Override
    public int add(CommandContext<CommandSource> context) {
        return 0;
    }
}
