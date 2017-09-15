package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.glycoforest.mol.VertexPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureRemoval extends StructureTransformation {

    private final VertexPredicate vertexPredicate;

    public StructureRemoval(SugarUnit target) {

        super("-" + target.toString(), -target.getUnitMass());
        this.vertexPredicate = (v, g) -> v.isUnit(target) && !v.equals(g.getRoot());
    }

    public StructureRemoval(SugarUnit target, VertexPredicate vertexPredicate) {

        super("-" + target.toString(), -target.getUnitMass());
        this.vertexPredicate = vertexPredicate.and((v, g) -> v.isUnit(target) && !v.equals(g.getRoot()));
    }

    @Override
    public List<SugarStructure> generateCandidates(SugarStructure src, boolean allowDuplicates) {

        List<SugarStructure> allCandidates = new ArrayList<>();
        final String newLabel = src.getLabel() + " " + name;

        doAdd(src.remove(newLabel, vertexPredicate), allCandidates, allowDuplicates);

        return allCandidates;
    }
}
