package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarComposition;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructureDBTest {

    @Test
    public void testBuilder() throws Exception {

        final String json = "{" +
                "\"50\": \"RES\\n1b:b-dglc-HEX-1:5\\n2s:n-acetyl\\n3b:a-lgal-HEX-1:5|6:d\\n4b:b-dgal-HEX-1:5\\nLIN\\n1:1d(2+1)2n\\n2:1o(3+1)3d\\n3:1o(4+1)4d\",\n" +
                "\"90\": \"RES\\n1b:o-dgal-HEX-0:0|1:aldi\\n2s:n-acetyl\\n3b:b-dgal-HEX-1:5\\n4b:a-lgal-HEX-1:5|6:d\\nLIN\\n1:1d(2+1)2n\\n2:1o(3+1)3d\\n3:3o(2+1)4d\",\n" +
                "\"138\": \"RES\\n1b:x-dgal-HEX-1:5\\n2b:a-dgal-HEX-1:5\\nLIN\\n1:1o(3+1)2d\"" +
                "}";
        final SugarStructureDB graphDB = new SugarStructureDB.Builder().addGlycoCT(new StringReader(json)).build();
        Assert.assertEquals(3, graphDB.getSize());

        final SugarStructureDB graphDB2 = new SugarStructureDB.Builder(graphDB).addNonIsomorphicGlycoCT(new StringReader(json)).build();
        Assert.assertEquals(3, graphDB2.getSize());
    }

    @Test
    public void testGet() throws Exception {

        final String json = "{" +
                "\"50\": \"RES\\n1b:b-dglc-HEX-1:5\\n2s:n-acetyl\\n3b:a-lgal-HEX-1:5|6:d\\n4b:b-dgal-HEX-1:5\\nLIN\\n1:1d(2+1)2n\\n2:1o(3+1)3d\\n3:1o(4+1)4d\",\n" +
                "\"90\": \"RES\\n1b:o-dgal-HEX-0:0|1:aldi\\n2s:n-acetyl\\n3b:b-dgal-HEX-1:5\\n4b:a-lgal-HEX-1:5|6:d\\nLIN\\n1:1d(2+1)2n\\n2:1o(3+1)3d\\n3:3o(2+1)4d\",\n" +
                "\"138\": \"RES\\n1b:x-dgal-HEX-1:5\\n2b:a-dgal-HEX-1:5\\nLIN\\n1:1o(3+1)2d\"" +
                "}";
        final SugarStructureDB graphDB = new SugarStructureDB.Builder().addGlycoCT(new StringReader(json)).build();

        Assert.assertEquals(0, graphDB.streamFor(new SugarComposition()).count());
        Assert.assertEquals(1, graphDB.streamFor(new SugarComposition(Hex, Hex)).count());
        Assert.assertEquals(2, graphDB.streamFor(new SugarComposition(HexNAc, Hex, Fuc)).count());
    }
}