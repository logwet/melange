package me.logwet.melange.parallelization.kernel;

import com.aparapi.Range;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.parallelization.api.AbstractSharedKernel;
import me.logwet.melange.parallelization.api.ShortArrayKernel;

public class PrepareImageKernel extends AbstractSharedKernel implements ShortArrayKernel {
    protected double[] input;
    protected short[] output;

    protected double factor;

    public void setup(double[] input, short[] output, double max) {
        this.input = input;
        this.output = output;

        this.factor = MelangeConstants.COLOR_DEPTH / max;
    }

    @Override
    protected void initializer() {
        this.input = new double[1];
        this.output = new short[1];

        this.factor = 0;
    }

    @Override
    public void run() {
        int i = getGlobalId();

        output[i] = (short) (((int) (input[i] * factor)) & 0xffff);
    }

    @Override
    public short[] render() {
        this.execute(Range.create(MelangeConstants.BUFFER_SIZE));

        return this.output;
    }
}
