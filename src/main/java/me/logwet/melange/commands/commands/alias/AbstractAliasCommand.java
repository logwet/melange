package me.logwet.melange.commands.commands.alias;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.Getter;
import me.logwet.melange.commands.commands.AbstractCommand;
import me.logwet.melange.commands.source.CommandSource;

public abstract class AbstractAliasCommand extends AbstractCommand {
    @Getter protected final String[] aliases;

    public AbstractAliasCommand(String root, String... aliases) {
        super(root);
        this.aliases = aliases;
    }

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> node = getBuilder().build();

        dispatcher.getRoot().addChild(node);

        for (String alias : AliasManager.getAliases(root)) {
            dispatcher.getRoot().addChild(buildRedirect(alias, node));
        }
    }
}
