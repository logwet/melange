package me.logwet.melange.divine.provider.feature;

import me.logwet.melange.commands.arguments.coordinate.Coordinate;
import me.logwet.melange.commands.arguments.coordinate.Coordinate.Type;

public class FossilFeatureProvider extends DecoratorFeatureProvider {
    private FossilFeatureProvider(long salt, int xLb, int xUb, int zLb, int zUb) {
        super(salt, xLb, xUb, zLb, zUb);
    }

    public static FossilFeatureProvider build(Coordinate coordinate) {
        @SuppressWarnings("ConstantConditions")
        int xLb = coordinate.getX();
        int xUb = xLb + 1;
        int zLb;
        int zUb;

        if (coordinate.getType() == Type.XZ) {
            //noinspection ConstantConditions
            zLb = coordinate.getZ();
            zUb = zLb + 1;
        } else {
            zLb = 0;
            zUb = 16;
        }

        return new FossilFeatureProvider(0, xLb, xUb, zLb, zUb);
    }
}
