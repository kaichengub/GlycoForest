package org.expasy.glycoforest.glycomod;

import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarUnit;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class TerminatingCountPredicateTest {

    @Test
    public void testTest() throws Exception {

        Predicate<SugarComposition> predicate = new TerminatingCountPredicate(SugarUnit.values());

        Assert.assertEquals(true, predicate.test(new SugarComposition(SugarUnit.HexNAc, SugarUnit.HexNAc, SugarUnit.Fuc)));
        Assert.assertEquals(false, predicate.test(new SugarComposition(SugarUnit.HexNAc, SugarUnit.Fuc, SugarUnit.Fuc)));
    }
}