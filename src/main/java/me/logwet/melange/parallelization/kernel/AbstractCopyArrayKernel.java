package me.logwet.melange.parallelization.kernel;

public abstract class AbstractCopyArrayKernel extends AbstractInPlaceArrayKernel {
    protected double[] output;

    public double[] getOutput() {
        return this.output;
    }

    public void setOutput(double[] output) {
        this.output = output;
    }

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
