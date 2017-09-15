package org.expasy.glycoforest.app.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureQuantEntryTest {

    @Test
    public void testJsonWrite() throws Exception {

        HashMap<String, QuantEntry> map = new HashMap<>();

        map.put("S1", new QuantEntry(14.93, 40060.8));
        map.put("S2", new QuantEntry(15.28, 77341.5));
        map.put("S3", new QuantEntry(15.55, 51097.2));

        final StructureQuantEntry entry = new StructureQuantEntry("425a", "N2", "GalNAca1-3GalNAcol", map);

        final ObjectMapper mapper = new ObjectMapper();

        final String expectedJson = "{\n" +
                "  \"name\" : \"425a\",\n" +
                "  \"composition\" : \"N2\",\n" +
                "  \"structure\" : \"GalNAca1-3GalNAcol\",\n" +
                "  \"quantMap\" : {\n" +
                "    \"S3\" : {\n" +
                "      \"retentionTime\" : 15.55,\n" +
                "      \"intensity\" : 51097.2\n" +
                "    },\n" +
                "    \"S1\" : {\n" +
                "      \"retentionTime\" : 14.93,\n" +
                "      \"intensity\" : 40060.8\n" +
                "    },\n" +
                "    \"S2\" : {\n" +
                "      \"retentionTime\" : 15.28,\n" +
                "      \"intensity\" : 77341.5\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JsonAssert.assertJsonEquals(expectedJson, mapper.writeValueAsString(entry));
    }

    @Test
    public void testJsonRead() throws Exception {

        final StructureQuantEntry entry = new ObjectMapper().reader(StructureQuantEntry.class).readValue("{\n" +
                "  \"name\" : \"425a\",\n" +
                "  \"composition\" : \"N2\",\n" +
                "  \"structure\" : \"GalNAca1-3GalNAcol\",\n" +
                "  \"quantMap\" : {\n" +
                "    \"S3\" : {\n" +
                "      \"retentionTime\" : 15.55,\n" +
                "      \"intensity\" : 51097.2\n" +
                "    },\n" +
                "    \"S1\" : {\n" +
                "      \"retentionTime\" : 14.93,\n" +
                "      \"intensity\" : 40060.8\n" +
                "    },\n" +
                "    \"S2\" : {\n" +
                "      \"retentionTime\" : 15.28,\n" +
                "      \"intensity\" : 77341.5\n" +
                "    }\n" +
                "  }\n" +
                "}");

        Assert.assertEquals("425a", entry.getName());
        Assert.assertEquals("N2", entry.getComposition());
        Assert.assertEquals("GalNAca1-3GalNAcol", entry.getStructure());

        Map<String, QuantEntry> quantMap = entry.getQuantMap();
        Assert.assertEquals(3, quantMap.size());
    }
}