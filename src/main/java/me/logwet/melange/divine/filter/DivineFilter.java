package me.logwet.melange.divine.filter;

import java.util.List;

public interface DivineFilter {
    Type getType();

    Policy getPolicy();

    List<Integer> getTargets();

    boolean test(double x);

    enum Type {
        ANGLE,
        DISTANCE
    }

    enum Policy {
        INCLUDE,
        EXCLUDE
    }
}
