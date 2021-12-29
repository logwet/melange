package me.logwet.melange.parallelization.kernel;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractCopyArrayKernel extends AbstractInPlaceArrayKernel {
    @Getter @Setter protected double[] output;

    @Override
    public double[] execute() {
        this.executeHelper();
        return this.getOutput();
    }

    @Override
    public void initialize() {
        this.setOutput(new double[1]);
        super.initialize();
    }
}
