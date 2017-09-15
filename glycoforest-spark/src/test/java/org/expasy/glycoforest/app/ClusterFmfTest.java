package org.expasy.glycoforest.app;

import gnu.trove.map.hash.TIntIntHashMap;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ClusterFmfTest {

    @Test
    public void testExtractCharge() throws Exception {

        final ClusterFmf fmFunction = mock(ClusterFmf.class);
        when(fmFunction.extractCharge(any())).thenCallRealMethod();

        Assert.assertArrayEquals(new int[]{1}, fmFunction.extractCharge(new TIntIntHashMap()));

        TIntIntHashMap chargeMap = new TIntIntHashMap();
        chargeMap.put(1, 5);
        Assert.assertArrayEquals(new int[]{1}, fmFunction.extractCharge(chargeMap));

        chargeMap = new TIntIntHashMap();
        chargeMap.put(1, 5);
        chargeMap.put(2, 3);
        Assert.assertArrayEquals(new int[]{1, 2}, fmFunction.extractCharge(chargeMap));

        chargeMap = new TIntIntHashMap();
        chargeMap.put(0, 2);
        chargeMap.put(1, 3);
        chargeMap.put(2, 4);
        Assert.assertArrayEquals(new int[]{2, 1}, fmFunction.extractCharge(chargeMap));

        chargeMap = new TIntIntHashMap();
        chargeMap.put(0, 3);
        chargeMap.put(1, 3);
        chargeMap.put(2, 4);
        Assert.assertArrayEquals(new int[]{2, 1}, fmFunction.extractCharge(chargeMap));
    }
}