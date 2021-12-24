package me.logwet.melange.math;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import me.logwet.melange.math.RingDensity.Caches.AngleCacheLoader;
import me.logwet.melange.math.RingDensity.Caches.AngleToLengthCacheLoader;
import me.logwet.melange.math.RingDensity.Caches.CumulativeDensityCacheLoader;
import me.logwet.melange.math.RingDensity.Caches.DensityCacheLoader;
import me.logwet.melange.math.RingDensity.Caches.MagnitudeCacheLoader;
import org.apache.commons.math3.util.FastMath;

/**
 * This class contains code used to sample the Stronghold ring's density. The methods use {@link
 * Caffeine}'s {@link LoadingCache} and a {@link CacheLoader} to cache previously generated values.
 * Each of the caches will automatically evict entries if they exceed ~5MB in size so the total
 * theoretical maximum memory usage of this code is 30MB, however under the current config it will
 * never exceed ~1.59MB in total.
 *
 * <p>PDF and CDF derived with the help of al.
 *
 * @author logwet
 * @author al
 */
public class RingDensity {
    public static final double LOWER_BOUND = 1280;
    public static final double UPPER_BOUND = 2816;
    private static final long MAX_CACHE_SIZE = 5 * 8 * 1024 * 1024 / 64;
    private static final double PI_ON_2 = FastMath.PI / 2;
    private static final double PI_ON_4 = PI_ON_2 / 2;
    private static final double THREE_PI_ON_4 = 3 * PI_ON_4;
    private static final double SQRT_2 = FastMath.sqrt(2);
    /** 0 entries */
    private static final LoadingCache<Double, Double> DENSITY_CACHE;
    /** 21851 entries */
    private static final LoadingCache<Double, Double> CUMULATIVE_DENSITY_CACHE;

    @Getter(lazy = true)
    private static final double maxProbability = genMaxProbability();
    /** 22026 entries */
    private static final LoadingCache<Double, Double> MAGNITUDE_CACHE;
    /** 142416 entries */
    private static final LoadingCache<Double[], Double> ANGLE_CACHE;
    /** 0 entries */
    private static final LoadingCache<Double, Double> ANGLE_TO_LENGTH_CACHE;

    static {
        DENSITY_CACHE =
                Caffeine.newBuilder().maximumSize(MAX_CACHE_SIZE).build(new DensityCacheLoader());
        CUMULATIVE_DENSITY_CACHE =
                Caffeine.newBuilder()
                        .maximumSize(MAX_CACHE_SIZE)
                        .build(new CumulativeDensityCacheLoader());
        MAGNITUDE_CACHE =
                Caffeine.newBuilder().maximumSize(MAX_CACHE_SIZE).build(new MagnitudeCacheLoader());
        ANGLE_CACHE =
                Caffeine.newBuilder().maximumSize(MAX_CACHE_SIZE).build(new AngleCacheLoader());
        ANGLE_TO_LENGTH_CACHE =
                Caffeine.newBuilder()
                        .maximumSize(MAX_CACHE_SIZE)
                        .build(new AngleToLengthCacheLoader());
    }

    public static double getDensity(double x) {
        if (x >= LOWER_BOUND && x <= UPPER_BOUND) {
            //noinspection ConstantConditions
            return DENSITY_CACHE.get(x);
        }
        return 0D;
    }

    public static double getCumulativeDensity(double x) {
        if (x < LOWER_BOUND) {
            return 0D;
        } else if (x > UPPER_BOUND) {
            return 1D;
        }
        //noinspection ConstantConditions
        return CUMULATIVE_DENSITY_CACHE.get(x);
    }

    public static double getProbability(double x0, double x1) {
        return getCumulativeDensity(x1) - getCumulativeDensity(x0);
    }

    private static double genMaxProbability() {
        return getProbability(LOWER_BOUND - 0.5D, LOWER_BOUND + 0.5D);
    }

    public static double getMagnitude(double x, double y) {
        //noinspection ConstantConditions
        return MAGNITUDE_CACHE.get(x * x + y * y);
    }

    public static double getAngle(double x, double y) {
        //noinspection ConstantConditions
        return ANGLE_CACHE.get(new Double[] {x, y});
    }

    public static double getLengthFromAngle(double t) {
        if (t >= -FastMath.PI && t <= FastMath.PI) {
            int s;

            if (t <= -THREE_PI_ON_4) {
                s = 3;
            } else if (t <= -PI_ON_4) {
                s = 2;
            } else if (t < PI_ON_4) {
                s = 1;
            } else if (t > THREE_PI_ON_4) {
                s = -1;
            } else {
                s = 0;
            }

            double f = t + (PI_ON_2 * s);

            if (f == PI_ON_4 || f == THREE_PI_ON_4) {
                return SQRT_2;
            } else if (f == PI_ON_2) {
                return 1D;
            }

            //noinspection ConstantConditions
            return ANGLE_TO_LENGTH_CACHE.get(f);
        }

        return 1D;
    }

    public static long getNumItemsInCaches() {
        return Caches.SQRT_X_ON_B_CACHE.estimatedSize()
                + DENSITY_CACHE.estimatedSize()
                + CUMULATIVE_DENSITY_CACHE.estimatedSize()
                + MAGNITUDE_CACHE.estimatedSize()
                + ANGLE_CACHE.estimatedSize()
                + ANGLE_TO_LENGTH_CACHE.estimatedSize();
    }

    public static double getEstimatedMemoryUsageInMB() {
        return (getNumItemsInCaches() * 64) / 8D / 1024D / 1024D;
    }

    protected static class Caches {
        /** 21851 entries */
        protected static final LoadingCache<Double, Double> SQRT_X_ON_B_CACHE;

        private static final double A = LOWER_BOUND;
        private static final double B = UPPER_BOUND;
        private static final double SQRT_AB = FastMath.sqrt(A / B);
        private static final double ONE_SUB_SQRT_AB = 1D - SQRT_AB;

        static {
            SQRT_X_ON_B_CACHE =
                    Caffeine.newBuilder()
                            .maximumSize(MAX_CACHE_SIZE)
                            .build(new SqrtXOnBCacheLoader());
        }

        private static double sqrtXOnB(double x) {
            //noinspection ConstantConditions
            return SQRT_X_ON_B_CACHE.get(x);
        }

        protected static class SqrtXOnBCacheLoader implements CacheLoader<Double, Double> {
            @Override
            public @NonNull Double load(@NonNull Double x) {
                return FastMath.sqrt(x / B);
            }
        }

        /**
         * Probability Density Function of the Stronghold's distance from 0,0.
         *
         * <p><img src = "../doc-files/ring_pdf.png" />
         */
        protected static class DensityCacheLoader implements CacheLoader<Double, Double> {
            private static final double ONE_ON_TWO_B_TIMES_ONE_SUB_SQRT_AB =
                    0.5D / (B * ONE_SUB_SQRT_AB);

            @Override
            public @NonNull Double load(@NonNull Double x) {
                return ONE_ON_TWO_B_TIMES_ONE_SUB_SQRT_AB / sqrtXOnB(x);
            }
        }

        /**
         * Cumulative Density Function of the Stronghold's distance from 0,0.
         *
         * <p><img src = "../doc-files/ring_cdf.png" />
         */
        protected static class CumulativeDensityCacheLoader implements CacheLoader<Double, Double> {
            @Override
            public @NonNull Double load(@NonNull Double x) {
                return (sqrtXOnB(x) - SQRT_AB) / ONE_SUB_SQRT_AB;
            }
        }

        protected static class MagnitudeCacheLoader implements CacheLoader<Double, Double> {
            @Override
            public @NonNull Double load(@NonNull Double r) {
                return FastMath.sqrt(r);
            }
        }

        protected static class AngleCacheLoader implements CacheLoader<Double[], Double> {
            @Override
            public @NonNull Double load(@NonNull Double @NonNull [] r) {
                return FastMath.atan2(r[1], r[0]);
            }
        }

        protected static class AngleToLengthCacheLoader implements CacheLoader<Double, Double> {
            @Override
            public @NonNull Double load(@Nonnull Double x) {
                return 1D / FastMath.sin(x);
            }
        }
    }
}
