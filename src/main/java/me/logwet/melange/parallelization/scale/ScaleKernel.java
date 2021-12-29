package me.logwet.melange.parallelization.scale;

import lombok.Getter;
import lombok.Setter;
import me.logwet.melange.parallelization.kernel.AbstractCopyArrayKernel;

public class ScaleKernel extends AbstractCopyArrayKernel {
    @Getter @Setter protected double factor;

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

    @Override
    protected void executeHelper() {
        this.setExplicit(true);
        this.put(input);
        super.executeHelper();
        this.get(output);
    }
}
