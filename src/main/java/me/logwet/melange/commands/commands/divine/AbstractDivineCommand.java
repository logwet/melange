package me.logwet.melange.commands.commands.divine;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.Melange;
import me.logwet.melange.commands.commands.AbstractAliasedCommand;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.DivineProvider;

public abstract class AbstractDivineCommand<T extends DivineProvider> extends AbstractAliasedCommand
        implements DivineCommand {
    protected Class<T> clazz;

    protected AbstractDivineCommand(Class<T> clazz, String root, String... aliases) {
        super(root, aliases);
        this.clazz = clazz;
    }

    @Override
    public int remove(CommandContext<CommandSource> context) {
        boolean removed = false;

        for (DivineProvider provider : ImmutableList.copyOf(Melange.providerList)) {
            if (clazz.isInstance(provider)) {
                Melange.providerList.remove(provider);
                removed = true;
            }
        }

        String name = clazz.getSimpleName();

        if (removed) {
            context.getSource().sendSuccess("Removed all instances of " + name);

        } else {
            context.getSource().sendError("No instances of " + name + " to remove!");
        }

        return 1;
    }

    @Override
    public Type getType() {
        return Type.DIVINE;
    }
}
