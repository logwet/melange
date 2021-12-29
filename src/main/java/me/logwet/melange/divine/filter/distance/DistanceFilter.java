package me.logwet.melange.divine.filter.distance;

import me.logwet.melange.divine.filter.DivineFilter;

public interface DistanceFilter extends DivineFilter {
    @Override
    default Type getType() {
        return Type.DISTANCE;
    }

    @Override
    default int getTypeI() {
        return 1;
    }
}
