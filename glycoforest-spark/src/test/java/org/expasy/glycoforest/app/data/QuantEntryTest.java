package org.expasy.glycoforest.app.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.Assert;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class QuantEntryTest extends TestCase {

    public void testJsonWrite() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new QuantEntry(11.64, 6038.5));
        JsonAssert.assertJsonEquals("{\n" +
                "  \"retentionTime\" : 11.64,\n" +
                "  \"intensity\" : 6038.5\n" +
                "}", json);
    }

    public void testJsonRead() throws Exception {

        final QuantEntry entry = new ObjectMapper().reader(QuantEntry.class).readValue("{\n" +
                "  \"retentionTime\" : 11.64,\n" +
                "  \"intensity\" : 6038.5\n" +
                "}");
        Assert.assertEquals(11.64, entry.getRetentionTime(), 0.001);
        Assert.assertEquals(6038.5, entry.getIntensity(), 0.01);
    }
}