package me.logwet.melange.divine.filter.angle;

import org.apache.commons.lang3.Range;
import org.apache.commons.math3.util.FastMath;

public class RangeAngleFilter extends AbstractAngleFilter {
    private static final double TWO_PI = 2 * FastMath.PI;
    protected final double lb;
    protected final double ub;
    protected final Range<Double> range;
    protected final Range<Double> range2;

    public RangeAngleFilter(double lb, double ub) {
        super(Policy.INCLUDE);

        this.lb = lb;
        this.ub = ub;

        this.range = Range.between(this.lb, this.ub);
        this.range2 = Range.between(this.lb, this.ub + TWO_PI);
    }

    @Override
    public boolean test(double x) {
        if (x < 0) {
            x += TWO_PI;
        }

        if (ub < lb) {
            return range2.contains(x);
        }

        return range.contains(x);
    }
}
