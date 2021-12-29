package me.logwet.melange.divine.consumer;

import com.google.common.collect.ImmutableList;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.parallelization.SharedKernels;
import me.logwet.melange.parallelization.ring.DivineFilterKernel;
import me.logwet.melange.util.DoubleBuffer2D;

public class StrongholdRing {
    private static final int STRONGHOLDS = 3;

    public static DoubleBuffer2D filter(
            ImmutableList<DivineProvider> divineProviders, DoubleBuffer2D defaultBuffer) {
        DoubleBuffer2D buffer;

        DivineFilterKernel kernel = SharedKernels.DIVINE_FILTER.get();
        synchronized (SharedKernels.DIVINE_FILTER) {
            kernel.setInput(defaultBuffer.getBuffer());
            kernel.setOutput(new double[defaultBuffer.getLength()]);
            kernel.setup(defaultBuffer.getWidth(), divineProviders);

            long s = System.currentTimeMillis();
            buffer = new DoubleBuffer2D(kernel.execute());
            long e = System.currentTimeMillis();

            System.out.println(e - s);
        }

        return buffer;
    }
}
