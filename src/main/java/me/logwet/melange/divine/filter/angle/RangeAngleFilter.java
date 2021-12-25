package me.logwet.melange.divine.filter.angle;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class RangeAngleFilter extends AbstractAngleFilter {
    private static final double TWO_PI_ON_THREE = MathUtils.TWO_PI / 3;

    protected final double lb;
    protected final double ub;

    public RangeAngleFilter(double lb, double ub) {
        this.lb = lb;
        this.ub = ub;
    }

    @Override
    protected Policy getPolicy() {
        return Policy.INCLUDE;
    }

    @Override
    protected boolean tester(double x, int index) {
        double shift = TWO_PI_ON_THREE * index;
        double l = MathUtils.normalizeAngle(lb + shift, FastMath.PI);
        double u = MathUtils.normalizeAngle(ub + shift, FastMath.PI);

        if (l < u) {
            return l <= x && x <= u;
        } else {
            return l <= x || x <= u;
        }
    }
}
