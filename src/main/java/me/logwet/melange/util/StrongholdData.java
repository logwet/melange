package me.logwet.melange.util;

import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode()
public class StrongholdData {
    @EqualsAndHashCode.Include private final List<double[]> data;

    public StrongholdData(double[]... data) {
        this.data = Arrays.asList(data);
    }

    public StrongholdData(List<double[]> data) {
        this.data = data;
    }

    public double[] getData(int index) {
        return data.get(index);
    }

    public double getSumFactor(int index) {
        return ArrayHelper.normaliseSumFactor(ArrayHelper.sumArray(getData(index)));
    }
}
