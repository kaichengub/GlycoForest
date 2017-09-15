package org.expasy.glycoforest.app.evaluator;

import com.google.common.base.Preconditions;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.solver2.GlycanSpectrumCandidate;
import org.expasy.glycoforest.solver2.StructureMultimap;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycanSpectrumMatch {

    private final GlycanSpectrumCandidate bestCandidate;
    private final StructureMultimap.Entry<GlycanSpectrumCandidate> hit;
    private final double coverage;
    private final double simScores;
    private final double resultScore;
    private final int transitionCount;

    public GlycanSpectrumMatch(final StructureMultimap.Entry<GlycanSpectrumCandidate> hit) {

        bestCandidate = hit.valuesStream()
                .max((e1, e2) -> Double.compare(e1.getCandidateScore(), e2.getCandidateScore()))
                .orElseThrow(IllegalStateException::new);

        this.hit = hit;
        this.coverage = bestCandidate.getCoverageScore();
        this.simScores = bestCandidate.getNdp();
        this.transitionCount = hit.valueCount();

        final List<GlycanSpectrumCandidate> candidates = hit.valuesStream()
                .sorted(Comparator.comparing(GlycanSpectrumCandidate::getOpenSimScore).reversed())
                .collect(Collectors.toList());
        this.resultScore = 7/9.0 * bestCandidate.getCandidateScore() + 1/9.0 * getCandidateScore(candidates, 1) + 1/9.0 * getCandidateScore(candidates, 2);

        final double[] ndpArray = hit.valuesStream().mapToDouble(GlycanSpectrumCandidate::getNdp).toArray();
        final double first = ndpArray[0];
        for(int i = 1; i < ndpArray.length; i++) {

            Preconditions.checkState(Math.abs(first - ndpArray[i]) < 0.001, "Ndp of all glycan candidates should be the same");
        }
    }

    private double getCandidateScore(final List<GlycanSpectrumCandidate> candidates, final int index) {

        if (index > candidates.size() - 1) {
            return 0;
        } else {
            return candidates.get(index).getCandidateScore();
        }
    }

    public double getResultScore() {

        return resultScore;
    }

    public double getSimScores() {

        return simScores;
    }

    public double getCoverage() {

        return coverage;
    }

    public int getTransitionCount() {

        return transitionCount;
    }

    public GlycanSpectrumCandidate getBestCandidate() {

        return bestCandidate;
    }

    public StructureMultimap.Entry<GlycanSpectrumCandidate> getHit() {

        return hit;
    }

    public SugarStructure getStructure() {

        return hit.getKey();
    }
}
