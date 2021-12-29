package me.logwet.melange.divine.filter.distance;

import me.logwet.melange.MelangeConstants;
import me.logwet.melange.parallelization.ring.AbstractRingKernel;

public class SliceDistanceFilter extends RangeDistanceFilter {
    public SliceDistanceFilter(int n, int i) {
        super(getLB(n, i), getUB(n, i));
    }

    protected static double getLB(int n, int i) {
        return ((MelangeConstants.UPPER_BOUND - MelangeConstants.LOWER_BOUND) / n) * i
                + MelangeConstants.LOWER_BOUND;
    }

    protected static double getUB(int n, int i) {
        return ((MelangeConstants.UPPER_BOUND - MelangeConstants.LOWER_BOUND) / n) * (i + 1)
                + MelangeConstants.LOWER_BOUND;
    }
}
