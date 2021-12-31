package me.logwet.melange.divine.filter.distance;

import lombok.Getter;

public class RangeDistanceFilter implements DistanceFilter {
    @Getter protected final double lb;
    @Getter protected final double ub;

    public RangeDistanceFilter(double lb, double ub) {
        this.lb = lb;
        this.ub = ub;
    }
}
