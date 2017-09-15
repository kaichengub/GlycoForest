package org.expasy.glycoforest.app;

import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLWriter;
import org.apache.commons.collections15.Transformer;
import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.core.ms.peaklist.Peak;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SpectralNetworkGraphMLWriter extends GraphMLWriter<SpectralNetworkEvaluator.Vertex, SpectralNetworkEvaluator.Edge> {

    private final Map<String, String> attrTypeMap;

    private final GlycanMassCalculator massCalculator = GlycanMassCalculator.newEsiNegativeReduced();

    public SpectralNetworkGraphMLWriter() {

        attrTypeMap = new HashMap<>();
        attrTypeMap.put("Label", "string");
        attrTypeMap.put("annotation_score", "float");
        attrTypeMap.put("gg_mass", "int");
        attrTypeMap.put("mz", "double");
        attrTypeMap.put("msn_count", "int");
        attrTypeMap.put("x", "float");
        attrTypeMap.put("y", "float");
        attrTypeMap.put("Weight", "float");
        attrTypeMap.put("annotated", "string");
        attrTypeMap.put("z", "int");

        setupNodes();
        setupEdges();
    }

    private void setupNodes() {

        Map<String,GraphMLMetadata<SpectralNetworkEvaluator.Vertex>> nodeDataMap = new HashMap<>();
        nodeDataMap.put("Label", new GraphMLMetadata<>("Label", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                final String charge = " (" + node.getSpectrumEntry().getCharge() + ")";
                if (node.getSpectrumEntry().isAnnotated()) {

                    return node.getSpectrumEntry().structureStream().map(SugarStructure::getLabel).collect(Collectors.joining(", ")) + charge;
                }
                else {

                    return calcMassLabel(node) + charge;
                }
            }
        }));
        nodeDataMap.put("gg_mass", new GraphMLMetadata<>("gg_mass", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return calcMassLabel(node);
            }
        }));
        nodeDataMap.put("mz", new GraphMLMetadata<>("mz", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return Double.toString(node.getSpectrumEntry().getMz());
            }
        }));
        nodeDataMap.put("msn_count", new GraphMLMetadata<>("msn_count", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return Double.toString(node.getSpectrumEntry().getRawSpectrum().getMemberCount());
            }
        }));        
        nodeDataMap.put("x", new GraphMLMetadata<>("x", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return Double.toString(0.0);
            }
        }));
        nodeDataMap.put("y", new GraphMLMetadata<>("y", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return Double.toString(0.0);
            }
        }));
        nodeDataMap.put("annotation_score", new GraphMLMetadata<>("annotation_score", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return Double.toString(node.getResultList().getMetaScore());
            }
        }));
        nodeDataMap.put("annotated", new GraphMLMetadata<>("annotated", null, node -> {

            switch (node.getType()){

                case REFERENCE:
                    return "reference";
                case ANNOTATED:
                    return "annotated";
                case NEW_UN_ANNOTATED:
                    return "new_no_result";
                case NEW_ANNOTATED:
                    return "new_result";
                default:
                    throw new IllegalStateException("Unknown enum " + node.getType());
            }
        }));
        nodeDataMap.put("z", new GraphMLMetadata<>("z", null, new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return Integer.toString(node.getSpectrumEntry().getCharge());
            }
        }));

        setVertexIDs(new Transformer<SpectralNetworkEvaluator.Vertex, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Vertex node) {

                return node.getSpectrumEntry().getRawSpectrum().getId().toString();
            }
        });

        super.setVertexData(nodeDataMap);
    }

    private String calcMassLabel(SpectralNetworkEvaluator.Vertex node) {

        final Peak precursor = node.getSpectrumEntry().getPrecursor();
        final double mzZ1;
        if (precursor.getCharge() == 1) {

            mzZ1 = precursor.getMz();
        } else {

            final double compositionMass = massCalculator.calcCompositionMass(precursor.getMz(), precursor.getCharge());
            mzZ1 = massCalculator.calcMz(compositionMass, 1);
        }
        return Integer.toString((int)mzZ1);
    }

    private void setupEdges(){

        Map<String, GraphMLMetadata<SpectralNetworkEvaluator.Edge>> edgeDataMap = new HashMap<>();
        edgeDataMap.put("Weight", new GraphMLMetadata<>("Weight", null, new Transformer<SpectralNetworkEvaluator.Edge, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Edge edge) {

                return Double.toString(edge.getScore());
            }
        }));
        edgeDataMap.put("Label", new GraphMLMetadata<>("Label", null, new Transformer<SpectralNetworkEvaluator.Edge, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Edge edge) {

                return edge.getTransformation().toString();
            }
        }));

        setEdgeData(edgeDataMap);
        setEdgeIDs(new Transformer<SpectralNetworkEvaluator.Edge, String>() {
            @Override
            public String transform(SpectralNetworkEvaluator.Edge edge) {

                return UUID.randomUUID().toString();
            }
        });

    }

    @Override
    public void setVertexData(Map<String, GraphMLMetadata<SpectralNetworkEvaluator.Vertex>> vertex_map) {

        throw new UnsupportedOperationException();
    }

    protected void writeKeySpecification(String key, String type,
                                         GraphMLMetadata<?> ds, BufferedWriter bw) throws IOException {

        bw.write("<key id=\"" + key + "\" for=\"" + type + "\"");
        if (attrTypeMap.containsKey(key)) {

            bw.write(" attr.type=\"" + attrTypeMap.get(key) + "\"");
        }
        boolean closed = false;
        // write out description if any
        String desc = ds.description;
        if (desc != null) {
            bw.write(">\n");
            closed = true;
            bw.write("<desc>" + desc + "</desc>\n");
        }
        // write out default if any
        Object def = ds.default_value;
        if (def != null) {
            if (!closed) {
                bw.write(">\n");
                closed = true;
            }
            bw.write("<default>" + def.toString() + "</default>\n");
        }
        if (!closed)
            bw.write("/>\n");
        else
            bw.write("</key>\n");
    }
}
