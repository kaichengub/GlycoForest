package org.expasy.glycoforest.util;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DoubleMinMax {

    private double min = Double.MAX_VALUE;
    private double max = Double.MIN_VALUE;

    public void add(double d) {

        min = Math.min(min, d);
        max = Math.max(max, d);
    }

    public double getMin() {

        return min;
    }

    public double getMax() {

        return max;
    }
}
