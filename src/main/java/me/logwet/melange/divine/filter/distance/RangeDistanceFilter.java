package me.logwet.melange.divine.filter.distance;

import lombok.Getter;

public class RangeDistanceFilter extends AbstractDistanceFilter {
    @Getter protected final double lb;
    @Getter protected final double ub;

    public RangeDistanceFilter(double lb, double ub) {
        this.lb = lb;
        this.ub = ub;
    }

    @Override
    protected Policy getPolicy() {
        return Policy.INCLUDE;
    }

    @Override
    protected boolean tester(double x, int index) {
        if (index == 0) {
            return lb <= x && x <= ub;
        } else {
            return true;
        }
    }
}
