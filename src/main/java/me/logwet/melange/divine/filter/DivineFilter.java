package me.logwet.melange.divine.filter;

public interface DivineFilter {
    double getLb();

    double getUb();

    boolean test(double x, int s);

    Type getType();

    enum Type {
        ANGLE,
        DISTANCE
    }
}
