package org.expasy.glycoforest.app.evaluator;

import org.expasy.glycoforest.solver.OpenEdge;
import org.expasy.glycoforest.solver.StructureVertex;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SolveTask {

    private final StructureVertex vertex;
    private final List<OpenEdge> incomingEdges;

    public SolveTask(StructureVertex vertex, List<OpenEdge> incomingEdges) {

        this.vertex = vertex;
        this.incomingEdges = incomingEdges;
    }

    public StructureVertex getVertex() {

        return vertex;
    }

    public Stream<OpenEdge> getIncomingEdgeStream() {

        return incomingEdges.stream();
    }
}
