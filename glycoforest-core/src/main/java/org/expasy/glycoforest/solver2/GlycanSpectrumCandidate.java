package org.expasy.glycoforest.solver2;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.solver.OpenEdge;
import org.expasy.glycoforest.solver.SpectrumAnnotation;
import org.expasy.glycoforest.solver.StructureTransformation;

import java.util.Optional;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycanSpectrumCandidate {

    private final OpenEdge openEdge;
    private final SugarStructure sourceStructure;
    private final SugarStructure candidate;
    private final double coverageScore;
    private final double openSimScore;
    private final double ndp;

    public GlycanSpectrumCandidate(OpenEdge openEdge, final SugarStructure sourceStructure, SugarStructure candidate, double coverageScore, double openSimScore, double ndp) {

        this.openEdge = openEdge;
        this.sourceStructure = sourceStructure;
        this.candidate = candidate;
        this.coverageScore = coverageScore;
        this.openSimScore = openSimScore;
        this.ndp = ndp;
    }

    public double getCoverageScore() {

        return coverageScore;
    }

    public double getNdp() {

        return ndp;
    }

    @Override
    public String toString() {

        final Optional<SpectrumAnnotation> first = openEdge.getVertex2().annotationStream().findFirst();
        if (first.isPresent()) {

            final boolean isomorphic = first.get().getStructure().isIsomorphic(candidate, IsomorphismType.ROOTED_TOPOLOGY);
            return isomorphic + ", GC=" + getCandidateScore() + ", cov=" + coverageScore + ", ondp=" + openSimScore + ", ndp=" + ndp + ", " + candidate;
        } else {

            return "~ , " + getCandidateScore() + ", " + coverageScore + ", " + openSimScore + ", " + ndp + ", " + candidate;
        }
    }

    public SugarStructure getSourceStructure() {

        return sourceStructure;
    }

    public SugarStructure getCandidate() {

        return candidate;
    }

    public double getCandidateScore() {

        return openSimScore  * 0.5 + ndp * 0.5;
    }

    public StructureTransformation getTransformation() {

        return openEdge.getTransformation();
    }

    public OpenEdge getOpenEdge() {

        return openEdge;
    }

    public double getOpenSimScore() {

        return openSimScore;
    }
}
