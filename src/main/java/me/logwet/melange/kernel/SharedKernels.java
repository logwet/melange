package me.logwet.melange.kernel;

import com.aparapi.Kernel;
import com.aparapi.internal.kernel.KernelManager;
import java.util.Objects;
import me.logwet.melange.kernel.api.SharedKernel;
import me.logwet.melange.render.convolve.ConvolveKernel;
import me.logwet.melange.render.divine.RenderDivineKernel;
import org.apache.commons.lang3.concurrent.BackgroundInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SharedKernels implements AutoCloseable {
    RENDER(RenderDivineKernel.class),
    CONVOLVE(ConvolveKernel.class);

    public static final Logger LOGGER = LoggerFactory.getLogger(SharedKernels.class);

    private final KernelInitializer<? extends SharedKernel> initializer;

    <T extends Kernel> SharedKernels(Class<T> kernelClass) {
        initializer = new KernelInitializer<>(kernelClass);
        initializer.start();
    }

    public static void forceLoad() {
        //noinspection ResultOfMethodCallIgnored
        SharedKernels.values();
    }

    public static void closeAll() {
        for (SharedKernels sharedKernel : SharedKernels.values()) {
            sharedKernel.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends SharedKernel> T getChecked() {
        try {
            return (T) initializer.get();
        } catch (ConcurrentException e) {
            LOGGER.error("Unable to get SharedKernel from initializer", e);
        }

        return null;
    }

    @NotNull
    public <T extends SharedKernel> T get() {
        return Objects.requireNonNull(getChecked());
    }

    @Override
    public void close() {
        try {
            this.get().close();
        } catch (Exception e) {
            LOGGER.error("Unable to close SharedKernel", e);
        }
    }

    private static class KernelInitializer<T extends SharedKernel>
            extends BackgroundInitializer<T> {
        Class<? extends Kernel> kernelClass;

        protected KernelInitializer(Class<? extends Kernel> kernelClass) {
            this.kernelClass = kernelClass;
        }

        @NotNull
        @Override
        protected T initialize() {
            @SuppressWarnings("unchecked")
            T kernel = (T) KernelManager.sharedKernelInstance(this.kernelClass);
            kernel.initialize();

            return kernel;
        }
    }
}
