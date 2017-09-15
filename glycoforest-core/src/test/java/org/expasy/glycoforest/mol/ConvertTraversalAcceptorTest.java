package org.expasy.glycoforest.mol;

import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.glycomics.io.mol.glycoct.GlycoCTReader;
import org.expasy.mzjava.glycomics.mol.Glycan;
import org.junit.Assert;
import org.junit.Test;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ConvertTraversalAcceptorTest {

    @Test
    public void testToGlycanNeu5Ac_HexNAcol() throws Exception {

        Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "3s:n-acetyl\n" +
                "4s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+2)2d\n" +
                "2:2d(5+1)3n\n" +
                "3:1d(2+1)4n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Neu5Ac).build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanNeu5Gc__Hex__HexNAcol() throws Exception {

        Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-HEX-1:5\n" +
                "3b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "4s:n-glycolyl\n" +
                "5s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:1o(-1+2)3d\n" +
                "3:3d(5+1)4n\n" +
                "4:1d(2+1)5n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc)
                .branch().add(Hex)
                .pop().add(Neu5Gc)
                .build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanHex_HexNAcol() throws Exception {

        Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-HEX-1:5\n" +
                "3s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:1d(2+1)3n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanKdn_HexNAcol() throws Exception {

        Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "3s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+2)2d\n" +
                "2:1d(2+1)3n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Kdn).build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanHexNAc_HexNAcol() throws Exception {

        Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-HEX-1:5\n" +
                "3s:n-acetyl\n" +
                "4s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:2d(2+1)3n\n" +
                "3:1d(2+1)4n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(HexNAc).build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanFuc_HexNAcol() throws Exception {

        Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-lgal-HEX-1:5|6:d\n" +
                "3s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:1d(2+1)3n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Fuc).build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void test1081d() throws Exception {

        Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-HEX-1:5\n" +
                "3b:x-HEX-1:5\n" +
                "4b:x-HEX-1:5\n" +
                "5s:n-acetyl\n" +
                "6s:n-acetyl\n" +
                "7b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "8s:n-acetyl\n" +
                "9s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:2o(-1+1)3d\n" +
                "3:2o(-1+1)4d\n" +
                "4:4d(2+1)5n\n" +
                "5:2d(2+1)6n\n" +
                "6:1o(-1+2)7d\n" +
                "7:7d(5+1)8n\n" +
                "8:1d(2+1)9n", "1081d");

        final SugarStructure structure = new SugarStructure.Builder("1081d", HexNAc)
                .branch().add(Neu5Ac).pop()
                .add(HexNAc)
                .branch().add(Hex).pop()
                .add(HexNAc).build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testConvertSulfated() throws Exception {

        final Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2s:sulfate\n" +
                "3s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2n\n" +
                "2:1d(2+1)3n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(S).build();
        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanWithNeu5Ac() throws Exception {

        final Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-HEX-1:5\n" +
                "3b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "4s:n-acetyl\n" +
                "5s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:1o(-1+2)3d\n" +
                "3:3d(5+1)4n\n" +
                "4:1d(2+1)5n", "test");

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).branch().add(Hex)
                .pop().add(Neu5Ac)
                .build();

        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(structure.getLabel(), Composition.parseComposition("H2"));
        structure.dfs(converter);
        final Glycan glycan = converter.build();

        Assert.assertEquals(expected, glycan);
    }
}