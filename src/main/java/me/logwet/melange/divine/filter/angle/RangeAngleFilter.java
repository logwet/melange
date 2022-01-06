package me.logwet.melange.divine.filter.angle;

import lombok.EqualsAndHashCode;
import lombok.Value;
import me.logwet.melange.MelangeConstants;
import org.apache.commons.math3.util.MathUtils;

@Value(staticConstructor = "range")
public class RangeAngleFilter implements AngleFilter {
    @EqualsAndHashCode.Include String id = "angle";
    double lb;
    double ub;

    public static RangeAngleFilter slice(int n, int i) {
        double arc = MelangeConstants.TWO_PI / n;
        return new RangeAngleFilter(arc * i, arc * (i + 1));
    }

    @Override
    public boolean test(double x, int s) {
        double shift = MelangeConstants.TWO_PI_ON_THREE * s;
        double l = MathUtils.normalizeAngle(lb + shift, MelangeConstants.PI);
        double u = MathUtils.normalizeAngle(ub + shift, MelangeConstants.PI);

        if (l < u) {
            return x >= l && x <= u;
        } else {
            return x >= l || x <= u;
        }
    }
}
