package me.logwet.melange.parallelization.ring;

import com.aparapi.Range;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.divine.filter.DivineFilter;
import me.logwet.melange.divine.filter.angle.RangeAngleFilter;
import me.logwet.melange.divine.filter.distance.RangeDistanceFilter;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.util.DoubleBuffer;

public class DivineFilterKernel extends AbstractRingKernel {
    protected static final int STRONGHOLD_COUNT = 3;

    protected int enabled;

    @Getter @Setter protected double[] output2;
    @Getter @Setter protected double[] output3;

    protected double[] angleLBs;
    protected double[] angleUBs;
    protected double[] distanceLBs;
    protected double[] distanceUBs;

    public void setup(ImmutableList<DivineProvider> divineProviders) {
        this.enabled = 1;

        input = new double[MelangeConstants.BUFFER_SIZE];
        output2 = new double[MelangeConstants.BUFFER_SIZE];
        output3 = new double[MelangeConstants.BUFFER_SIZE];

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
        enabled = 0;

        output2 = new double[1];
        output3 = new double[1];

        angleLBs = new double[1];
        angleUBs = new double[1];
        distanceLBs = new double[1];
        distanceUBs = new double[1];
    }

    @Override
    public void run() {
        if (enabled > 0) {
            int i = getGlobalId();

            int x = calcX(i);
            int y = calcY(i);

            double r = calcMagnitude(x, y) * MelangeConstants.SCALING_FACTOR;

            if (r >= MelangeConstants.LOWER_BOUND && r <= MelangeConstants.UPPER_BOUND) {
                int f = 0;

                int s = getPassId();

                boolean accept = false;

                for (int k = 0; k < angleLBs.length; k++) {
                    double t = calcAngle(x, y);

                    double shift = MelangeConstants.TWO_PI_ON_THREE * s;
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

                if (s == 0) {
                    for (int l = 0; l < distanceLBs.length; l++) {
                        if (!(r >= distanceLBs[l] && r <= distanceUBs[l])) {
                            accept = false;
                        }
                    }
                }

                if (accept) {
                    f++;
                }

                if (f > 0) {
                    double a = sampleProbability(r) * f;
                    if (s == 0) {
                        input[i] = a;
                    } else if (s == 1) {
                        output2[i] = a;
                    } else {
                        output3[i] = a;
                    }
                }
            }
        }
    }

    @Override
    public void initialize() {
        this.setupForInitialization();
        super.initialize();
    }

    @Override
    protected void executeHelper() {
        Range range = Range.create(MelangeConstants.BUFFER_SIZE);
        this.setExplicit(true);
        this.execute(range, 3);
        this.get(input).get(output2).get(output3);
    }

    @Override
    public double[] execute() {
        this.executeHelper();

        if (enabled > 0) {
            DoubleBuffer buffer = new DoubleBuffer(input);
            DoubleBuffer buffer2 = new DoubleBuffer(output2);
            DoubleBuffer buffer3 = new DoubleBuffer(output3);

            buffer.normalizeSumInPlace();
            buffer2.normalizeSumInPlace();
            buffer3.normalizeSumInPlace();

            buffer.addInPlace(buffer2, buffer3);

            return buffer.getBuffer();
        }
        return new double[0];
    }
}
