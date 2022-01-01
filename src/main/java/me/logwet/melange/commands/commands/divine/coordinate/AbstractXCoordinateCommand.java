package me.logwet.melange.commands.commands.divine.coordinate;

import me.logwet.melange.commands.arguments.coordinate.Coordinate;
import me.logwet.melange.divine.provider.DivineProvider;

public abstract class AbstractXCoordinateCommand<T extends DivineProvider>
        extends AbstractCoordinateCommand<T> {
    public AbstractXCoordinateCommand(Class<T> clazz, String root, String... aliases) {
        super(Coordinate.Type.X, clazz, root, aliases);
    }
}
