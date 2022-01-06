package me.logwet.melange.commands.commands.divine.coordinate;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.logwet.melange.commands.arguments.coordinate.Coordinate;
import me.logwet.melange.commands.arguments.coordinate.CoordinateArgumentType;
import me.logwet.melange.commands.commands.divine.AbstractDivineCommand;
import me.logwet.melange.commands.source.CommandSource;
import me.logwet.melange.divine.provider.DivineProvider;

public abstract class AbstractMultiCoordinateCommand<T extends DivineProvider>
        extends AbstractDivineCommand<T> {
    private final Coordinate.Type[] coordinateTypes;

    public AbstractMultiCoordinateCommand(
            Coordinate.Type[] coordinateTypes, Class<T> clazz, String root, String... aliases) {
        super(clazz, root, aliases);
        this.coordinateTypes = coordinateTypes;
    }

    protected abstract int addWithCoordinateType(
            Coordinate.Type coordinateType, CommandContext<CommandSource> context);

    @Override
    protected LiteralArgumentBuilder<CommandSource> buildCommandTree(
            LiteralArgumentBuilder<CommandSource> rootLiteral) {
        LiteralArgumentBuilder<CommandSource> argumentBuilder = rootLiteral;

        for (Coordinate.Type coordinateType : coordinateTypes) {
            argumentBuilder =
                    argumentBuilder.then(
                            argument(
                                            coordinateType.toString(),
                                            CoordinateArgumentType.coordinate(coordinateType))
                                    .executes(c -> this.addWithCoordinateType(coordinateType, c)));
        }

        return argumentBuilder;
    }

    @Override
    public int add(CommandContext<CommandSource> context) {
        return 0;
    }
}
