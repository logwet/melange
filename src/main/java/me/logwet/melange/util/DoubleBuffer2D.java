package me.logwet.melange.util;

import org.apache.commons.math3.util.FastMath;

@SuppressWarnings("unused")
public class DoubleBuffer2D extends DoubleBuffer {
    private final int width;
    private final int bits;
    private final int xMask;

    /**
     * N x N sized 2D buffer of doubles
     *
     * @param width - Width of the buffer. (Must be a power of 2)
     */
    public DoubleBuffer2D(int width) {
        super(width * width);

        this.width = width;
        this.xMask = this.width - 1;

        assert (this.width & this.xMask) == 0;

        this.bits = Integer.bitCount(this.xMask);
    }

    /**
     * N x N sized 2D buffer of doubles
     *
     * @param buffer - Pre-existing buffer to wrap
     */
    public DoubleBuffer2D(double[] buffer) {
        super(buffer);

        this.width = (int) FastMath.sqrt(buffer.length);
        this.xMask = this.width - 1;

        assert (this.width & this.xMask) == 0;

        this.bits = Integer.bitCount(this.xMask);
    }

    public int getIndex(int x, int y) {
        return (y << this.bits) ^ x;
    }

    public int getX(int i) {
        return i & this.xMask;
    }

    public int getY(int i) {
        return i >> this.bits;
    }

    public double get(int x, int y) {
        return super.get(getIndex(x, y));
    }

    public void set(int x, int y, double v) {
        super.set(getIndex(x, y), v);
    }

    public void add(int x, int y, double v) {
        super.add(getIndex(x, y), v);
    }

    @Override
    public DoubleBuffer add(DoubleBuffer... doubleBuffers) {
        return new DoubleBuffer2D(super.addHelper(doubleBuffers));
    }

    @Override
    public DoubleBuffer scale(double f) {
        return new DoubleBuffer2D(super.scaleHelper(f));
    }

    @Override
    public DoubleBuffer copy() {
        return new DoubleBuffer2D(super.copyHelper());
    }
}
