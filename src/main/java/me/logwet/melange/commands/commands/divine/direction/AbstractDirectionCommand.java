package me.logwet.melange.commands.commands.divine.direction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.melange.commands.arguments.direction.DirectionArgumentType;
import me.logwet.melange.commands.commands.divine.AbstractDivineCommand;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.DivineProvider;

public abstract class AbstractDirectionCommand<T extends DivineProvider>
        extends AbstractDivineCommand<T> {
    public AbstractDirectionCommand(Class<T> clazz, String root, String... aliases) {
        super(clazz, root, aliases);
    }

    @Override
    protected LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral) {
        return rootLiteral.then(
                argument("direction", DirectionArgumentType.direction()).executes(this::add));
    }
}
