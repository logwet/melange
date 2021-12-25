package me.logwet.melange.divine.filter.distance;

public class RangeDistanceFilter extends AbstractDistanceFilter {
    protected final double lb;
    protected final double ub;

    public RangeDistanceFilter(double lb, double ub) {
        this.lb = lb;
        this.ub = ub;
    }

    @Override
    protected Policy getPolicy() {
        return Policy.INCLUDE;
    }

    @Override
    protected boolean tester(double x) {
        return lb <= x && x <= ub;
    }
}
