package org.expasy.glycoforest.avro.io;

import net.javacrumbs.jsonunit.core.Configuration;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.avro.AvroAssert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructureAvroTest {

    @Test
    public void testRoundTrip() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", SugarUnit.HexNAc).add(SugarUnit.Hex).build();

        final String expected = "{\n" +
                "  \"label\" : \"test\",\n" +
                "  \"structure\" : \"Hex-HexNAcol\"\n" +
                "}";

        AvroAssert.assertRoundTrip(expected, structure, Configuration.empty(), (s1, s2) -> s1.isIsomorphic(s2, IsomorphismType.ROOTED_LINKAGE));
    }

    @Test
    public void testRoundTripBranched() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", SugarUnit.HexNAc).branch().add(SugarUnit.Hex).pop().add(SugarUnit.Fuc).build();

        final String expected = "{\n" +
                "  \"label\" : \"test\",\n" +
                "  \"structure\" : \"Fuc(Hex)HexNAcol\"\n" +
                "}";

        AvroAssert.assertRoundTrip(expected, structure, Configuration.empty(), (s1, s2) -> s1.isIsomorphic(s2, IsomorphismType.ROOTED_LINKAGE));
    }

    @Test
    public void testRoundTripBranched2() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", SugarUnit.HexNAc).branch().add(SugarUnit.Fuc).pop().add(SugarUnit.Hex).build();

        final String expected = "{\n" +
                "  \"label\" : \"test\",\n" +
                "  \"structure\" : \"Hex(Fuc)HexNAcol\"\n" +
                "}";

        AvroAssert.assertRoundTrip(expected, structure, Configuration.empty(), (s1, s2) -> s1.isIsomorphic(s2, IsomorphismType.ROOTED_LINKAGE));
    }

    @Test
    public void testCreateSchema() throws Exception {

        String expected = "{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"SugarStructure\",\n" +
                "  \"namespace\" : \"org.expasy.glycoforest.mol\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"name\" : \"label\",\n" +
                "    \"type\" : \"string\"\n" +
                "  }, {\n" +
                "    \"name\" : \"structure\",\n" +
                "    \"type\" : \"string\"\n" +
                "  } ]\n" +
                "}";

        AvroAssert.assertSchema(expected, new SugarStructureWriter());
        AvroAssert.assertSchema(expected, new SugarStructureReader());
    }
}