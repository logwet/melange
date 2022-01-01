package me.logwet.melange.render.kernel;

import com.aparapi.Range;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.kernel.api.AbstractSharedKernel;
import me.logwet.melange.kernel.api.ShortArrayKernel;
import me.logwet.melange.util.BufferHolder;

public class PrepareImageKernel extends AbstractSharedKernel implements ShortArrayKernel {
    protected double[] input;
    protected short[] output;

    protected double factor;

    public void setup(BufferHolder input, short[] output) {
        this.input = input.getBuffer();
        this.output = output;

        double max = input.getMax();
        this.factor = max > 0 ? MelangeConstants.COLOR_DEPTH / max : 0D;
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
        this.setExplicit(true);

        this.put(input);

        this.execute(Range.create(MelangeConstants.BUFFER_SIZE));

        return this.output;
    }
}
