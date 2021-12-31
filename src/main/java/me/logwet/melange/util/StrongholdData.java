package me.logwet.melange.util;

import lombok.Getter;

public class StrongholdData {
    @Getter private final double[] data1;
    @Getter private final double[] data2;
    @Getter private final double[] data3;

    @Getter private final int count;

    public StrongholdData(double[] data1, double[] data2, double[] data3, int count) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;

        this.count = count;
    }

    public double getFactor(int index, double[] array) {
        if (index < count) {
            return ArrayHelper.sumArray(array);
        }

        return 0D;
    }
}
