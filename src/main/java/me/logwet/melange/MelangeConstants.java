package me.logwet.melange;

import org.apache.commons.math3.util.FastMath;

public class MelangeConstants {
    public static final double PI = FastMath.PI;
    public static final double TWO_PI = 2 * PI;
    public static final double TWO_PI_ON_THREE = TWO_PI / 3;
    public static final double PI_ON_TWO = PI / 2;

    public static final double LOWER_BOUND = 1280;
    public static final double UPPER_BOUND = 2816;

    //    public static final int HALF_WIDTH = 168 + 7;
    public static final int HALF_WIDTH = 255;
    public static final int WIDTH = HALF_WIDTH * 2 + 1;

    public static final int BUFFER_SIZE = WIDTH * WIDTH;

    public static final double SEARCH_SIZE = UPPER_BOUND + 100;
    public static final double SCALING_FACTOR = SEARCH_SIZE / HALF_WIDTH;
    public static final double HALF_SCALING_FACTOR = SCALING_FACTOR / 2;

    public static final int COLOR_BITS = 16;
    public static final int COLOR_DEPTH = 1 << COLOR_BITS;

    private MelangeConstants() {}
}
