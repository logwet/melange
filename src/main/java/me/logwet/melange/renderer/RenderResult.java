package me.logwet.melange.renderer;

import java.awt.image.BufferedImage;
import lombok.Getter;
import me.logwet.melange.math.RingDensity;
import me.logwet.melange.util.DoubleBuffer;
import me.logwet.melange.util.DoubleBuffer2D;

public class RenderResult {
    private static final int WIDTH_BITS = 9;
    private static final int WIDTH = 1 << WIDTH_BITS;
    private static final int WIDTH_2 = WIDTH / 2;
    private static final int BUFFER_SIZE = WIDTH * WIDTH;
    private static final int X_MASK = WIDTH - 1;
    @Getter(lazy = true)
    private static final DoubleBuffer2D defaultProbBuffer = buildDefaultBuffer();
    private static final double SEARCH_SIZE;
    private static final double SCALING_FACTOR;
    private static final double SCALING_FACTOR_2;
    private static final int COLOR_BITS = 16;
    private static final int COLOR_DEPTH = 1 << COLOR_BITS;

    static {
        SEARCH_SIZE = RingDensity.UPPER_BOUND + 200;
        SCALING_FACTOR = SEARCH_SIZE / (double) WIDTH_2;
        SCALING_FACTOR_2 = SCALING_FACTOR / 2;
    }

    private final boolean hasData = false;

    @Getter(lazy = true)
    private final BufferedImage render = genRender();

    private static void addDefaultProbToBuffer(int i, DoubleBuffer buffer) {
        int ox = i & X_MASK;
        int oy = i >> WIDTH_BITS;

        double v = getDefaultProbForCoord(ox, oy);
        if (v > 0) {
            buffer.set(i, v);
        }
    }

    private static double getDefaultProbForCoord(int ox, int oy) {
        int x = ox - WIDTH_2;
        int y = oy - WIDTH_2;

        double r = RingDensity.getMagnitude(x, y) * SCALING_FACTOR;

        if (r >= RingDensity.LOWER_BOUND && r <= RingDensity.UPPER_BOUND) {
            //            double t = RingDensity.getAngle(x, y);
            //            double k = RingDensity.getLengthFromAngle(t) * SCALING_FACTOR / 2D;
            return RingDensity.getProbability(r - SCALING_FACTOR_2, r + SCALING_FACTOR_2);
        }
        return 0;
    }

    private static DoubleBuffer2D buildDefaultBuffer() {
        final DoubleBuffer2D buffer = new DoubleBuffer2D(WIDTH);
        buffer.operateOnIndices(i -> addDefaultProbToBuffer(i, buffer));
        buffer.normalizeSumInPlace();
        return buffer;
    }

    private BufferedImage buildImage(DoubleBuffer2D buffer) {
        @SuppressWarnings("SuspiciousNameCombination")
        final BufferedImage image = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_USHORT_GRAY);

        buffer.normalizeInPlace(COLOR_DEPTH);

        buffer.operateOnIndices(
                i ->
                        image.getRaster()
                                .setPixel(
                                        buffer.getX(i),
                                        buffer.getY(i),
                                        new int[] {(int) buffer.get(i)}));
        return image;
    }

    private BufferedImage genRender() {
        long startTime = System.currentTimeMillis();

        DoubleBuffer2D buffer = (DoubleBuffer2D) getDefaultProbBuffer().copy();

        final BufferedImage image = buildImage(buffer);

        long endTime = System.currentTimeMillis();

        System.out.println("Generated render in " + (endTime - startTime) + "ms");

        return image;
    }
}
