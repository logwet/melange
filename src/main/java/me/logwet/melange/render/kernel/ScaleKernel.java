package me.logwet.melange.render.kernel;

import com.aparapi.Range;
import lombok.Getter;
import lombok.Setter;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.kernel.api.AbstractSharedKernel;
import me.logwet.melange.kernel.api.DoubleArrayKernel;

public class ScaleKernel extends AbstractSharedKernel implements DoubleArrayKernel {
    @Getter @Setter protected double[] input;

    protected double factor;

    public void setup(double[] input, double factor) {
        this.input = input;
        this.factor = factor;
    }

    @Override
    protected void initializer() {
        input = new double[1];
        factor = 0;
    }

    @Override
    public void run() {
        int i = getGlobalId();

        input[i] *= factor;
    }

    @Override
    public double[] render() {
        this.execute(Range.create(MelangeConstants.BUFFER_SIZE));

        return this.input;
    }
}
