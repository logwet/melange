package me.logwet.melange.divine.filter.distance;

import java.util.List;
import org.apache.commons.lang3.Range;

public class RangeDistanceFilter extends AbstractDistanceFilter {
    protected final double lb;
    protected final double ub;
    protected final Range<Double> range;

    public RangeDistanceFilter(List<Integer> targets, double lb, double ub) {
        super(Policy.INCLUDE, targets);

        this.lb = lb;
        this.ub = ub;

        this.range = Range.between(this.lb, this.ub);
    }

    @Override
    public boolean test(double x) {
        return range.contains(x);
    }
}
