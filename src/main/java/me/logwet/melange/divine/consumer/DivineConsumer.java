package me.logwet.melange.divine.consumer;

import com.google.common.collect.ImmutableList;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.util.DoubleBuffer2D;

public interface DivineConsumer {
    DoubleBuffer2D filter(
            ImmutableList<DivineProvider> divineProviders, DoubleBuffer2D defaultBuffer);
}
