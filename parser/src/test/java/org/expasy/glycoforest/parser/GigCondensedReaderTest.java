package org.expasy.glycoforest.parser;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.glycomics.mol.Anomericity;
import org.junit.Assert;
import org.junit.Test;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GigCondensedReaderTest {

    @Test
    public void test() throws Exception {

        String input = "Galb1-3[HexNAc(Gal)Galb1-4GlcNAcb1-6]GalNAcol";

        SugarStructure structure = new GigCondensedReader().readStructure("test", input);
        Assert.assertEquals("test", structure.getLabel());
        Assert.assertEquals(true, structure.isIsomorphic(new SugarStructure.Builder("expected", HexNAc)
                .branch().add(HexNAc, Anomericity.beta, 1, 6).add(Hex, Anomericity.beta, 1, 4).branch()
                .add(Hex)
                .pop().add(HexNAc)
                .pop().add(Hex, Anomericity.beta, 1, 3)
                .build(),
                IsomorphismType.LINKAGE
        ));
    }

    @Test
    public void test2() throws Exception {

        String input = "2(Fuc-HexNAc)+Gal+Galb1-3[HexNAc(Gal)Galb1-4GlcNAcb1-6]GalNAcol";

        SugarStructure structure = new GigCondensedReader().readStructure("test", input);
        Assert.assertEquals("test", structure.getLabel());
        Assert.assertEquals(true, structure.isIsomorphic(new SugarStructure.Builder("expected", HexNAc).branch().add(Hex, Anomericity.beta, 1, 3)
                .pop().add(HexNAc, Anomericity.beta, 1, 6).add(Hex, Anomericity.beta, 1, 4).branch().add(HexNAc)
                .pop()
                .add(Hex)
                .build(),
                IsomorphismType.LINKAGE
        ));
    }

    @Test
    public void testS() throws Exception {

        String input = "Fuca1-2Galb1-3(S0-6GlcNAcb1-6)GalNAcol";

        SugarStructure structure = new GigCondensedReader().readStructure("test", input);
        Assert.assertEquals("test", structure.getLabel());
        Assert.assertEquals(true, structure.isIsomorphic(new SugarStructure.Builder("expected", HexNAc).branch().add(Hex, Anomericity.beta, 1, 3).add(Fuc, Anomericity.alpha, 1, 2)
                .pop().add(HexNAc, Anomericity.beta, 1, 6).add(S, null, 0, 6)
                .build(),
                IsomorphismType.LINKAGE
        ));
    }

    @Test
    public void testNoLinkageInfo() throws Exception {

        String input = "GalGalNAcol";

        SugarStructure structure = new GigCondensedReader().readStructure("test", input);
        Assert.assertEquals("test", structure.getLabel());
        Assert.assertEquals(true, structure.isIsomorphic(new SugarStructure.Builder("expected", HexNAc).add(Hex).build(), IsomorphismType.LINKAGE));
    }

    @Test
    public void testNoLinkageInfo2() throws Exception {

        String input = "HexHexNAcol";

        SugarStructure structure = new GigCondensedReader().readStructure("test", input);
        Assert.assertEquals("test", structure.getLabel());
        Assert.assertEquals(true, structure.isIsomorphic(new SugarStructure.Builder("expected", HexNAc).add(Hex).build(), IsomorphismType.LINKAGE));
    }

    @Test(expected = IllegalStateException.class)
    public void testError() throws Exception {

        String input = "GalAcol";

        new GigCondensedReader().readStructure("test", input);
    }

    @Test(expected = IllegalStateException.class)
    public void testError2() throws Exception {

        String input = "Fuc-GalNAc-Galb1-3[Gal(Fuc)GlcNAcb1-6])GalNAcol";

        new GigCondensedReader().readStructure("test", input);
    }
}
