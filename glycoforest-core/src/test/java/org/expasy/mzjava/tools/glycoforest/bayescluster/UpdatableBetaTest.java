package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class UpdatableBetaTest {

    @Test
    public void testUpdateEmpty() throws Exception {

        UpdatableBeta prior = new UpdatableBeta(0.85, 20, UpdatableBeta.Type.VERTEX);
        prior.update(Collections.<SimEdge<String>>emptySet());

        final double delta = 0.00001;
        Assert.assertEquals(0.0, prior.getSampleMean(), delta);
        Assert.assertEquals(0.0, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.85, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.12895, prior.getPosteriorStandardDeviation(), delta);
    }

    @Test
    public void testUpdate1() throws Exception {

        List<SimEdge<String>> instances = new ArrayList<>();
        instances.add(new SimEdge<>("A1", "A2", 0.5));
        instances.add(new SimEdge<>("A2", "B1", 0.51));

        UpdatableBeta prior = new UpdatableBeta(0.85, 20, UpdatableBeta.Type.VERTEX);
        prior.update(instances);

        final double delta = 0.00001;
        Assert.assertEquals(0.505, prior.getSampleMean(), delta);
        Assert.assertEquals(0.00707, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.8186363, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.1509746, prior.getPosteriorStandardDeviation(), delta);
    }

    @Test
    public void testUpdate2() throws Exception {

        List<SimEdge<String>> instances = new ArrayList<>();
        instances.add(new SimEdge<>("A1", "A2", 0.5));
        instances.add(new SimEdge<>("A2", "B1", 0.51));

        UpdatableBeta prior = new UpdatableBeta(0.85, 2, UpdatableBeta.Type.VERTEX);
        prior.update(instances);

        final double delta = 0.00001;
        Assert.assertEquals(0.505, prior.getSampleMean(), delta);
        Assert.assertEquals(0.00707, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.6775, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.23083, prior.getPosteriorStandardDeviation(), delta);
    }
}