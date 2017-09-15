package org.expasy.glycoforest.mol;

import org.junit.Assert;
import org.junit.Test;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MissingFragmentFunctionTest {

    @Test
    public void testMissingFragments() throws Exception {

        final SugarStructure s587 = new SugarStructure.Builder("587", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).build();

        final SugarStructure s749 = new SugarStructure.Builder("749", HexNAc)
                .branch().add(HexNAc).add(Hex)
                .pop().add(Hex).build();

        Assert.assertEquals(0, new MissingFragmentFunction().missingFragments(s587, s749));
    }

    @Test
    public void testMissingFragments2() throws Exception {

        final SugarStructure s587 = new SugarStructure.Builder("587", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).build();

        final SugarStructure s1024 = new SugarStructure.Builder("1024", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).branch().add(Fuc).pop().add(Neu5Ac).build();

        Assert.assertEquals(4, new MissingFragmentFunction().missingFragments(s587, s1024));
    }

    @Test
    public void testMissingFragments3() throws Exception {

        final SugarStructure s587 = new SugarStructure.Builder("587", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).build();

        final SugarStructure s1024 = new SugarStructure.Builder("1024", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).add(Neu5Ac).add(Fuc)
                .build();

        Assert.assertEquals(2, new MissingFragmentFunction().missingFragments(s587, s1024));
    }

    @Test
    public void testMissingFragments4() throws Exception {

        final SugarStructure s587 = new SugarStructure.Builder("587", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).build();

        final SugarStructure s1024 = new SugarStructure.Builder("1024", HexNAc)
                .branch().add(HexNAc).add(Fuc)
                .pop().add(Hex).add(Neu5Ac).build();

        Assert.assertEquals(8, new MissingFragmentFunction().missingFragments(s587, s1024));
    }

    @Test
    public void testMissingFragments5() throws Exception {

        final SugarStructure s1 = new SugarStructure.Builder("s1", HexNAc).branch().add(Hex).add(Neu5Ac)
                .pop().add(HexNAc).add(Hex)
                    .branch().add(HexNAc)
                    .pop().add(Hex)
                .build();

        final SugarStructure s2 = new SugarStructure.Builder("s2", HexNAc).branch().add(Hex)
                .pop().add(HexNAc).add(Hex)
                .branch().add(HexNAc).add(Neu5Ac)
                .pop().add(Hex)
                .build();

        Assert.assertEquals(6, new MissingFragmentFunction().missingFragments(s1, s2));
    }

    @Test
    public void testMissingFragments7() throws Exception {

        final SugarStructure s1 = new SugarStructure.Builder("s1", HexNAc).branch().add(Hex).add(Neu5Ac)
                .pop().add(HexNAc).add(Hex)
                .branch().add(HexNAc)
                .pop().add(Hex)
                .build();

        final SugarStructure s2 = new SugarStructure.Builder("s2", HexNAc).branch().add(Hex)
                .pop().add(HexNAc).add(Hex)
                .branch().add(HexNAc).add(Neu5Ac)
                .pop().add(Hex)
                .build();

        Assert.assertEquals(8, new MissingFragmentFunction().missingFragments(s2, s1));
    }

    @Test
    public void testMissingFragments8() throws Exception {

        final SugarStructure s1 = new SugarStructure.Builder("s1", HexNAc).add(HexNAc)
                .build();

        final SugarStructure s2 = new SugarStructure.Builder("s2", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Fuc)
                .build();

        Assert.assertEquals(0, new MissingFragmentFunction().missingFragments(s1, s2));
    }
}