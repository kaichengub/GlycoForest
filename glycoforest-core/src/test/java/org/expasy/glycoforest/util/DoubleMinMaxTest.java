package org.expasy.glycoforest.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DoubleMinMaxTest {

    @Test
    public void test() throws Exception {

        final DoubleMinMax minMax = new DoubleMinMax();

        minMax.add(3);
        final double delta = 0.0;
        Assert.assertEquals(3, minMax.getMin(), delta);
        Assert.assertEquals(3, minMax.getMax(), delta);

        minMax.add(2);
        Assert.assertEquals(2, minMax.getMin(), delta);
        Assert.assertEquals(3, minMax.getMax(), delta);

        minMax.add(3);
        Assert.assertEquals(2, minMax.getMin(), delta);
        Assert.assertEquals(3, minMax.getMax(), delta);

        minMax.add(4);
        Assert.assertEquals(2, minMax.getMin(), delta);
        Assert.assertEquals(4, minMax.getMax(), delta);
    }
}