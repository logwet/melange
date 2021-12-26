package me.logwet.melange.parallelization.kernel;

import com.aparapi.Kernel;

public abstract class AbstractArrayKernel extends Kernel implements SharedKernel {
    protected double[] input;

    public double[] getInput() {
        return this.input;
    }

    public void setInput(double[] input) {
        this.input = input;
    }
}
