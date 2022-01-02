package me.logwet.melange.commands.commands.divine.coordinate;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.logwet.melange.commands.arguments.coordinate.Coordinate;
import me.logwet.melange.commands.arguments.coordinate.CoordinateArgumentType;
import me.logwet.melange.commands.commands.divine.AbstractDivineCommand;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.DivineProvider;

public abstract class AbstractCoordinateCommand<T extends DivineProvider>
        extends AbstractDivineCommand<T> {
    protected final Coordinate.Type coordinateType;

    public AbstractCoordinateCommand(
            Coordinate.Type coordinateType, Class<T> clazz, String root, String... aliases) {
        super(clazz, root, aliases);
        this.coordinateType = coordinateType;
    }

    @Override
    protected LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral) {
        return rootLiteral.then(
                argument(
                                coordinateType.toString(),
                                CoordinateArgumentType.coordinate(coordinateType))
                        .executes(this::add));
    }
}
