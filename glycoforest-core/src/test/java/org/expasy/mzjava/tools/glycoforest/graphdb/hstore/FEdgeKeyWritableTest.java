package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.expasy.mzjava.hadoop.io.MockDataInput;
import org.expasy.mzjava.hadoop.io.MockDataOutput;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FEdgeKeyWritableTest {

    private static final String base64RunIdPresent = "RDdTMwABOjWP5O8HRFCZMqLcNolAYw==";
    private static final String base64RunIdAbsent = "RDdTMwAA";

    @Test
    public void testWrite() throws Exception {

        final FEdgeKeyWritable key = new FEdgeKeyWritable();
        key.setValues(733.3f, EdgeType.MSN_MSN, Optional.of(UUID.fromString("3a358fe4-ef07-4450-9932-a2dc36894063")));

        final MockDataOutput out = new MockDataOutput(22);
        key.write(out);

        Assert.assertEquals(base64RunIdPresent, out.getBase64());
    }

    @Test
    public void testReadFields() throws Exception {


        final FEdgeKeyWritable key = new FEdgeKeyWritable();
        key.readFields(new MockDataInput(base64RunIdPresent));

        Assert.assertEquals(733.3f, key.getMz(), 0.000001);
        Assert.assertEquals(UUID.fromString("3a358fe4-ef07-4450-9932-a2dc36894063"), key.getRunId().get());
        Assert.assertEquals(EdgeType.MSN_MSN, key.getEdgeType());
    }

    @Test
    public void testWriteRunIdAbsent() throws Exception {

        final FEdgeKeyWritable key = new FEdgeKeyWritable();
        key.setValues(733.3f, EdgeType.MSN_MSN, Optional.empty());

        final MockDataOutput out = new MockDataOutput(22);
        key.write(out);

        Assert.assertEquals(base64RunIdAbsent, out.getBase64());
    }

    @Test
    public void testReadFieldsRunIdAbsent() throws Exception {


        final FEdgeKeyWritable key = new FEdgeKeyWritable();
        key.readFields(new MockDataInput(base64RunIdAbsent));

        Assert.assertEquals(733.3f, key.getMz(), 0.000001);
        Assert.assertEquals(Optional.empty(), key.getRunId());
        Assert.assertEquals(EdgeType.MSN_MSN, key.getEdgeType());
    }
}