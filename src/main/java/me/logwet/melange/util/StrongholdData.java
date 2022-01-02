package me.logwet.melange.util;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.Getter;
import org.apache.commons.lang3.concurrent.CallableBackgroundInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentException;

public class StrongholdData {
    private final List<double[]> data;
    private final List<CallableBackgroundInitializer<Double>> sums;

    @Getter private final int count;

    public StrongholdData(double[] data1, double[] data2, double[] data3, int count) {
        data = new ArrayList<>();

        data.add(data1);
        data.add(data2);
        data.add(data3);

        this.count = count;

        sums = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Callable<Double> callable;

            if (i < count) {
                int index = i;
                callable = () -> ArrayHelper.sumArray(getData(index));
            } else {
                callable = () -> 0D;
            }

            CallableBackgroundInitializer<Double> initializer =
                    new CallableBackgroundInitializer<>(callable);
            initializer.start();

            sums.add(initializer);
        }
    }

    public double[] getData(int index) {
        return data.get(index);
    }

    public double getFactor(int index) {
        try {
            return ArrayHelper.normaliseSumFactor(sums.get(index).get());
        } catch (ConcurrentException e) {
            e.printStackTrace();
        }

        return 0D;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StrongholdData that = (StrongholdData) o;
        return count == that.count && Objects.equal(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data, count);
    }
}
