package me.logwet.melange.divine.consumer;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.util.DoubleBuffer2D;

public class StrongholdRing {
    private static final ImmutableList<StrongholdConsumer> strongholds;

    static {
        strongholds =
                ImmutableList.of(
                        new StrongholdConsumer(0),
                        new StrongholdConsumer(1),
                        new StrongholdConsumer(2));
    }

    public static DoubleBuffer2D filter(
            ImmutableList<DivineProvider> divineProviders, DoubleBuffer2D defaultBuffer) {
        List<DoubleBuffer2D> filterResults = new ArrayList<>();

        for (StrongholdConsumer stronghold : strongholds) {
            filterResults.add(stronghold.filter(divineProviders, defaultBuffer));
        }

        return (DoubleBuffer2D) defaultBuffer.add(filterResults.toArray(new DoubleBuffer2D[3]));
    }
}
