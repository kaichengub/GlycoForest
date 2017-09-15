package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.Spectrum;
import org.expasy.mzjava.tools.glycoforest.graphdb.WithinRunNode;
import org.expasy.mzjava.tools.glycoforest.graphdb.hstore.HWithinRunNode;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunStructureVertex implements StructureVertex {

    private final WithinRunConsensus consensus;
    private final PeakList<LibPeakAnnotation> processedPeakList;
    private final List<SpectrumAnnotation> annotations = new ArrayList<>();

    public WithinRunStructureVertex(WithinRunConsensus consensus, PeakList<LibPeakAnnotation> processedPeakList) {

        this.consensus = consensus;
        this.processedPeakList = processedPeakList;
    }

    public double getSimScoreMean() {

        return consensus.getSimScoreMean();
    }

    public double getTotalIonCurrent() {

        return consensus.getTotalIonCurrent();
    }

    public double getSimScoreStdev() {

        return consensus.getSimScoreStdev();
    }

    public double getConsensusSize() {

        return consensus.size();
    }

    public int getMsnMemberCount() {

        return consensus.getMemberCount();
    }

    public UUID getConsensusId() {

        return consensus.getId();
    }

    public Peak getPrecursor() {

        return consensus.getPrecursor();
    }

    public WithinRunNode getSpectrumNode() {

        return new HWithinRunNode(consensus);
    }

    public PeakList<LibPeakAnnotation> getProcessedPeakList() {

        return processedPeakList;
    }

    public int getWithinRunMemberCount() {

        return 1;
    }

    public double getMz() {

        return consensus.getPrecursor().getMz();
    }

    @Override
    public String toString() {

        final Peak precursor = consensus.getPrecursor();
        return NumberFormat.getNumberInstance().format(precursor.getMz()) + " -" + precursor.getCharge() + " " + Arrays.toString(annotations.stream().map(annotation -> annotation.getStructure().getLabel()).toArray());
    }

    public void addAnnotation(SpectrumAnnotation annotation) {

        annotations.add(annotation);
    }

    public Stream<SpectrumAnnotation> annotationStream(){

        return annotations.stream();
    }

    public boolean isAnnotated() {

        return !annotations.isEmpty();
    }

    public int getCharge() {

        return consensus.getPrecursor().getCharge();
    }

    public AnnotationState getAnnotationState() {

        if(annotations.isEmpty())
            return AnnotationState.ABSENT;

        final SugarStructure first = annotations.get(0).getStructure();
        for(int i = 1, size = annotations.size(); i < size; i++) {

            final SugarStructure next = annotations.get(i).getStructure();
            if(!next.isIsomorphic(first, IsomorphismType.ROOTED_TOPOLOGY))
                return AnnotationState.AMBIGUOUS;
        }

        return AnnotationState.UNIQUE;
    }

    public double bestAnnotationScore() {

        return annotations.stream().mapToDouble(SpectrumAnnotation::getScore).max().orElse(0.0);
    }

    public String getLabel() {

        return annotations.get(0).getStructure().getLabel();
    }

    public Stream<String> structureLabelStream() {

        return annotations.stream().map(SpectrumAnnotation::getStructure).map(SugarStructure::getLabel);
    }

    public String calcMassLabel(final GlycanMassCalculator massCalculator) {

        return consensus.calcMassLabel(massCalculator);
    }

    public Spectrum<LibPeakAnnotation> getConsensus() {

        return consensus;
    }
}
