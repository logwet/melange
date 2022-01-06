package me.logwet.melange.divine.filter.distance;

import lombok.EqualsAndHashCode;
import lombok.Value;
import me.logwet.melange.MelangeConstants;

@Value(staticConstructor = "range")
public class RangeDistanceFilter implements DistanceFilter {
    @EqualsAndHashCode.Include String id = "distance";
    double lb;
    double ub;

    public static RangeDistanceFilter slice(int n, int i) {
        return new RangeDistanceFilter(
                ((MelangeConstants.UPPER_BOUND - MelangeConstants.LOWER_BOUND) / n) * i
                        + MelangeConstants.LOWER_BOUND,
                ((MelangeConstants.UPPER_BOUND - MelangeConstants.LOWER_BOUND) / n) * (i + 1)
                        + MelangeConstants.LOWER_BOUND);
    }

    @Override
    public boolean test(double x, int s) {
        if (s == 0) {
            return x >= lb && x <= ub;
        }

        return true;
    }
}
