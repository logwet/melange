package me.logwet.melange.util;

import java.util.Arrays;
import me.logwet.melange.MelangeConstants;

public class ArrayHelper {
    private ArrayHelper() {}

    public static double sumArray(double[] array) {
        return Arrays.stream(array).parallel().sum();
    }

    public static double normaliseSumFactor(double[] array) {
        double sum = sumArray(array);
        if (sum > 0) {
            return 1D / sum;
        }

        return 0D;
    }

    public static double maxArray(double[] buffer) {
        double max = buffer[0];

        for (int i = 1; i < buffer.length; i++) {
            if (buffer[i] > max) {
                max = buffer[i];
            }
        }

        return max;
    }

    public static int getX(int i) {
        return i & MelangeConstants.X_MASK;
    }

    public static int getY(int i) {
        return MelangeConstants.X_MASK - (i >> MelangeConstants.WIDTH_BITS);
    }

    public static int getIndex(int x, int y) {
        return ((MelangeConstants.X_MASK - y) << MelangeConstants.WIDTH_BITS) ^ x;
    }
}
