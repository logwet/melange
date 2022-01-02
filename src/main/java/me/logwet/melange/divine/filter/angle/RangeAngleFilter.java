package me.logwet.melange.divine.filter.angle;

import lombok.Value;

@Value(staticConstructor = "range")
public class RangeAngleFilter implements AngleFilter {
    String id = "angle";
    double lb;
    double ub;
}
