package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.expasy.mzjava.hadoop.io.MockDataInput;
import org.expasy.mzjava.hadoop.io.MockDataOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FEdgeValueWritableTest {

    private static final String base64 = "RAgZMYQoRDu4sIvutHjkXOtcrT0g6UvaqjT/ylfCaJk/YBrf";

    @Test
    public void testWrite() throws Exception {

        final FEdgeValueWritable value = new FEdgeValueWritable();
        value.setValues(UUID.fromString("44081931-8428-443b-b8b0-8beeb478e45c"), UUID.fromString("eb5cad3d-20e9-4bda-aa34-ffca57c26899"), 0.87541f);

        final MockDataOutput out = new MockDataOutput(36);
        value.write(out);

        Assert.assertEquals(base64, out.getBase64());
    }

    @Test
    public void testReadFields() throws Exception {

        final FEdgeValueWritable value = new FEdgeValueWritable();
        value.readFields(new MockDataInput(base64));

        Assert.assertEquals(UUID.fromString("44081931-8428-443b-b8b0-8beeb478e45c"), value.getNode1Id());
        Assert.assertEquals(UUID.fromString("eb5cad3d-20e9-4bda-aa34-ffca57c26899"), value.getNode2Id());
        Assert.assertEquals(0.87541f, value.getScore(), 0.000000001);
    }
}