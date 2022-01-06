package me.logwet.melange.render.convolve;

import ch.qos.logback.classic.Logger;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.kernel.SharedKernels;
import me.logwet.melange.util.ArrayHelper;
import org.apache.commons.math3.util.FastMath;
import org.jtransforms.fft.DoubleFFT_2D;
import org.slf4j.LoggerFactory;

/**
 * FFT convolution implementation adapted from <a
 * href="https://github.com/mjtb49/DivineHeatmapGenerator/blob/a8bd3966c7bf4d9dd96b2c4a32f05dcd7fa29dee/src/main/java/heatmaps/FFTHelper.java">FFTHelper</a>
 * from DivineHeatmapGenerator by Matthew Bolan.
 *
 * @author Matthew Bolan
 */
public class ConvolveHelper {
    protected static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ConvolveHelper.class);
    private static final double EPSILON = 1e-10;
    private static final int THRESHOLD = 80;

    private ConvolveHelper() {}

    private static double[][] realToComplexArray(double[] realArray, int width) {
        double[][] output = new double[width][width * 2];

        for (int i = 0; i < MelangeConstants.WIDTH; i++) {
            for (int j = 0; j < MelangeConstants.WIDTH; j++) {
                output[i][2 * j] = realArray[ArrayHelper.getIndex(i, j, MelangeConstants.WIDTH)];
            }
        }

        return output;
    }

    private static double[][] realToComplexArray(double[][] realArray, int width) {
        double[][] output = new double[width][width * 2];

        for (int i = 0; i < realArray.length; i++) {
            for (int j = 0; j < realArray[0].length; j++) {
                output[i][2 * j] = realArray[i][j];
            }
        }

        return output;
    }

    private static void pointwiseMultiplyComplexArrays(double[][] a, double[][] b) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length / 2; j++) {
                // (x + iy)(z + iw) = (xz - yw) + i(xw + yz)
                double at = a[i][2 * j];
                a[i][2 * j] = at * b[i][2 * j] - a[i][2 * j + 1] * b[i][2 * j + 1];
                a[i][2 * j + 1] = at * b[i][2 * j + 1] + a[i][2 * j + 1] * b[i][2 * j];
            }
        }
    }

    private static double[] convolveWithFFT(double[] data, double[][] kernel) {
        int kernelWidth = kernel.length;
        int paddedWidth = MelangeConstants.WIDTH + kernelWidth - 1;

        double[][] complexData = realToComplexArray(data, paddedWidth);
        double[][] complexKernel = realToComplexArray(kernel, paddedWidth);

        DoubleFFT_2D transformer = new DoubleFFT_2D(paddedWidth, paddedWidth);

        transformer.complexForward(complexData);
        transformer.complexForward(complexKernel);

        pointwiseMultiplyComplexArrays(complexData, complexKernel);

        transformer.complexInverse(complexData, true);

        int offset = kernelWidth / 2;

        for (int i = 0; i < MelangeConstants.WIDTH; i++) {
            for (int j = 0; j < MelangeConstants.WIDTH; j++) {
                double v = complexData[i + offset][(j + offset) * 2];
                if (FastMath.abs(v) < EPSILON) {
                    v = 0D;
                }
                if (v < 0) {
                    LOGGER.warn("Illegal negative real part " + v);
                    v = 0D;
                }

                data[ArrayHelper.getIndex(i, j)] = v;

                /*
                if (FastMath.abs(complexData[i + offset][(j + offset) * 2 + 1]) > EPSILON) {
                    LOGGER.warn(
                            "Large imaginary part "
                                    + complexData[i + offset][(j + offset) * 2 + 1]);
                }
                 */
            }
        }

        return data;
    }

    private static double[] flatten2DArray(double[][] input) {
        return Arrays.stream(input).flatMapToDouble(DoubleStream::of).toArray();
    }

    public static double[] convolveWithRecursion(double[] data, double[][] kernel) {
        synchronized (SharedKernels.CONVOLVE) {
            ConvolveKernel convolveKernel = SharedKernels.CONVOLVE.get();
            convolveKernel.setup(
                    data,
                    flatten2DArray(kernel),
                    kernel.length / 2,
                    new double[MelangeConstants.BUFFER_SIZE]);
            return convolveKernel.render();
        }
    }

    public static double[] convolve(double[] data, double[][] kernel) {
        long startTime = System.currentTimeMillis();

        String algo;

        double[] output;

        if (KernelManager.instance().bestDevice() instanceof OpenCLDevice
                && kernel.length < THRESHOLD) {
            output = convolveWithRecursion(data, kernel);
            algo = "Recursion";
        } else {
            output = convolveWithFFT(data, kernel);
            algo = "FFT";
        }

        long endTime = System.currentTimeMillis();
        LOGGER.info("Convolution took " + (endTime - startTime) + "ms with " + algo + " Algorithm");

        return output;
    }

    public static double[][] genRangeKernel(int r) {
        double rawRange = r / MelangeConstants.SCALING_FACTOR;
        double maxDist = rawRange * rawRange;
        int range = (int) FastMath.ceil(rawRange);

        int width = 2 * range + 1;

        double[][] kernel = new double[width][width];

        for (int x0 = -range; x0 < width - range; x0++) {
            for (int y0 = -range; y0 < width - range; y0++) {
                if (x0 * x0 + y0 * y0 <= maxDist) {
                    kernel[x0 + range][y0 + range] = 1;
                }
            }
        }

        return kernel;
    }
}
