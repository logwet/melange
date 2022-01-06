package me.logwet.melange.render.convolve;

import com.aparapi.Range;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.kernel.api.AbstractRingKernel;
import me.logwet.melange.kernel.api.DoubleArrayKernel;

public class ConvolveKernel extends AbstractRingKernel implements DoubleArrayKernel {
    protected int enabled;

    protected double[] data;
    protected double[] output;

    protected double[] convolveKernel;
    protected int convolveRange;

    public void setup(double[] data, double[] convolveKernel, int convolveRange, double[] output) {
        this.enabled = 1;

        this.data = data;

        this.convolveKernel = convolveKernel;
        this.convolveRange = convolveRange;

        this.output = output;
    }

    @Override
    protected void initializer() {
        this.enabled = 0;

        this.data = new double[1];

        this.convolveKernel = new double[1];
        this.convolveRange = 0;

        this.output = new double[1];
    }

    @Override
    public void run() {
        if (enabled == 1) {
            int i = getGlobalId();

            int width = 2 * convolveRange + 1;

            int x = calcX(i);
            int y = calcY(i);

            double v = 0D;

            for (int x0 = constrainToBounds(x - convolveRange);
                    x0 < constrainToBounds(x + convolveRange);
                    x0++) {
                for (int y0 = constrainToBounds(y - convolveRange);
                        y0 < constrainToBounds(y + convolveRange);
                        y0++) {
                    int x1 = x0 + convolveRange - x;
                    int y1 = y0 + convolveRange - y;

                    v += data[calcIndex(x0, y0)] * convolveKernel[y1 * width + x1];
                }
            }

            output[i] = v;
        }
    }

    @Override
    public double[] render() {
        this.setExplicit(true);

        this.put(data).put(convolveKernel);

        this.execute(Range.create(MelangeConstants.BUFFER_SIZE));

        this.get(output);
        return output;
    }
}
