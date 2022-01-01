package me.logwet.melange.commands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.logwet.melange.commands.source.CommandSource;

public abstract class AbstractAliasedCommand extends AbstractCommand {
    protected final String[] aliases;

    public AbstractAliasedCommand(String root, String... aliases) {
        super(root);
        this.aliases = aliases;
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
