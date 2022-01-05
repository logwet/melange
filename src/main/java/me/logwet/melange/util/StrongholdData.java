package me.logwet.melange.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StrongholdData {
    @EqualsAndHashCode.Include private final List<double[]> data;

    private final List<Future<Double>> sums;

    @EqualsAndHashCode.Include @Getter private final int count;

    public StrongholdData(double[] data1, double[] data2, double[] data3, int count) {
        data = new ArrayList<>();

        data.add(data1);
        data.add(data2);
        data.add(data3);

        this.count = count;

        sums = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Supplier<Double> supplier;

            if (i < count) {
                int index = i;
                supplier = () -> ArrayHelper.sumArray(getData(index));
            } else {
                supplier = () -> 0D;
            }

            sums.add(CompletableFuture.supplyAsync(supplier));
        }
    }

    public double[] getData(int index) {
        return data.get(index);
    }

    public double getFactor(int index) {
        try {
            return ArrayHelper.normaliseSumFactor(sums.get(index).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return 0D;
    }
}
