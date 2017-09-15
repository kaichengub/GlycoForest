package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.Spectrum;
import org.expasy.mzjava.tools.glycoforest.graphdb.SpectrumNode;

import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public interface StructureVertex {

    enum AnnotationState {ABSENT, UNIQUE, AMBIGUOUS}

    double getSimScoreMean();

    double getTotalIonCurrent();

    double getSimScoreStdev();

    double getConsensusSize();

    int getMsnMemberCount();

    UUID getConsensusId();

    Peak getPrecursor();

    SpectrumNode getSpectrumNode();

    PeakList<LibPeakAnnotation> getProcessedPeakList();

    int getWithinRunMemberCount();

    double getMz();

    void addAnnotation(SpectrumAnnotation annotation);

    Stream<SpectrumAnnotation> annotationStream();

    boolean isAnnotated();

    int getCharge();

    AnnotationState getAnnotationState();

    double bestAnnotationScore();

    String getLabel();

    Stream<String> structureLabelStream();

    String calcMassLabel(final GlycanMassCalculator massCalculator);

    Spectrum<LibPeakAnnotation> getConsensus();
}
