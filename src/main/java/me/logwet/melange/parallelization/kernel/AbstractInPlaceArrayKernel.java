package me.logwet.melange.parallelization.kernel;

import com.aparapi.Range;

public abstract class AbstractInPlaceArrayKernel extends AbstractArrayKernel
        implements ArrayOutputKernel {
    protected void executeHelper() {
        Range range = Range.create(this.getInput().length);
        this.execute(range);
    }

    public double[] execute() {
        this.executeHelper();
        return this.getInput();
    }

    @Override
    public void initialize() {
        this.setInput(new double[1]);
        this.execute();
    }
}
