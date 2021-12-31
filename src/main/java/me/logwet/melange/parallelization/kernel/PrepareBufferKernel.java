package me.logwet.melange.parallelization.kernel;

import com.aparapi.Range;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.parallelization.api.DoubleArrayKernel;
import me.logwet.melange.util.StrongholdData;

public class PrepareBufferKernel extends AbstractRingKernel implements DoubleArrayKernel {
    protected int enabled;

    protected double[] input1;
    protected double[] input2;
    protected double[] input3;

    protected double factor1;
    protected double factor2;
    protected double factor3;

    public void setup(StrongholdData strongholdData) {
        this.enabled = 1;

        this.input1 = strongholdData.getData1();
        this.input2 = strongholdData.getData2();
        this.input3 = strongholdData.getData3();

        this.factor1 = strongholdData.getFactor(0, input1);
        this.factor2 = strongholdData.getFactor(1, input2);
        this.factor3 = strongholdData.getFactor(2, input3);
    }

    @Override
    protected void initializer() {
        this.enabled = 0;

        this.input1 = new double[1];
        this.input2 = new double[1];
        this.input3 = new double[1];

        this.factor1 = 0;
        this.factor2 = 0;
        this.factor3 = 0;
    }

    @Override
    public void run() {
        if (enabled == 1) {
            int i = getGlobalId();

            double s = 0D;
            int k = 0;

            if (factor1 > 0) {
                s += input1[i] / factor1;
                k++;
            }
            if (factor2 > 0) {
                s += input2[i] / factor2;
                k++;
            }
            if (factor3 > 0) {
                s += input3[i] / factor3;
                k++;
            }

            input1[i] = s / k;
        }
    }

    @Override
    public double[] render() {
        this.setExplicit(true);

        this.put(input1).put(input2).put(input3);

        this.execute(Range.create(MelangeConstants.BUFFER_SIZE));

        this.get(input1);

        return this.input1;
    }
}
