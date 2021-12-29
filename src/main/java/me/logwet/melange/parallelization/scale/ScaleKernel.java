package me.logwet.melange.parallelization.scale;

import me.logwet.melange.parallelization.kernel.AbstractCopyArrayKernel;

public class ScaleKernel extends AbstractCopyArrayKernel {
    protected double factor;

    public double getFactor() {
        return this.factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    @Override
    public void run() {
        int i = getGlobalId();
        this.output[i] = this.input[i] * this.factor;
    }

    @Override
    public void initialize() {
        this.setFactor(0);
        super.initialize();
    }
}
