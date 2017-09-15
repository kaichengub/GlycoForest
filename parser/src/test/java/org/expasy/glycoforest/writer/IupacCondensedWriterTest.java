package org.expasy.glycoforest.writer;

import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class IupacCondensedWriterTest {

    @Test
    public void testIupacWriter() throws Exception{

       // String inputStructure = "HexNAc(b1-6)[Hex(b1-3)]HexNAc";
        String inputStructure = "NeuAca2-6GalNAcol";
        String expectedStructure = "NeuAc(a2-6)HexNAc";

        final SugarStructure actualStructure = new GigCondensedReader().readStructure("s1", inputStructure);

        Assert.assertEquals(expectedStructure, new IupacCondensedWriter().write(actualStructure));
    }


    @Test
    public void testIupacWriterNoLinkageInfo() throws Exception{

        String inputStructure = "NeuAc-GalNAcol";
        String expectedStructure = "NeuAc(-)HexNAc";

        final SugarStructure actualStructure = new GigCondensedReader().readStructure("s2", inputStructure);

        Assert.assertEquals(expectedStructure, new IupacCondensedWriter().write(actualStructure));
    }

    @Test
    public void testIupacWriterBranched() throws Exception{

        String inputStructure = "Galb1-3(NeuGca2-6)GalNAcol";
        String expectedStructure = "Hex(b1-3)[NeuGc(a2-6)]HexNAc";

        final SugarStructure actualStructure = new GigCondensedReader().readStructure("s3", inputStructure);

        Assert.assertEquals(expectedStructure, new IupacCondensedWriter().write(actualStructure));
    }

    @Test
    public void testIupacWriterBranched2() throws Exception{

        String inputStructure = "Galb1-3[Gal(Fuc)GlcNAcb1-6]GalNAcol";
        String expectedStructure = "Hex(b1-3)[Hex(-)[Fuc(-)]HexNAc(b1-6)]HexNAc";

        final SugarStructure actualStructure = new GigCondensedReader().readStructure("s3", inputStructure);

        Assert.assertEquals(expectedStructure, new IupacCondensedWriter().write(actualStructure));
    }

    @Test
    public void testIupacWriterSulfur() throws Exception{

        String inputStructure = "Galb1-3[(S)Galb1-4GlcNAcb1-6]GalNAcol";
        String expectedStructure = "Hex(b1-3)[S(6)Hex(b1-4)HexNAc(b1-6)]HexNAc";

        final SugarStructure actualStructure = new GigCondensedReader().readStructure("s4", inputStructure);

        Assert.assertEquals(expectedStructure, new IupacCondensedWriter().write(actualStructure));
    }




}