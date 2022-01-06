package me.logwet.melange.util;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode()
public class StrongholdData {
    @EqualsAndHashCode.Include private final List<double[]> data;

    @EqualsAndHashCode.Include @Getter private final int count;

    public StrongholdData(double[] data1, double[] data2, double[] data3, int count) {
        data = new ArrayList<>();

        data.add(data1);
        data.add(data2);
        data.add(data3);

        this.count = count;
    }

    public double[] getData(int index) {
        return data.get(index);
    }

    public double getSumFactor(int index) {
        return ArrayHelper.normaliseSumFactor(ArrayHelper.sumArray(getData(index)));
    }
}
