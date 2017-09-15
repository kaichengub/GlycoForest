package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarStructure;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class TransformationCandidate {

    private final double score;
    private final StructureTransformation structureTransformation;
    private final SugarStructure sourceGraph;

    public TransformationCandidate(SugarStructure sourceStructure, StructureTransformation structureTransformation, double score) {

        checkNotNull(sourceStructure);
        checkNotNull(structureTransformation);

        this.score = score;
        this.structureTransformation = structureTransformation;
        this.sourceGraph = sourceStructure;
    }

    public double getScore() {

        return score;
    }

    public StructureTransformation getStructureTransformation() {

        return structureTransformation;
    }

    public SugarStructure getSourceStructure() {

        return sourceGraph;
    }

    public List<SugarStructure> generateCandidates() {

        return structureTransformation.generateCandidates(sourceGraph, false);
    }

    @Override
    public String toString() {

        return "TransformationCandidate{" +
                "score=" + score +
                ", graphTransformation=" + structureTransformation +
                ", sourceGraph=" + sourceGraph +
                '}';
    }
}
