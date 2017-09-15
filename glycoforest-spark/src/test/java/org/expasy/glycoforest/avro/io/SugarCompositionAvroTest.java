package org.expasy.glycoforest.avro.io;

import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.avro.AvroAssert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarCompositionAvroTest {

    @Test
    public void testWrite() throws Exception {

        final SugarComposition sugarComposition = new SugarComposition(SugarUnit.Fuc, SugarUnit.Fuc, SugarUnit.HexNAc);
        final String expectedJson = "{\"composition\":{\"HexNAc\":1,\"Fuc\":2}}";

        AvroAssert.assertRoundTrip(expectedJson, sugarComposition);
    }

    @Test
    public void testCreateRecordFields() throws Exception {

        String expected = "{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"SugarComposition\",\n" +
                "  \"namespace\" : \"org.expasy.glycoforest.mol\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"name\" : \"composition\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"map\",\n" +
                "      \"values\" : \"int\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}";

        AvroAssert.assertSchema(expected, new SugarCompositionWriter());
        AvroAssert.assertSchema(expected, new SugarCompositionReader());
    }
}