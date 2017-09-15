package org.expasy.glycoforest.chargestate;

import com.google.common.collect.Lists;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.Mockito.when;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FrequencyChargeEstimatorTest {

    @Test
    public void testEstimateChargeState() throws Exception {

        Assert.assertArrayEquals(new int[]{1}, new FrequencyChargeEstimator().estimateChargeState(Lists.newArrayList(mockPeakList(0))));
        Assert.assertArrayEquals(new int[]{2}, new FrequencyChargeEstimator().estimateChargeState(Lists.newArrayList(
                mockPeakList(0),
                mockPeakList(2),
                mockPeakList(2)
        )));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEstimateChargeStateIllArgument() throws Exception {

        new FrequencyChargeEstimator().estimateChargeState(Collections.emptyList());
    }

    private PeakList mockPeakList(int charge) {

        final PeakList pl = Mockito.mock(PeakList.class);
        when(pl.getPrecursor()).thenReturn(new Peak(733.3, 23, charge));

        return pl;
    }
}