package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.expasy.mzjava.core.ms.cluster.SimEdge;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdatableNormalTest {

    @Test
    public void testUpdateEmpty() throws Exception {

        UpdatableNormal prior = new UpdatableNormal(0.85, 0.1 * 0.1, 0.05 * 0.05);
        prior.update(Collections.<SimEdge<String>>emptySet());

        final double delta = 0.00001;
        Assert.assertEquals(0.0, prior.getSampleMean(), delta);
        Assert.assertEquals(0.0, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.85, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.05, prior.getPosteriorStandardDeviation(), delta);
    }

    @Test
    public void testUpdate1() throws Exception {

        List<SimEdge<String>> instances = new ArrayList<>();
        instances.add(new SimEdge<>("A1", "A2", 0.5));
        instances.add(new SimEdge<>("A2", "B1", 0.51));

        UpdatableNormal prior = new UpdatableNormal(0.85, 0.1 * 0.1, 0.05 * 0.05);
        prior.update(instances);

        final double delta = 0.00001;
        Assert.assertEquals(0.505, prior.getSampleMean(), delta);
        Assert.assertEquals(0.00707, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.543333333333333, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.05, prior.getPosteriorStandardDeviation(), delta);
    }

    @Test
    public void testUpdate2() throws Exception {

        List<SimEdge<String>> instances = new ArrayList<>();
        instances.add(new SimEdge<>("A1", "A2", 0.5));
        instances.add(new SimEdge<>("A2", "B1", 0.51));

        UpdatableNormal prior = new UpdatableNormal(0.5, 0.1 * 0.1, 0.01 * 0.01);
        prior.update(instances);

        final double delta = 0.00001;
        Assert.assertEquals(0.505, prior.getSampleMean(), delta);
        Assert.assertEquals(0.00707, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.504975124378109, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.01, prior.getPosteriorStandardDeviation(), delta);
    }

    @Test
    public void testUpdate3() throws Exception {

        List<SimEdge<String>> instances = new ArrayList<>();
        instances.add(new SimEdge<>("A1", "A2", 0.5));

        UpdatableNormal prior = new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01);
        prior.update(instances);

        final double delta = 0.00001;
        Assert.assertEquals(0.5, prior.getSampleMean(), delta);
        Assert.assertEquals(0.0, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.503465346534653, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.01, prior.getPosteriorStandardDeviation(), delta);
    }

    @Test
    public void testMinPosterior() throws Exception {

        List<SimEdge<String>> instances = new ArrayList<>();
        instances.add(new SimEdge<>("A1", "A2", 0.5));

        UpdatableNormal prior = new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.6, 1.0);
        prior.update(instances);

        final double delta = 0.00001;
        Assert.assertEquals(0.5, prior.getSampleMean(), delta);
        Assert.assertEquals(0.0, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.6, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.01, prior.getPosteriorStandardDeviation(), delta);
    }

    @Test
    public void testMaxPosterior() throws Exception {

        List<SimEdge<String>> instances = new ArrayList<>();
        instances.add(new SimEdge<>("A1", "A2", 1.0));

        UpdatableNormal prior = new UpdatableNormal(0.85, 0.1 * 0.1, 0.01 * 0.01, 0.0, 0.9);
        prior.update(instances);

        final double delta = 0.00001;
        Assert.assertEquals(1.0, prior.getSampleMean(), delta);
        Assert.assertEquals(0.0, prior.getSampleStandardDeviation(), delta);

        Assert.assertEquals(0.9, prior.getPosteriorMean(), delta);
        Assert.assertEquals(0.01, prior.getPosteriorStandardDeviation(), delta);
    }
}