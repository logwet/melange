package me.logwet.melange.divine.filter;

public interface DivineFilter {
    double getLb();

    double getUb();

    Type getType();

    enum Type {
        ANGLE,
        DISTANCE
    }
}
