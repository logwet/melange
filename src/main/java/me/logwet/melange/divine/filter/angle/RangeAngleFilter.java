package me.logwet.melange.divine.filter.angle;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class RangeAngleFilter extends AbstractAngleFilter {
    protected final double lb;
    protected final double ub;

    public RangeAngleFilter(double lb, double ub) {
        super(Policy.INCLUDE);

        this.lb = MathUtils.normalizeAngle(lb, FastMath.PI);
        this.ub = MathUtils.normalizeAngle(ub, FastMath.PI);
    }

    @Override
    public boolean test(double x) {
        if (lb < ub) {
            return lb <= x && x <= ub;
        } else {
            return lb <= x || x <= ub;
        }
    }
}
