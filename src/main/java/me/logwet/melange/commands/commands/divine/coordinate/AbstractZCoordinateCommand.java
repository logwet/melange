package me.logwet.melange.commands.commands.divine.coordinate;

import me.logwet.melange.commands.arguments.coordinate.Coordinate;
import me.logwet.melange.divine.provider.DivineProvider;

public abstract class AbstractZCoordinateCommand<T extends DivineProvider>
        extends AbstractCoordinateCommand<T> {
    public AbstractZCoordinateCommand(Class<T> clazz, String root, String... aliases) {
        super(Coordinate.Type.Z, clazz, root, aliases);
    }
}
