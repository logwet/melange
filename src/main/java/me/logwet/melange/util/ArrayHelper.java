package me.logwet.melange.util;

import java.util.Arrays;
import me.logwet.melange.MelangeConstants;

public class ArrayHelper {
    private ArrayHelper() {}

    public static double sumArray(double[] array) {
        return Arrays.stream(array).parallel().sum();
    }

    public static double normaliseSumFactor(double sum) {
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

    public static int getX(int i, int w) {
        return i - (w * (i / w));
    }

    public static int getX(int i) {
        return getX(i, MelangeConstants.WIDTH);
    }

    public static int getY(int i, int w) {
        return i / w;
    }

    public static int getY(int i) {
        return getY(i, MelangeConstants.WIDTH);
    }

    public static int getIndex(int x, int y, int w) {
        return y * w + x;
    }

    public static int getIndex(int x, int y) {
        return getIndex(x, y, MelangeConstants.WIDTH);
    }
}
