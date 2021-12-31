package me.logwet.melange.parallelization.ring;

import me.logwet.melange.MelangeConstants;
import me.logwet.melange.parallelization.kernel.AbstractSharedKernel;
import org.apache.commons.math3.util.FastMath;

/**
 * PDF and CDF derived with the help of al.
 *
 * @author logwet
 * @author al
 */
public abstract class AbstractRingKernel extends AbstractSharedKernel {
    protected static final double A = MelangeConstants.LOWER_BOUND;
    protected static final double B = MelangeConstants.UPPER_BOUND;

    protected final double SQRT_A_ON_B = FastMath.sqrt(A / B);
    protected final double ONE_SUB_SQRT_A_ON_B = 1D - SQRT_A_ON_B;
    protected final double ONE_ON_SQRT_B = 1D / FastMath.sqrt(B);
    protected final double P_FAC = 0.5D / (ONE_SUB_SQRT_A_ON_B * B * ONE_ON_SQRT_B);

    protected int calcX(int i) {
        return i & MelangeConstants.X_MASK;
    }

    protected int calcY(int i) {
        return MelangeConstants.X_MASK - (i >> MelangeConstants.WIDTH_BITS);
    }

    protected double normalizeAngle(double x) {
        return x - MelangeConstants.TWO_PI * floor(x / MelangeConstants.TWO_PI);
    }

    protected double calcAngle(double x, double y) {
        return normalizeAngle(MelangeConstants.PI_ON_TWO - atan2(x, y));
    }

    protected double calcMagnitude(int x, int y) {
        return sqrt(x * x + y * y);
    }

    /**
     * Probability Density Function of the Stronghold's distance from 0,0.
     *
     * <p><img src = "../../doc-files/ring_pdf.png" />
     */
    protected double probabilityDensity(double x) {
        if (x >= A && x <= B) {
            return P_FAC / sqrt(x);
        }
        return 0D;
    }

    /**
     * Cumulative Density Function of the Stronghold's distance from 0,0.
     *
     * <p><img src = "../../doc-files/ring_cdf.png" />
     */
    protected double cumulativeDensity(double x) {
        if (x < A) {
            return 0D;
        } else if (x > B) {
            return 1D;
        }
        return (ONE_ON_SQRT_B * sqrt(x) - SQRT_A_ON_B) / ONE_SUB_SQRT_A_ON_B;
    }

    protected double sampleProbability(double x) {
        return cumulativeDensity(x + MelangeConstants.HALF_SCALING_FACTOR)
                - cumulativeDensity(x - MelangeConstants.HALF_SCALING_FACTOR);
    }
}
