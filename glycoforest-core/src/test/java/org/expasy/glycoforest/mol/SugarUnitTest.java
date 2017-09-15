package org.expasy.glycoforest.mol;

import org.junit.Assert;
import org.junit.Test;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarUnitTest {

    /**
     * Hex, Fuc, HexNac & Neu5Ac masses from http://www.ionsource.com/Card/carbo/slmmarker.htm
     */
    @Test
    public void testUniMass() throws Exception {

        final double delta = 0.0001;
        Assert.assertEquals( 79.9568, S.getUnitMass(), delta);
        Assert.assertEquals(146.0579, Fuc.getUnitMass(), delta);
        Assert.assertEquals(162.0528, Hex.getUnitMass(), delta);
        Assert.assertEquals(203.0794, HexNAc.getUnitMass(), delta);
        Assert.assertEquals(250.0689, Kdn.getUnitMass(), delta);
        Assert.assertEquals(291.0954, Neu5Ac.getUnitMass(), delta);
        Assert.assertEquals(307.0903, Neu5Gc.getUnitMass(), delta);
    }
}