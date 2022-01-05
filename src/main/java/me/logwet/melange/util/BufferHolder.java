package me.logwet.melange.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BufferHolder {
    @EqualsAndHashCode.Include @Getter private final double[] buffer;

    private Future<Double> max;
    private Future<Double> sum;

    public BufferHolder(double[] buffer, boolean maxEnabled, boolean sumEnabled) {
        this.buffer = buffer;

        if (maxEnabled) {
            this.max = CompletableFuture.supplyAsync(() -> ArrayHelper.maxArray(this.buffer));
        }

        if (sumEnabled) {
            this.sum = CompletableFuture.supplyAsync(() -> ArrayHelper.sumArray(this.buffer));
        }
    }

    public BufferHolder update(boolean maxEnabled, boolean sumEnabled) {
        return new BufferHolder(this.buffer, maxEnabled, sumEnabled);
    }

    public double getMax() {
        try {
            return this.max.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0D;
    }

    public double getSum() {
        try {
            return this.sum.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0D;
    }
}
