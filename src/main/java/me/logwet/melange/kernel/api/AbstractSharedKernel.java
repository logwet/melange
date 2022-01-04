package me.logwet.melange.kernel.api;

import com.aparapi.Kernel;

public abstract class AbstractSharedKernel extends Kernel implements SharedKernel {
    protected abstract void initializer();

    @Override
    public void initialize() {
        this.initializer();

        this.execute(1);
    }

    @Override
    public void close() {
        this.dispose();
    }
}
