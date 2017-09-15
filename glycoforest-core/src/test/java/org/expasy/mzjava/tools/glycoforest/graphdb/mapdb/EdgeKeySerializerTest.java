package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.expasy.mzjava.hadoop.io.MockDataInput;
import org.expasy.mzjava.hadoop.io.MockDataOutput;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeKeySerializerTest {

    @Test
    public void testSerialize() throws Exception {

        final EdgeKeySerializer serializer = new EdgeKeySerializer();

        final MockDataOutput out = new MockDataOutput(serializer.fixedSize());
        serializer.serialize(out, new EdgeKey(733.3249234575f, 3));

        Assert.assertEquals("RDdUzAAAAAM=", out.getBase64());
        Assert.assertEquals(serializer.fixedSize(), out.getBytes().length);
    }

    @Test
    public void testDeserialize() throws Exception {

        final EdgeKeySerializer serializer = new EdgeKeySerializer();

        final MockDataInput in = new MockDataInput("RDdUzAAAAAM=");
        EdgeKey key = serializer.deserialize(in, 8);

        Assert.assertEquals(733.3249234575f, key.getMz(), 0.0);
        Assert.assertEquals(3, key.getCount());
    }
}