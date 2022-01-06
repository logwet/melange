package me.logwet.melange.render.divine;

import com.aparapi.Range;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.divine.filter.DivineFilter;
import me.logwet.melange.divine.filter.DivineFilter.Type;
import me.logwet.melange.divine.filter.angle.AngleFilter;
import me.logwet.melange.divine.filter.distance.DistanceFilter;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.kernel.api.StrongholdDataKernel;
import me.logwet.melange.util.StrongholdData;

public class RenderDivineKernel extends AbstractRingKernel implements StrongholdDataKernel {
    protected int strongholdCount;

    @Getter @Setter protected double[] output1;
    @Getter @Setter protected double[] output2;
    @Getter @Setter protected double[] output3;

    protected int hasProviders;
    protected int numAngleProviders;
    protected int numDistanceProviders;

    protected double[] angleLBs;
    protected double[] angleUBs;
    protected double[] distanceLBs;
    protected double[] distanceUBs;

    protected double[] guaranteeLength(double[] array) {
        if (array.length == 0) {
            return new double[1];
        }

        return array;
    }

    public void setup(List<DivineProvider> divineProviders, int strongholdCount) {
        this.strongholdCount = strongholdCount;

        this.output1 = new double[MelangeConstants.BUFFER_SIZE];
        this.output2 = new double[MelangeConstants.BUFFER_SIZE];
        this.output3 = new double[MelangeConstants.BUFFER_SIZE];

        List<AngleFilter> angleFilters = new ArrayList<>();
        List<DistanceFilter> distanceFilters = new ArrayList<>();

        this.numAngleProviders = 0;
        this.numDistanceProviders = 0;

        for (DivineProvider divineProvider : divineProviders) {
            boolean hasAngleFilter = false;
            boolean hasDistanceFilter = false;

            for (DivineFilter divineFilter : divineProvider.getFilters()) {
                if (divineFilter.getType() == Type.ANGLE) {
                    angleFilters.add((AngleFilter) divineFilter);
                    hasAngleFilter = true;
                } else if (divineFilter.getType() == Type.DISTANCE) {
                    distanceFilters.add((DistanceFilter) divineFilter);
                    hasDistanceFilter = true;
                }
            }

            if (hasAngleFilter) {
                this.numAngleProviders++;
            }
            if (hasDistanceFilter) {
                this.numDistanceProviders++;
            }
        }

        this.hasProviders = this.numAngleProviders + this.numDistanceProviders > 0 ? 1 : 0;

        this.angleLBs =
                guaranteeLength(
                        angleFilters.stream()
                                .map(DivineFilter::getLb)
                                .mapToDouble(d -> d)
                                .toArray());
        this.angleUBs =
                guaranteeLength(
                        angleFilters.stream()
                                .map(DivineFilter::getUb)
                                .mapToDouble(d -> d)
                                .toArray());
        this.distanceLBs =
                guaranteeLength(
                        distanceFilters.stream()
                                .map(DivineFilter::getLb)
                                .mapToDouble(d -> d)
                                .toArray());
        this.distanceUBs =
                guaranteeLength(
                        distanceFilters.stream()
                                .map(DivineFilter::getUb)
                                .mapToDouble(d -> d)
                                .toArray());
    }

    @Override
    protected void initializer() {
        strongholdCount = 0;

        output1 = new double[1];
        output2 = new double[1];
        output3 = new double[1];

        hasProviders = 0;
        numAngleProviders = 0;
        numDistanceProviders = 0;

        angleLBs = new double[1];
        angleUBs = new double[1];
        distanceLBs = new double[1];
        distanceUBs = new double[1];
    }

    @Override
    public void run() {
        int i = getGlobalId();

        if (strongholdCount > 0) {
            int x = calcX(i) - MelangeConstants.HALF_WIDTH;
            int y = calcY(i) - MelangeConstants.HALF_WIDTH;

            double r = calcMagnitude(x, y) * MelangeConstants.SCALING_FACTOR;

            if (r >= MelangeConstants.LOWER_BOUND && r <= MelangeConstants.UPPER_BOUND) {
                if (hasProviders == 1) {
                    int s = getPassId();

                    int ap = 0;

                    double t = calcAngle(x, y);

                    for (int j = 0; j < angleLBs.length; j++) {
                        double shift = MelangeConstants.TWO_PI_ON_THREE * s;
                        double l = normalizeAngle(angleLBs[j] + shift);
                        double u = normalizeAngle(angleUBs[j] + shift);

                        if (l < u) {
                            if (t >= l && t <= u) {
                                ap++;
                            }
                        } else {
                            if (t >= l || t <= u) {
                                ap++;
                            }
                        }
                    }

                    int dp = 0;

                    if (s == 0) {
                        for (int k = 0; k < distanceLBs.length; k++) {
                            if (r >= distanceLBs[k] && r <= distanceUBs[k]) {
                                dp++;
                            }
                        }
                    } else {
                        dp = numDistanceProviders;
                    }

                    if (ap >= numAngleProviders && dp >= numDistanceProviders) {
                        double p = sampleProbability(r);

                        if (s == 0) {
                            output1[i] = p;
                        } else {
                            if (s == 1) {
                                output2[i] = p;
                            } else {
                                output3[i] = p;
                            }
                        }
                    }
                } else {
                    output1[i] = sampleProbability(r);
                }
            }
        }
    }

    @Override
    public StrongholdData render() {
        this.setExplicit(true);

        int count = hasProviders > 0 ? strongholdCount : 1;

        this.execute(Range.create(MelangeConstants.BUFFER_SIZE), count);

        if (count >= 1) {
            this.get(output1);
        }
        if (count >= 2) {
            this.get(output2);
        }
        if (count >= 3) {
            this.get(output3);
        }

        return new StrongholdData(output1, output2, output3);
    }
}
