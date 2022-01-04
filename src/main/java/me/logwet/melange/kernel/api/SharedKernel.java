package me.logwet.melange.kernel.api;

public interface SharedKernel extends AutoCloseable {
    void initialize();
}
