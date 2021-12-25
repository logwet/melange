package me.logwet.melange.divine.filter.distance;

public class SliceDistanceFilter extends RangeDistanceFilter {
    public SliceDistanceFilter(int n, int i, double lb, double ub) {
        super(getLB(n, i, lb, ub), getUB(n, i, lb, ub));
    }

    protected static double getLB(int n, int i, double lb, double ub) {
        return ((ub - lb) / n) * i + lb;
    }

    protected static double getUB(int n, int i, double lb, double ub) {
        return ((ub - lb) / n) * (i + 1) + lb;
    }
}
