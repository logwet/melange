package me.logwet.melange.commands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.logwet.melange.commands.source.CommandSource;

public abstract class AbstractAliasedCommand extends AbstractCommand {
    protected final String root;
    protected final String[] aliases;

    protected AbstractAliasedCommand(String root, String... aliases) {
        this.root = root;
        this.aliases = aliases;
    }

    protected abstract LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral);

    @Override
    public LiteralArgumentBuilder<CommandSource> getBuilder() {
        return buildCommandTree(literal(root));
    }

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> node = getBuilder().build();

        dispatcher.getRoot().addChild(node);

        for (String alias : aliases) {
            dispatcher.getRoot().addChild(buildRedirect(alias, node));
        }
    }
}
