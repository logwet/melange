package me.logwet.melange.divine.filter.angle;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value(staticConstructor = "range")
public class RangeAngleFilter implements AngleFilter {
    @EqualsAndHashCode.Include String id = "angle";
    double lb;
    double ub;
}
