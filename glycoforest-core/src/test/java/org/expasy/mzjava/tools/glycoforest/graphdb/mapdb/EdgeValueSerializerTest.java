package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.expasy.mzjava.hadoop.io.MockDataInput;
import org.expasy.mzjava.hadoop.io.MockDataOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeValueSerializerTest {

    @Test
    public void testSerialize() throws Exception {

        final EdgeValueSerializer serializer = new EdgeValueSerializer();

        final MockDataOutput out = new MockDataOutput(serializer.fixedSize());

        serializer.serialize(out, new EdgeValue(UUID.fromString("d30f1747-2d42-4a50-b81b-55d5075d53b8"), UUID.fromString("6d35b40f-1968-4156-ace5-3a7b58ae97ac"), 0.4532157843168651653458f));

        Assert.assertEquals("0w8XRy1CSlC4G1XVB11TuG01tA8ZaEFWrOU6e1iul6w+6Avm", out.getBase64());
        Assert.assertEquals(serializer.fixedSize(), out.getBytes().length);
    }

    @Test
    public void testDeserialize() throws Exception {

        final EdgeValueSerializer serializer = new EdgeValueSerializer();

        final MockDataInput in = new MockDataInput("0w8XRy1CSlC4G1XVB11TuG01tA8ZaEFWrOU6e1iul6w+6Avm");
        final EdgeValue value = serializer.deserialize(in, 36);

        Assert.assertEquals(UUID.fromString("d30f1747-2d42-4a50-b81b-55d5075d53b8"), value.getNode1Id());
        Assert.assertEquals(UUID.fromString("6d35b40f-1968-4156-ace5-3a7b58ae97ac"), value.getNode2Id());
        Assert.assertEquals(0.4532157843168651653458f, value.getScore(), 0.0);
    }
}