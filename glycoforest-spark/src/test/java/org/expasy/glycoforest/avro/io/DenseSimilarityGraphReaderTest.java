package org.expasy.glycoforest.avro.io;

import com.google.common.collect.Lists;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class DenseSimilarityGraphReaderTest {

    @Test
    public void testReadGraph() throws Exception {

        MockSimGraphReader reader = new MockSimGraphReader();
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\n" +
                "  \"members\" : [ {\n" +
                "    \"composition\" : {\n" +
                "      \"Hex\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"composition\" : {\n" +
                "      \"HexNAc\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"composition\" : {\n" +
                "      \"Fuc\" : 1\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"composition\" : {\n" +
                "      \"Kdn\" : 1\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"scores\" : [ 0.8, 0.0, 0.0, 0.7, 0.0, 0.6 ]\n" +
                "}");

        final SimilarityGraph<SugarComposition> graph = reader.read(in);
        List<SugarComposition> vertices = Lists.newArrayList(graph.getVertices());
        Collections.sort(vertices, (c1, c2) -> Double.compare(c1.getMass(), c2.getMass()));

        Assert.assertEquals(4, vertices.size());

        final SugarComposition v1 = vertices.get(1);
        final SugarComposition v2 = vertices.get(2);
        final SugarComposition v3 = vertices.get(0);
        final SugarComposition v4 = vertices.get(3);

        Assert.assertEquals(new SugarComposition(SugarUnit.Fuc), v3);
        Assert.assertEquals(new SugarComposition(SugarUnit.Hex), v1);
        Assert.assertEquals(new SugarComposition(SugarUnit.HexNAc), v2);
        Assert.assertEquals(new SugarComposition(SugarUnit.Kdn), v4);

        Assert.assertEquals(0.8, graph.findEdge(v1, v2).get().getScore(), 0.000001);
        Assert.assertEquals(0.0, graph.findEdge(v1, v3).get().getScore(), 0.000001);
        Assert.assertEquals(0.0, graph.findEdge(v1, v4).get().getScore(), 0.000001);
        Assert.assertEquals(0.7, graph.findEdge(v2, v3).get().getScore(), 0.000001);
        Assert.assertEquals(0.0, graph.findEdge(v2, v4).get().getScore(), 0.000001);
        Assert.assertEquals(0.6, graph.findEdge(v3, v4).get().getScore(), 0.000001);
    }

    @Test
    public void testCreateGraphRecordFields() throws Exception {

        Schema expected = new Schema.Parser().parse("{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"DenseSimilarityGraph\",\n" +
                "  \"namespace\" : \"org.expasy.mzjava_avro.core.ms.cluster\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"name\" : \"members\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : {\n" +
                "        \"type\" : \"record\",\n" +
                "        \"name\" : \"SugarComposition\",\n" +
                "        \"namespace\" : \"org.expasy.glycoforest.mol\",\n" +
                "        \"fields\" : [ {\n" +
                "          \"name\" : \"composition\",\n" +
                "          \"type\" : {\n" +
                "            \"type\" : \"map\",\n" +
                "            \"values\" : \"int\"\n" +
                "          }\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"scores\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : \"float\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}");

        Assert.assertEquals(expected.toString(true), new MockSimGraphReader().createSchema().toString(true));
    }

    private class MockSimGraphReader extends DenseSimilarityGraphReader<SugarComposition, DenseSimilarityGraph<SugarComposition>> {

        public MockSimGraphReader() {

            super(new SugarCompositionReader());
        }

        @Override
        protected void createRecordFields(List<Schema.Field> fields) {

            createGraphRecordFields(fields);
        }

        @Override
        public DenseSimilarityGraph<SugarComposition> read(Decoder in) throws IOException {

            DenseSimilarityGraph.Builder<SugarComposition> builder = new DenseSimilarityGraph.Builder<>();
            readGraph(in, builder);
            return builder.build();
        }

        @Override
        public Class getObjectClass() {

            return DenseSimilarityGraph.class;
        }
    }
}