package me.logwet.melange.parallelization.ring;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.logwet.melange.divine.filter.DivineFilter;
import me.logwet.melange.divine.filter.angle.RangeAngleFilter;
import me.logwet.melange.divine.filter.distance.RangeDistanceFilter;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.math.RingDensity;
import me.logwet.melange.parallelization.kernel.AbstractCopyArrayKernel;
import org.apache.commons.math3.util.FastMath;

public class DivineFilterKernel extends AbstractCopyArrayKernel {
    protected static final int STRONGHOLD_COUNT = 3;

    protected static final double PI = FastMath.PI;
    protected static final double TWO_PI = 2 * PI;
    protected static final double TWO_PI_ON_THREE = TWO_PI / 3;
    protected static final double PI_ON_TWO = PI / 2;

    protected int width;
    protected int width_2;
    protected double scalingFactor;
    protected int xMask;
    protected int widthBits;

    protected double[] angleLBs;
    protected double[] angleUBs;
    protected double[] distanceLBs;
    protected double[] distanceUBs;

    public void setup(int width, ImmutableList<DivineProvider> divineProviders) {
        this.width = width;
        this.width_2 = width / 2;

        this.scalingFactor = RingDensity.SEARCH_SIZE / (double) width_2;

        this.xMask = width - 1;
        this.widthBits = Integer.bitCount(this.xMask);

        List<RangeAngleFilter> angleFilters = new ArrayList<>();
        List<RangeDistanceFilter> distanceFilters = new ArrayList<>();

        List<DivineFilter> divineFilters =
                divineProviders.stream()
                        .flatMap(p -> p.getFilters().stream())
                        .collect(Collectors.toList());

        for (DivineFilter divineFilter : divineFilters) {
            if (divineFilter instanceof RangeAngleFilter) {
                angleFilters.add((RangeAngleFilter) divineFilter);
            } else if (divineFilter instanceof RangeDistanceFilter) {
                distanceFilters.add((RangeDistanceFilter) divineFilter);
            }
        }

        this.angleLBs =
                angleFilters.stream().map(RangeAngleFilter::getLb).mapToDouble(d -> d).toArray();
        this.angleUBs =
                angleFilters.stream().map(RangeAngleFilter::getUb).mapToDouble(d -> d).toArray();
        this.distanceLBs =
                distanceFilters.stream()
                        .map(RangeDistanceFilter::getLb)
                        .mapToDouble(d -> d)
                        .toArray();
        this.distanceUBs =
                distanceFilters.stream()
                        .map(RangeDistanceFilter::getUb)
                        .mapToDouble(d -> d)
                        .toArray();
    }

    protected void setupForInitialization() {
        width = 0;
        width_2 = 0;
        scalingFactor = 0;
        xMask = 0;
        widthBits = 0;

        angleLBs = new double[0];
        angleUBs = new double[0];
        distanceLBs = new double[0];
        distanceUBs = new double[0];
    }

    protected double normalizeAngle(double a) {
        return a - TWO_PI * floor(a / TWO_PI);
    }

    protected double getAngle(double x, double y) {
        return normalizeAngle(-atan2(x, y) + PI_ON_TWO);
    }

    protected double getMagnitude(double x, double y) {
        return sqrt(x * x + y * y);
    }

    @Override
    public void run() {
        if (width > 0) {
            int i = getGlobalId();

            int x = (i & xMask) - width_2;
            int y = (i >> widthBits) - width_2;

            int f = 0;

            for (int j = 0; j < STRONGHOLD_COUNT; j++) {
                boolean accept = false;

                for (int k = 0; k < angleLBs.length; k++) {
                    double t = getAngle(x, y);

                    double shift = TWO_PI_ON_THREE * j;
                    double l = normalizeAngle(angleLBs[k] + shift);
                    double u = normalizeAngle(angleUBs[k] + shift);

                    if (l < u) {
                        if (t >= l && t <= u) {
                            accept = true;
                        }
                    } else {
                        if (t >= l || t <= u) {
                            accept = true;
                        }
                    }
                }

                if (j == 0) {
                    for (int l = 0; l < distanceLBs.length; l++) {
                        double t = getMagnitude(x, y) * scalingFactor;

                        if (!(t >= distanceLBs[l] && t <= distanceUBs[l])) {
                            accept = false;
                        }
                    }
                }

                if (accept) {
                    f++;
                }
            }

            if (f > 0) {
                output[i] = input[i] * f;
            }
        }
    }

    @Override
    public void initialize() {
        this.setupForInitialization();
        super.initialize();
    }
}
