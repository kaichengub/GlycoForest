package org.expasy.mzjava.tools.glycoforest.wrcluster;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.ScanNumberInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeMergePredicateTest {

    @Test
    public void testTest() throws Exception {

        final EdgeMergePredicate predicate = new EdgeMergePredicate(3);

        final UUID run1 = UUID.randomUUID();
        final UUID run2 = UUID.randomUUID();
        Assert.assertEquals(true, predicate.test(new SimEdge<>(mockConsensus(34.4, 36.2, run1), mockConsensus(37.1, 56.6, run1), 0.8f)));
        Assert.assertEquals(true, predicate.test(new SimEdge<>(mockConsensus(37.1, 56.6, run1), mockConsensus(34.4, 36.2, run1), 0.8f)));

        Assert.assertEquals(false, predicate.test(new SimEdge<>(mockConsensus(34.4, 36.2, run1), mockConsensus(47.1, 56.6, run1), 0.8f)));
        Assert.assertEquals(false, predicate.test(new SimEdge<>(mockConsensus(47.1, 56.6, run1), mockConsensus(34.4, 36.2, run1), 0.8f)));

        //Check that different nodes from different runs can always be merged
        Assert.assertEquals(true, predicate.test(new SimEdge<>(mockConsensus(34.4, 36.2, run1), mockConsensus(47.1, 56.6, run2), 0.8f)));
        Assert.assertEquals(true, predicate.test(new SimEdge<>(mockConsensus(47.1, 56.6, run2), mockConsensus(34.4, 36.2, run1), 0.8f)));
    }

    private WithinRunConsensus mockConsensus(double minRt, double maxRt, UUID runId) {

        final SummaryStatistics scoreStats = new SummaryStatistics();
        scoreStats.addValue(0.9);
        scoreStats.addValue(0.88);

        final SummaryStatistics mzStats = new SummaryStatistics();
        mzStats.addValue(733.32);
        mzStats.addValue(733.29);

        double totalIonCurrent = 3418.98;

        final ScanNumberInterval scanNumberInterval = new ScanNumberInterval(463, 521);
        final RetentionTimeInterval retentionTimeInterval = new RetentionTimeInterval(minRt, maxRt, TimeUnit.SECOND);

        return new WithinRunConsensus(UUID.randomUUID(), runId, scoreStats, totalIonCurrent, mzStats, new int[]{1}, scanNumberInterval, retentionTimeInterval, PeakList.Precision.FLOAT);
    }
}