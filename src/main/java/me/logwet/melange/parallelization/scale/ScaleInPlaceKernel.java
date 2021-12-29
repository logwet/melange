package me.logwet.melange.parallelization.scale;

import lombok.Getter;
import lombok.Setter;
import me.logwet.melange.parallelization.kernel.AbstractInPlaceArrayKernel;

public class ScaleInPlaceKernel extends AbstractInPlaceArrayKernel {
    @Getter @Setter protected double factor;

    @Override
    public void run() {
        int i = getGlobalId();
        this.input[i] = this.input[i] * this.factor;
    }

    @Override
    public void initialize() {
        this.setFactor(0);
        super.initialize();
    }
}
