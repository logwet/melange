package me.logwet.melange.render.divine;

import me.logwet.melange.MelangeConstants;
import me.logwet.melange.util.ArrayHelper;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class DivineHelper {
    private static final double A = MelangeConstants.LOWER_BOUND;
    private static final double B = MelangeConstants.UPPER_BOUND;

    private static final double SQRT_A_ON_B = FastMath.sqrt(A / B);
    private static final double ONE_SUB_SQRT_A_ON_B = 1D - SQRT_A_ON_B;
    private static final double ONE_ON_SQRT_B = 1D / FastMath.sqrt(B);

    private static final double P_FAC = 0.5D / (ONE_SUB_SQRT_A_ON_B * B * ONE_ON_SQRT_B);

    private DivineHelper() {}

    /**
     * Probability Density Function of the Stronghold's distance from 0,0.
     *
     * <p><img src = "../../doc-files/ring_pdf.png" />
     */
    private static double probabilityDensity(double x) {
        if (x >= A && x <= B) {
            return P_FAC / FastMath.sqrt(x);
        }
        return 0D;
    }

    /**
     * Cumulative Density Function of the Stronghold's distance from 0,0.
     *
     * <p><img src = "../../doc-files/ring_cdf.png" />
     */
    private static double cumulativeDensity(double x) {
        if (x < A) {
            return 0D;
        } else if (x > B) {
            return 1D;
        }
        return (ONE_ON_SQRT_B * FastMath.sqrt(x) - SQRT_A_ON_B) / ONE_SUB_SQRT_A_ON_B;
    }

    private static double sampleProbability(double x) {
        return cumulativeDensity(x + MelangeConstants.HALF_SCALING_FACTOR)
                - cumulativeDensity(x - MelangeConstants.HALF_SCALING_FACTOR);
    }

    private static int clamp(int x, int mn, int mx) {
        return FastMath.min(FastMath.max(x, mn), mx);
    }

    private static int constrainToBounds(int x) {
        return FastMath.min(FastMath.max(x, 0), MelangeConstants.WIDTH - 1);
    }

    public static double getAngle(double x, double y) {
        return MathUtils.normalizeAngle(FastMath.atan2(y, x), MelangeConstants.PI);
    }

    public static double getMagnitude(int x, int y) {
        return FastMath.sqrt(x * x + y * y);
    }

    public static double[] buildDefaultBuffer() {
        double[] output = new double[MelangeConstants.BUFFER_SIZE];

        for (int i = 0; i < MelangeConstants.BUFFER_SIZE; i++) {
            double r =
                    getMagnitude(ArrayHelper.getX(i), ArrayHelper.getY(i))
                            * MelangeConstants.SCALING_FACTOR;

            if (r >= MelangeConstants.LOWER_BOUND && r <= MelangeConstants.UPPER_BOUND) {
                output[i] = sampleProbability(r);
            }
        }

        return output;
    }
}
