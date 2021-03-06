package me.logwet.melange.commands.commands.divine.placeholder;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.commands.divine.AbstractDivineCommand;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.feature.PlaceholderFeatureProvider;

public class PlaceholderDivineCommand extends AbstractDivineCommand<PlaceholderFeatureProvider> {
    public PlaceholderDivineCommand() {
        super(PlaceholderFeatureProvider.class, "placeholder", "pholder", "ph");
    }

    @Override
    protected LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral) {
        return rootLiteral
                .then(literal("test1").executes(this::add))
                .then(
                        literal("test2")
                                .executes(context -> 1)
                                .then(literal("test3").executes(context -> 1)));
    }

    @Override
    public int add(CommandContext<CommandSource> context) {
        this.addAndWait(new PlaceholderFeatureProvider());
        return 1;
    }
}
