package me.logwet.melange.util;

import java.util.Arrays;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class DoubleBuffer {
    public final double[] buffer;
    protected final int length;

    /**
     * One dimensional buffer of doubles.
     *
     * @param length - The number of elements in the buffer
     */
    public DoubleBuffer(int length) {
        this.length = length;
        this.buffer = new double[this.length];
    }

    /**
     * One dimensional buffer of doubles.
     *
     * @param buffer - Pre-existing buffer to wrap
     */
    public DoubleBuffer(double[] buffer) {
        this.length = buffer.length;
        this.buffer = buffer;
    }

    public DoubleStream streamBuffer() {
        return Arrays.stream(this.buffer).parallel();
    }

    public void operateOnBuffer(DoubleConsumer consumer) {
        //noinspection ResultOfMethodCallIgnored
        this.streamBuffer().peek(consumer).allMatch(i -> true);
    }

    public IntStream streamIndices() {
        return IntStream.range(0, length).parallel();
    }

    public void operateOnIndices(IntConsumer consumer) {
        //noinspection ResultOfMethodCallIgnored
        this.streamIndices().peek(consumer).allMatch(i -> true);
    }

    public double get(int i) {
        return this.buffer[i];
    }

    public void set(int i, double v) {
        this.buffer[i] = v;
    }

    public void add(int i, double v) {
        this.buffer[i] += v;
    }

    protected double[] addHelper(final DoubleBuffer... doubleBuffers) {
        double[] array = copyHelper();

        this.operateOnIndices(
                i -> {
                    for (DoubleBuffer buffer : doubleBuffers) {
                        array[i] += buffer.buffer[i];
                    }
                });

        return array;
    }

    public DoubleBuffer add(DoubleBuffer... doubleBuffers) {
        return new DoubleBuffer(this.addHelper(doubleBuffers));
    }

    public double max() {
        double max = this.buffer[0];

        for (int i = 1; i < this.length; i++) {
            if (this.buffer[i] > max) {
                max = this.buffer[i];
            }
        }

        return max;
    }

    public double min() {
        double min = this.buffer[0];

        for (int i = 1; i < this.length; i++) {
            if (this.buffer[i] < min) {
                min = this.buffer[i];
            }
        }

        return min;
    }

    public double sum() {
        return streamBuffer().sum();
    }

    protected double[] scaleHelper(final double f) {
        final double[] array = new double[this.length];

        this.operateOnIndices(i -> array[i] = f * this.buffer[i]);

        return array;
    }

    public DoubleBuffer scale(double f) {
        return new DoubleBuffer(this.scaleHelper(f));
    }

    public DoubleBuffer normalize(double f) {
        return this.scale(f / this.max());
    }

    public DoubleBuffer normalize() {
        return this.normalize(1);
    }

    public DoubleBuffer normalizeSum(double f) {
        return this.scale(f / this.sum());
    }

    public DoubleBuffer normalizeSum() {
        return this.normalizeSum(1);
    }

    public void addInPlace(DoubleBuffer... doubleBuffers) {
        this.operateOnIndices(
                i -> {
                    for (DoubleBuffer buffer : doubleBuffers) {
                        this.buffer[i] += buffer.buffer[i];
                    }
                });
    }

    public void scaleInPlace(final double f) {
        this.operateOnIndices(i -> this.buffer[i] *= f);
    }

    public void normalizeInPlace(double f) {
        this.scaleInPlace(f / this.max());
    }

    public void normalizeInPlace() {
        this.normalizeInPlace(1);
    }

    public void normalizeSumInPlace(double f) {
        this.scaleInPlace(f / this.sum());
    }

    public void normalizeSumInPlace() {
        this.normalizeSumInPlace(1);
    }

    protected double[] copyHelper() {
        final double[] array = new double[this.length];

        System.arraycopy(this.buffer, 0, array, 0, this.length);

        return array;
    }

    public DoubleBuffer copy() {
        return new DoubleBuffer(this.copyHelper());
    }
}
