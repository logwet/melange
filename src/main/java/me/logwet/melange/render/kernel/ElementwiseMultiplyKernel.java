package me.logwet.melange.render.kernel;

import com.aparapi.Range;
import me.logwet.melange.kernel.api.AbstractSharedKernel;
import me.logwet.melange.kernel.api.DoubleArrayKernel;

public class ElementwiseMultiplyKernel extends AbstractSharedKernel implements DoubleArrayKernel {
    private double[] a;
    private double[] b;

    public void setup(double[] a, double[] b) {
        assert a.length == b.length;

        this.a = a;
        this.b = b;
    }

    @Override
    protected void initializer() {
        this.a = new double[1];
        this.b = new double[1];
    }

    @Override
    public void run() {
        int i = 2 * getGlobalId();

        double t = a[i];

        a[i] = (t * b[i]) - (a[i + 1] * b[i + 1]);
        a[i + 1] = (t * b[i + 1]) + (a[i + 1] * b[i]);
    }

    @Override
    public double[] render() {
        this.setExplicit(true);
        this.put(a).put(b);

        this.execute(Range.create(a.length / 2));

        this.get(a);
        return a;
    }
}
