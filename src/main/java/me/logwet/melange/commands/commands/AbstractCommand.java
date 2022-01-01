package me.logwet.melange.commands.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Locale;
import me.logwet.melange.commands.source.CommandSource;

public abstract class AbstractCommand implements Command {
    protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(
            String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    /**
     * Returns a literal node that redirects its execution to the given destination node.
     *
     * <p>This method is taken from MIT licensed code in the Velocity project, see <a
     * href="https://github.com/VelocityPowered/Velocity/blob/b88c573eb11839a95bea1af947b0c59a5956368b/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L33">
     * Velocity's BrigadierUtils class</a>
     */
    protected static LiteralCommandNode<CommandSource> buildRedirect(
            String alias, LiteralCommandNode<CommandSource> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        LiteralArgumentBuilder<CommandSource> builder =
                LiteralArgumentBuilder.<CommandSource>literal(alias.toLowerCase(Locale.ROOT))
                        .requires(destination.getRequirement())
                        .forward(
                                destination.getRedirect(),
                                destination.getRedirectModifier(),
                                destination.isFork())
                        .executes(destination.getCommand());
        for (CommandNode<CommandSource> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(getBuilder());
    }
}
