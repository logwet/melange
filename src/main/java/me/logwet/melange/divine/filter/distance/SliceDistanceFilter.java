package me.logwet.melange.divine.filter.distance;

import me.logwet.melange.math.RingDensity;

public class SliceDistanceFilter extends RangeDistanceFilter {
    public SliceDistanceFilter(int n, int i) {
        super(getLB(n, i), getUB(n, i));
    }

    protected static double getLB(int n, int i) {
        return ((RingDensity.UPPER_BOUND - RingDensity.LOWER_BOUND) / n) * i
                + RingDensity.LOWER_BOUND;
    }

    protected static double getUB(int n, int i) {
        return ((RingDensity.UPPER_BOUND - RingDensity.LOWER_BOUND) / n) * (i + 1)
                + RingDensity.LOWER_BOUND;
    }
}
