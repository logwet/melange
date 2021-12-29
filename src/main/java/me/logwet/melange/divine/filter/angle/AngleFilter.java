package me.logwet.melange.divine.filter.angle;

import me.logwet.melange.divine.filter.DivineFilter;

public interface AngleFilter extends DivineFilter {
    @Override
    default Type getType() {
        return Type.ANGLE;
    }

    @Override
    default int getTypeI() {
        return 0;
    }
}
