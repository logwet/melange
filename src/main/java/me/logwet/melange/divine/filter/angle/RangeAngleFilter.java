package me.logwet.melange.divine.filter.angle;

import lombok.Getter;

public class RangeAngleFilter implements AngleFilter {
    @Getter protected final double lb;
    @Getter protected final double ub;

    public RangeAngleFilter(double lb, double ub) {
        this.lb = lb;
        this.ub = ub;
    }
}
