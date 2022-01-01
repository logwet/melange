package me.logwet.melange.util;

import lombok.Getter;
import org.apache.commons.lang3.concurrent.CallableBackgroundInitializer;

public class BufferHolder {
    @Getter private final double[] buffer;

    private CallableBackgroundInitializer<Double> max;
    private CallableBackgroundInitializer<Double> sum;

    public BufferHolder(double[] buffer, boolean maxEnabled, boolean sumEnabled) {
        this.buffer = buffer;

        if (maxEnabled) {
            this.max = new CallableBackgroundInitializer<>(() -> ArrayHelper.maxArray(this.buffer));
            this.max.start();
        }

        if (sumEnabled) {
            this.sum = new CallableBackgroundInitializer<>(() -> ArrayHelper.sumArray(this.buffer));
            this.sum.start();
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
