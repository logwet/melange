package me.logwet.melange.render.kernel;

import com.aparapi.Range;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.kernel.api.DoubleArrayKernel;
import me.logwet.melange.util.StrongholdData;

public class PrepareBufferKernel extends AbstractRingKernel implements DoubleArrayKernel {
    protected int enabled;

    protected double[] input1;
    protected double[] input2;
    protected double[] input3;

    protected double factor1;
    protected double factor2;
    protected double factor3;

    protected double range;

    public void setup(StrongholdData strongholdData, int range) {
        this.enabled = 1;

        this.input1 = strongholdData.getData(0);
        this.input2 = strongholdData.getData(1);
        this.input3 = strongholdData.getData(2);

        this.factor1 = strongholdData.getFactor(0);
        this.factor2 = strongholdData.getFactor(1);
        this.factor3 = strongholdData.getFactor(2);

        this.range = range / MelangeConstants.SCALING_FACTOR;
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

        this.range = 0D;
    }

    @Override
    public void run() {
        if (enabled == 1) {
            int i = getGlobalId();
            int p = getPassId();

            if (p == 0) {
                double s = 0D;
                int k = 0;

                if (factor1 > 0) {
                    s += input1[i] * factor1;
                    k++;
                }
                if (factor2 > 0) {
                    s += input2[i] * factor2;
                    k++;
                }
                if (factor3 > 0) {
                    s += input3[i] * factor3;
                    k++;
                }

                input1[i] = s / k;
            } else {
                int x = calcX(i);
                int y = calcY(i);

                double v = 0D;

                for (int x0 = constrainToBounds((int) floor(x - range));
                        x0 < constrainToBounds((int) ceil(x + range));
                        x0++) {
                    for (int y0 = constrainToBounds((int) floor(y - range));
                            y0 < constrainToBounds((int) ceil(y + range));
                            y0++) {
                        if (calcDistance(x0, y0, x, y) <= range) {
                            v += input1[calcIndex(x0, y0)];
                        }
                    }
                }

                input2[i] = v;
            }
        }
    }

    @Override
    public double[] render() {
        this.setExplicit(true);

        if (factor1 > 0) {
            this.put(input1);
        }
        if (factor2 > 0) {
            this.put(input2);
        }
        if (factor3 > 0) {
            this.put(input3);
        }

        this.execute(Range.create(MelangeConstants.BUFFER_SIZE), range > 0 ? 2 : 1);

        if (range > 0) {
            this.get(input2);
            return this.input2;
        } else {
            this.get(input1);
            return this.input1;
        }
    }
}
