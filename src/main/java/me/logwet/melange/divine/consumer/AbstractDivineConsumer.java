package me.logwet.melange.divine.consumer;

import com.google.common.collect.ImmutableList;
import java.util.Objects;
import java.util.function.ToDoubleBiFunction;
import me.logwet.melange.divine.filter.DivineFilter;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.math.RingDensity;
import me.logwet.melange.renderer.RenderResult;
import me.logwet.melange.util.DoubleBuffer2D;

public abstract class AbstractDivineConsumer implements DivineConsumer {
    private final int index;

    public AbstractDivineConsumer(int index) {
        this.index = index;
    }

    @Override
    public DoubleBuffer2D filter(
            ImmutableList<DivineProvider> divineProviders, DoubleBuffer2D defaultBuffer) {
        int width = defaultBuffer.getWidth();
        int width2 = width / 2;
        double scalingFactor = RingDensity.SEARCH_SIZE / (double) width2;

        DoubleBuffer2D buffer = new DoubleBuffer2D(width);

        for (DivineProvider divineProvider : divineProviders) {
            buffer.operateOnIndices(
                    i -> {
                        boolean keep = true;

                        for (DivineFilter filter : divineProvider.getFilters()) {
                            int ox = buffer.getX(i) - width2;
                            int oy = buffer.getY(i) - width2;

                            ToDoubleBiFunction<Integer, Integer> filterOperation = null;

                            switch (filter.getType()) {
                                case ANGLE:
                                    filterOperation = RingDensity::getAngle;
                                    break;
                                case DISTANCE:
                                    filterOperation = (x, y) -> RingDensity.getMagnitude(x, y) * scalingFactor;
                                    break;
                            }

                            if (Objects.nonNull(filterOperation)) {
                                if (!filter.test(filterOperation.applyAsDouble(ox, oy), this.index)) {
                                    keep = false;
                                    break;
                                }
                            }
                        }

                        if (keep) {
                            buffer.set(i, defaultBuffer.get(i));
                        }
                    });
        }

        return buffer;
    }
}
