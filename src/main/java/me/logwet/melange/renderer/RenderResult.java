package me.logwet.melange.renderer;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.stream.IntStream;
import lombok.Getter;
import me.logwet.melange.math.RingDensity;

public class RenderResult {
    private static final int WIDTH_BITS = 9;
    private static final int WIDTH = 1 << WIDTH_BITS;
    private static final int WIDTH_2 = WIDTH / 2;
    private static final double[][] probBuffer = new double[WIDTH][WIDTH];
    private static final double SEARCH_SIZE = RingDensity.UPPER_BOUND + 200;
    private static final int COLOR_BITS = 16;
    private static final int COLOR_DEPTH = 1 << COLOR_BITS;
    private final boolean hasData = false;

    @Getter(lazy = true)
    private final BufferedImage render = genRender();

    private void updatePixel(int ox, int oy, WritableRaster raster) {
        double v = getProbForPixel(ox, oy);
        probBuffer[ox][oy] = v;
        if (v > 0) {
            raster.setPixel(
                    ox,
                    oy,
                    new int[] {(int) ((v / RingDensity.getMaxProbability()) * COLOR_DEPTH)});
        }
    }

    private double getProbForPixel(int ox, int oy) {
        int x = ox - WIDTH_2;
        int y = oy - WIDTH_2;

        double r = RingDensity.getMagnitude(x, y) * (SEARCH_SIZE / (double) WIDTH_2);
        double t = RingDensity.getAngle(x, y);

        if (r >= RingDensity.LOWER_BOUND && r <= RingDensity.UPPER_BOUND) {
            return RingDensity.getProbability(r);
        }
        return 0;
    }

    private BufferedImage genRender() {
        long startTime = System.currentTimeMillis();

        final BufferedImage image = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_USHORT_GRAY);

        final int xMask = WIDTH - 1;

        //noinspection ResultOfMethodCallIgnored
        IntStream.range(0, WIDTH * WIDTH)
                .parallel()
                .peek(i -> updatePixel(i & xMask, i >> WIDTH_BITS, image.getRaster()))
                .allMatch(i -> true);

        long endTime = System.currentTimeMillis();

        System.out.println("Generated render in " + (endTime - startTime) + "ms");

        return image;
    }
}
