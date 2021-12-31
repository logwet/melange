package me.logwet.melange.parallelization.api;

public interface DoubleArrayKernel extends SharedKernel {
    double[] render();
}
