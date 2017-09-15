package org.expasy.glycoforest.writer;

import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.mzjava.glycomics.mol.Anomericity;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GigCondensedWriterTest {

    @Test
    public void testToIupac() throws Exception {


        final SugarStructure structure = new SugarStructure.Builder("", HexNAc).add(Hex, Anomericity.beta, 1, 3)
                .build();

        Assert.assertEquals("Hexb1-3HexNAcol",
                new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupac2() throws Exception {


        final SugarStructure structure = new SugarStructure.Builder("", HexNAc).add(Hex)
                .build();

        Assert.assertEquals("Hex-HexNAcol",
                new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupac3() throws Exception {


        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .add(Hex, Anomericity.beta, 1, 3)
                .add(Neu5Ac, Anomericity.alpha, 2, 3)
                .add(Fuc)
                .build();

        Assert.assertEquals("Fuc-NeuAca2-3Hexb1-3HexNAcol",
                new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupac4() throws Exception {


        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .add(Hex, Anomericity.open, 1, 3)
                .build();

        Assert.assertEquals("Hexo1-3HexNAcol",
                new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupac5() throws Exception {


        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .add(Hex, Optional.of(Anomericity.beta), Optional.of(1), Optional.empty())
                .build();

        Assert.assertEquals("Hexb1-HexNAcol",
                new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupac6() throws Exception {


        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .add(Hex, Optional.empty(), Optional.of(1), Optional.of(3))
                .build();

        Assert.assertEquals("Hex1-3HexNAcol",
                new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupac7() throws Exception {


        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .add(Hex, Optional.of(Anomericity.beta), Optional.empty(), Optional.of(3))
                .build();

        Assert.assertEquals("Hexb-3HexNAcol",
                new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupacBranched() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .branch().add(Hex)
                .pop().add(Neu5Ac)
                .build();

        Assert.assertEquals("NeuAc(Hex)HexNAcol", new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupacBranched2() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .branch().add(Hex, Anomericity.beta, 1, 3)
                .pop().add(Neu5Ac, Anomericity.alpha, 2, 3)
                .build();

        Assert.assertEquals("NeuAca2-3(Hexb1-3)HexNAcol", new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupacBranched3() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .add(HexNAc)
                .branch().add(Hex, Anomericity.beta, 1, 3)
                .pop().add(Neu5Gc, Anomericity.alpha, 2, 3)
                .build();

        Assert.assertEquals("NeuGca2-3(Hexb1-3)HexNAc-HexNAcol", new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupacBranched4() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .add(HexNAc)
                .branch().add(Hex, Anomericity.beta, 1, 3)
                .branch().add(Neu5Ac, Anomericity.alpha, 2, 3)
                .pop().add(Fuc)
                .pop().add(Kdn, Anomericity.alpha, 1, 2)
                .build();

        Assert.assertEquals("Kdna1-2(Fuc(NeuAca2-3)Hexb1-3)HexNAc-HexNAcol", new GigCondensedWriter().write(structure));
    }

    @Test
    public void testToIupacBranched5() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc, Anomericity.beta, 1, 6).add(Hex, Anomericity.beta, 1, 4)
                .pop().add(Hex, Anomericity.beta, 1, 3)
                .add(HexNAc)
                .build();

        Assert.assertEquals("HexNAc-Hexb1-3(Hexb1-4HexNAcb1-6)HexNAcol", new GigCondensedWriter().write(structure));
    }

    // Galb1-3[HexNAc(Gal)Gal-GlcNAcb1-6]GalNAcol
    @Test
    public void testToIupacBranched6() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc, Anomericity.beta, 1, 6).add(Hex)
                .branch().add(Hex)
                .pop().add(HexNAc)
                .pop().add(Hex, Anomericity.beta, 1, 3)
                .build();

        Assert.assertEquals("Hexb1-3(HexNAc(Hex)Hex-HexNAcb1-6)HexNAcol", new GigCondensedWriter().write(structure));
    }


    @Test
    public void testCurratedToIupac() throws Exception {

        String inputStructure = "Galb1-3(NeuGca2-6)GalNAcol";
        String expectedStructure = inputStructure.replace("Gal", "Hex").replace("Glc", "Hex");

        final SugarStructure actualStructure = new GigCondensedReader().readStructure("s1", inputStructure);

        Assert.assertEquals(expectedStructure, new GigCondensedWriter().write(actualStructure));
    }

    @Test
    public void testCurratedToIupac2() throws Exception {

        String inputStructure = "Galb1-3(HexNAc(Gal)Gal-GlcNAcb1-6)GalNAcol";
        String expectedStructure = inputStructure.replace("Gal", "Hex").replace("Glc", "Hex");

        final SugarStructure actualStructure = new GigCondensedReader().readStructure("s1", inputStructure);

        Assert.assertEquals(expectedStructure, new GigCondensedWriter().write(actualStructure));
    }

    @Test
    public void testCurratedDataSetToIpuac() throws Exception {

        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(getClass().getResource("./currated_structures.txt").toURI())));
        GigCondensedReader gigCondensedReader = new GigCondensedReader();
        GigCondensedWriter writer = new GigCondensedWriter();
        String inputStructure;
        String expectedStructure;

        for (String line; (line = bufferedReader.readLine()) != null; ) {

            String[] tokens = line.split(",");
            inputStructure = tokens[0];
            expectedStructure = tokens[1];

            SugarStructure actualStructure = gigCondensedReader.readStructure("structure", inputStructure);

            /*Only replacing the [ brackets to ( for this test to keep the test data as it is. The [ are required for the proper iupac format */
            expectedStructure = expectedStructure.replace("[", "(").replace("]", ")");

            Assert.assertEquals(expectedStructure, writer.write(actualStructure));
        }
    }
}