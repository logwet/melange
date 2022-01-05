package me.logwet.melange.render.convolve;

import ch.qos.logback.classic.Logger;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.kernel.SharedKernels;
import me.logwet.melange.render.kernel.ElementwiseMultiplyKernel;
import me.logwet.melange.util.ArrayHelper;
import org.apache.commons.math3.util.FastMath;
import org.jtransforms.fft.DoubleFFT_2D;
import org.slf4j.LoggerFactory;

public class ConvolveHelper {
    protected static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ConvolveHelper.class);
    private static final double EPSILON = 1e-10;

    private static double[] realToComplexArray(double[] realArray, int width) {
        double[] output = new double[width * width * 2];

        for (int i = 0; i < realArray.length; i++) {
            output[i * 2] = realArray[i];
        }

        return output;
    }

    private static void pointwiseMultiplyComplexArrays(double[] a, double[] b) {
        synchronized (SharedKernels.ELEMENTWISE_MULTIPLY) {
            ElementwiseMultiplyKernel kernel = SharedKernels.ELEMENTWISE_MULTIPLY.get();
            kernel.setup(a, b);
            kernel.render();
        }
    }

    /**
     * FFT convolution implementation from <a
     * href="https://github.com/mjtb49/DivineHeatmapGenerator/blob/a8bd3966c7bf4d9dd96b2c4a32f05dcd7fa29dee/src/main/java/heatmaps/FFTHelper.java#L36-L76">FFTHelper.convolve()</a>
     * from DivineHeatmapGenerator by Matthew Bolan.
     *
     * @author Matthew Bolan
     */
    public static void convolve(double[] data, double[] kernel, int kernelWidth) {
        int paddedWidth = MelangeConstants.WIDTH + kernelWidth - 1;

        double[] d = realToComplexArray(data, paddedWidth);
        double[] k = realToComplexArray(kernel, paddedWidth);

        DoubleFFT_2D transformer = new DoubleFFT_2D(paddedWidth, paddedWidth);

        transformer.complexForward(d);
        transformer.complexForward(k);

        pointwiseMultiplyComplexArrays(d, k);

        transformer.complexInverse(d, true);

        int offset = kernelWidth / 2;

        for (int i = 0; i < data.length; i++) {
            int x = ArrayHelper.getX(i, paddedWidth);
            int y = ArrayHelper.getY(i, paddedWidth);
            int j = ArrayHelper.getIndex(x + offset, (y + offset) * 2, paddedWidth);

            double v = d[j];

            if (FastMath.abs(v) < EPSILON) {
                v = 0D;
            }
            if (v < 0) {
                LOGGER.error("Illegal negative real part " + v);
                v = 0D;
            }

            data[i] = v;

            if (FastMath.abs(d[j + 1]) > EPSILON) {
                LOGGER.error("Large imaginary part " + d[j + 1]);
            }
        }
    }

    public static double[] genRangeKernel(int r) {
        double rawRange = r / MelangeConstants.SCALING_FACTOR;
        double maxDist = rawRange * rawRange;
        int range = (int) FastMath.ceil(rawRange);

        int width = 2 * range + 1;
        int length = width * width;

        double[] kernel = new double[length];

        for (int x0 = -range; x0 < width - range; x0++) {
            for (int y0 = -range; y0 < width - range; y0++) {
                if (x0 * x0 + y0 * y0 <= maxDist) {
                    int x = x0 + range;
                    int y = y0 + range;

                    kernel[y * width + x] = 1;
                }
            }
        }

        return kernel;
    }
}
