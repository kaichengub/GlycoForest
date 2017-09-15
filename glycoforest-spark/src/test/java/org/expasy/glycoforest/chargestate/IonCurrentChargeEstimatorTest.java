package org.expasy.glycoforest.chargestate;

import com.google.common.collect.Lists;
import org.expasy.mzjava.core.ms.peaklist.DoublePeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class IonCurrentChargeEstimatorTest {

    @Test
    public void testEstimateChargeState1() throws Exception {

        final PeakList pl1 = new DoublePeakList<>();
        pl1.getPrecursor().setValues(10, 23, 0);
        pl1.addSorted(new double[]{1, 2, 3, 10}, new double[]{1, 1, 1, 1});

        IonCurrentChargeEstimator chargeEstimator = new IonCurrentChargeEstimator(0.15, 0);

        Assert.assertArrayEquals(new int[]{1}, chargeEstimator.estimateChargeState(Collections.singleton(pl1)));
    }

    @Test
    public void testEstimateChargeState2() throws Exception {

        final PeakList pl1 = new DoublePeakList<>();
        pl1.getPrecursor().setValues(10, 23, 0);
        pl1.addSorted(new double[]{1, 2, 3, 10}, new double[]{10, 10, 10, 10});

        final PeakList pl2 = new DoublePeakList<>();
        pl2.getPrecursor().setValues(10, 87, 0);
        pl2.addSorted(new double[]{1, 2, 4, 12}, new double[]{10, 10, 10, 1});

        IonCurrentChargeEstimator chargeEstimator = new IonCurrentChargeEstimator(0.15, 0);

        Assert.assertArrayEquals(new int[]{1}, chargeEstimator.estimateChargeState(Collections.singleton(pl1)));
    }

    @Test
    public void testEstimateChargeState3() throws Exception {

        final PeakList pl1 = new DoublePeakList<>();
        pl1.getPrecursor().setValues(10, 23, 0);
        pl1.addSorted(new double[]{1, 2, 3, 10}, new double[]{10, 10, 10, 10});

        final PeakList pl2 = new DoublePeakList<>();
        pl2.getPrecursor().setValues(10, 87, 0);
        pl2.addSorted(new double[]{1, 2, 4, 16}, new double[]{10, 10, 10, 50});

        IonCurrentChargeEstimator chargeEstimator = new IonCurrentChargeEstimator(0.15, 0);

        Assert.assertArrayEquals(new int[]{2}, chargeEstimator.estimateChargeState(Lists.newArrayList(pl1, pl2)));
    }
}