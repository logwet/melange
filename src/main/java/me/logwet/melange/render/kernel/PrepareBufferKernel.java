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

    protected double[] convolve1;
    protected int convolve1Range;
    protected double[] convolve2;
    protected int convolve2Range;

    public void setup(StrongholdData strongholdData, final double[] convolve1, int r) {
        this.enabled = 1;

        this.input1 = strongholdData.getData(0);
        this.input2 = strongholdData.getData(1);
        this.input3 = strongholdData.getData(2);

        this.factor1 = strongholdData.getFactor(0);
        this.factor2 = strongholdData.getFactor(1);
        this.factor3 = strongholdData.getFactor(2);

        this.convolve1 = convolve1;
        if (this.convolve1.length > 1) {
            this.convolve1Range = (((int) Math.sqrt(this.convolve2.length)) - 1) / 2;
        } else {
            this.convolve1Range = 0;
        }

        double rawRange = r / MelangeConstants.SCALING_FACTOR;
        double maxDist = rawRange * rawRange;
        this.convolve2Range = (int) Math.ceil(rawRange);

        if (convolve2Range > 0) {
            int convolveWidth = 2 * convolve2Range + 1;
            int convolveLength = convolveWidth * convolveWidth;

            this.convolve2 = new double[convolveLength];

            for (int x0 = -convolve2Range; x0 < convolveWidth - convolve2Range; x0++) {
                for (int y0 = -convolve2Range; y0 < convolveWidth - convolve2Range; y0++) {
                    if (x0 * x0 + y0 * y0 <= maxDist) {
                        int x = x0 + convolve2Range;
                        int y = y0 + convolve2Range;

                        this.convolve2[y * convolveWidth + x] = 1;
                    }
                }
            }
        } else {
            this.convolve2 = new double[1];
        }
    }

    public void setup(StrongholdData strongholdData, int r) {
        setup(strongholdData, new double[1], r);
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

        this.convolve1 = new double[1];
        this.convolve1Range = 0;
        this.convolve2 = new double[1];
        this.convolve2Range = 0;
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

                if (k > 0) {
                    input1[i] = s / k;
                } else {
                    input1[i] = 0;
                }
            } else {
                double v = 0D;

                //noinspection UnusedAssignment
                int range = 0;

                if (p == 1) {
                    range = convolve1Range;
                } else {
                    range = convolve2Range;
                }

                if (range > 0) {
                    int width = 2 * range + 1;

                    int x = calcX(i);
                    int y = calcY(i);

                    for (int x0 = constrainToBounds(x - range);
                            x0 < constrainToBounds(x + range);
                            x0++) {
                        for (int y0 = constrainToBounds(y - range);
                                y0 < constrainToBounds(y + range);
                                y0++) {
                            int x1 = x0 + range - x;
                            int y1 = y0 + range - y;

                            if (p == 1) {
                                v += input1[calcIndex(x0, y0)] * convolve1[y1 * width + x1];
                            } else {
                                v += input2[calcIndex(x0, y0)] * convolve2[y1 * width + x1];
                            }
                        }
                    }
                } else {
                    if (p == 1) {
                        v = input1[i];
                    } else {
                        v = input2[i];
                    }
                }

                if (p == 1) {
                    input2[i] = v;
                } else {
                    input3[i] = v;
                }
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

        int passCount = 1;
        if (convolve1Range > 0) {
            passCount = 2;
        }
        if (convolve2Range > 0) {
            passCount = 3;
        }

        this.execute(Range.create(MelangeConstants.BUFFER_SIZE), passCount);

        if (passCount == 1) {
            this.get(input1);
            return this.input1;
        } else if (passCount == 2) {
            this.get(input2);
            return this.input2;
        } else {
            this.get(input3);
            return this.input3;
        }
    }
}
