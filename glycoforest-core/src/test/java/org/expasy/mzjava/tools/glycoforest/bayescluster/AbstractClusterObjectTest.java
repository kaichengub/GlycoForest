package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class AbstractClusterObjectTest {

    @Test
    public void testEstimate1() throws Exception {

        //noinspection unchecked
        MockClusterObject clusterObject = new MockClusterObject(new UpdatableNormal(0.85, 0.01, 0.0025, 0.0, 1.0), Arrays.asList(
                new SimEdge<>("A1", "A2", 0.5),
                new SimEdge<>("A2", "B1", 0.51))
        );

        final double delta = 0.00001;
        Assert.assertEquals(0.505, clusterObject.getSampleMean(), delta);
        Assert.assertEquals(0.00707, clusterObject.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.543333333333333, clusterObject.getEstimatedMean(), delta);
        Assert.assertEquals(0.05, clusterObject.getEstimatedStandardDeviation(), delta);
    }

    @Test
    public void testEstimate2() throws Exception {

        //noinspection unchecked
        MockClusterObject clusterObject = new MockClusterObject(new UpdatableNormal(0.5, 0.01, 0.0025, 0.0, 1.0), Arrays.asList(
                new SimEdge<>("A1", "A2", 0.5),
                new SimEdge<>("A2", "B1", 0.51))
        );

        final double delta = 0.00001;
        Assert.assertEquals(0.505, clusterObject.getSampleMean(), delta);
        Assert.assertEquals(0.00707, clusterObject.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.5044444444444445, clusterObject.getEstimatedMean(), delta);
        Assert.assertEquals(0.05, clusterObject.getEstimatedStandardDeviation(), delta);
    }

    @Test
    public void testEstimate3() throws Exception {

        MockClusterObject clusterObject = new MockClusterObject(new UpdatableNormal(0.85, 0.01, 0.0025, 0.0, 1.0), Collections.singletonList(
                new SimEdge<>("A1", "A2", 0.5))
        );

        final double delta = 0.00001;
        Assert.assertEquals(0.5, clusterObject.getSampleMean(), delta);
        Assert.assertEquals(0.0, clusterObject.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.57, clusterObject.getEstimatedMean(), delta);
        Assert.assertEquals(0.05, clusterObject.getEstimatedStandardDeviation(), delta);
    }

    @Test
    public void testCalculateLikelihoods() throws Exception {

        UpdatableNormal prior = new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.0, 1.0);
        //noinspection unchecked
        MockClusterObject clusterObject = new MockClusterObject(prior, Arrays.asList(
                new SimEdge<>("A1", "A2", 0.5),
                new SimEdge<>("A2", "B1", 0.51))
        );

        Assert.assertEquals(7.093002401133779, clusterObject.getSumOfLogLikelihoods(), 0.000000001);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testForEachInstance() throws Exception {

        final SimEdge<String> edge1 = mockEdge(0.8);
        final SimEdge<String> edge2 = mockEdge(0.8);

        final UpdatableNormal prior = mock(UpdatableNormal.class);
        when(prior.pdf(anyDouble())).thenReturn(0.07);

        MockClusterObject clusterObject = new MockClusterObject(prior, Arrays.asList(edge1, edge2));

        Consumer<SimEdge<String>> consumer = mock(Consumer.class);
        clusterObject.forEachInstance(consumer);
        verify(consumer).accept(edge1);
        verify(consumer).accept(edge2);
        verifyNoMoreInteractions(consumer);
    }

    private SimEdge mockEdge(double score) {

        final SimEdge simEdge = mock(SimEdge.class);
        when(simEdge.getScore()).thenReturn(score);
        return simEdge;
    }

    private static class MockClusterObject extends AbstractClusterObject<String> {

        public MockClusterObject(UpdatableNormal prior, Collection<SimEdge<String>> edges) {

            //noinspection unchecked
            super(edges, prior, 1.0);
        }
    }
}