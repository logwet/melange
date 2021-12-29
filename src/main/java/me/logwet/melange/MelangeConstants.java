package me.logwet.melange;

public class MelangeConstants {
    public static final double PI = Math.PI;
    public static final double TWO_PI = 2 * PI;
    public static final double TWO_PI_ON_THREE = TWO_PI / 3;
    public static final double PI_ON_TWO = PI / 2;

    public static final double LOWER_BOUND = 1280;
    public static final double UPPER_BOUND = 2816;

    public static final int WIDTH_BITS = 9;
    public static final int WIDTH = 1 << WIDTH_BITS;
    public static final int HALF_WIDTH = WIDTH / 2;
    public static final int X_MASK = WIDTH - 1;

    public static final int BUFFER_SIZE = WIDTH * WIDTH;

    public static final double SEARCH_SIZE = UPPER_BOUND + 200;
    public static final double SCALING_FACTOR = SEARCH_SIZE / HALF_WIDTH;
    public static final double HALF_SCALING_FACTOR = SCALING_FACTOR / 2;

    private MelangeConstants() {}
}
