package me.logwet.melange.divine.filter;

public interface DivineFilter {
    Type getType();

    boolean test(double x, int index);

    enum Type {
        ANGLE,
        DISTANCE
    }
}
