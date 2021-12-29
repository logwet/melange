package me.logwet.melange.parallelization.kernel;

import com.aparapi.Kernel;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractArrayKernel extends Kernel implements SharedKernel {
    @Getter @Setter protected double[] input;
}
