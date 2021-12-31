package me.logwet.melange.kernel;

import com.aparapi.Kernel;
import com.aparapi.internal.kernel.KernelManager;
import java.util.Objects;
import me.logwet.melange.kernel.api.SharedKernel;
import me.logwet.melange.render.kernel.PrepareBufferKernel;
import me.logwet.melange.render.kernel.PrepareImageKernel;
import me.logwet.melange.render.kernel.RenderDivineKernel;
import me.logwet.melange.render.kernel.ScaleKernel;
import org.apache.commons.lang3.concurrent.BackgroundInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SharedKernels {
    RENDER(RenderDivineKernel.class),
    PREPARE_BUFFER(PrepareBufferKernel.class),
    SCALE(ScaleKernel.class),
    PREPARE_IMAGE(PrepareImageKernel.class);

    private final KernelInitializer<? extends SharedKernel> initializer;

    <T extends Kernel> SharedKernels(Class<T> kernelClass) {
        initializer = new KernelInitializer<>(kernelClass);
        initializer.start();
    }

    public static void forceLoad() {
        SharedKernels.values().clone();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends SharedKernel> T getChecked() {
        try {
            return (T) initializer.get();
        } catch (ConcurrentException e) {
            e.printStackTrace();
        }

        return null;
    }

    @NotNull
    public <T extends SharedKernel> T get() {
        return Objects.requireNonNull(getChecked());
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
