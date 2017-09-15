package org.expasy.glycoforest.parser;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.solver.SugarStructureDB;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import static org.expasy.glycoforest.mol.SugarUnit.HexNAc;
import static org.expasy.glycoforest.mol.SugarUnit.Kdn;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GigCondensedDbReaderTest {

    @Test
    public void testReadDb() throws Exception {

        //\u03b1 is ? \u03b2 is ?
        String input = "1041-1,Fuc\u03b11-2Gal\u03b21-3[Gal(Fuc)GlcNAc\u03b21-6]GalNAcol\n" +
                "1974,2(Fuc-HexNAc)+Gal+Galb1-3[HexNAc(Gal)Galb1-4GlcNAcb1-6]GalNAcol\n";

        SugarStructureDB db = new GigCondensedDbReader().readDb(new StringReader(input));

        Assert.assertEquals("1041-1", db.get("1041-1").get().getLabel());
        Assert.assertEquals("1974", db.get("1974").get().getLabel());
    }

    @Test
    public void testReadFile() throws Exception {

        FileReader reader = new FileReader(new File(getClass().getResource("./structures.csv").toURI()));//"C:\\Users\\ohorlach\\Documents\\tmp\\fish_mucin\\structures.csv");
        SugarStructureDB db = new GigCondensedDbReader().readDb(reader);
        
        Assert.assertEquals(true, db.get("472").get().isIsomorphic(new SugarStructure.Builder("expected", HexNAc).add(Kdn).build(), IsomorphismType.TOPOLOGY));
        final SugarStructure s675 = db.get("675").get();
        Assert.assertEquals(true, s675.isIsomorphic(new SugarStructure.Builder("expected", HexNAc).branch()
                .add(Kdn)
                .pop().add(HexNAc)
                .build(),
                IsomorphismType.TOPOLOGY));
    }
}