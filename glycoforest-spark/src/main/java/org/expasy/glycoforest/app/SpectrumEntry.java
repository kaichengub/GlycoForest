package org.expasy.glycoforest.app;

import org.expasy.glycoforest.mol.GlycanMassCalculator;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.solver.SpectrumAnnotation;
import org.expasy.glycoforest.solver.WithinRunStructureVertex;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;

import java.nio.channels.IllegalSelectorException;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SpectrumEntry {

    private final WithinRunConsensus rawSpectrum;
    private final WithinRunConsensus processedSpectrum;
    private final Map<SugarStructure, Long> structures;
    private final double fragmentCoverage;

    public SpectrumEntry(final WithinRunConsensus rawSpectrum, final WithinRunConsensus processedSpectrum, final Map<SugarStructure, Long> structures, double fragmentCoverage) {

        this.rawSpectrum = rawSpectrum;
        this.processedSpectrum = processedSpectrum;
        this.structures = structures;
        this.fragmentCoverage = fragmentCoverage;
    }

    public UUID getRunId() {

        return rawSpectrum.getRunId();
    }

    public int getStructureCount() {

        return structures.size();
    }

    public double getFragmentCoverage() {

        return fragmentCoverage;
    }

    public WithinRunConsensus getProcessedSpectrum() {

        return processedSpectrum;
    }

    public Optional<SugarStructure> getBestStructure() {

        if (structures.isEmpty())
            return Optional.empty();

        return Optional.of(structures.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .orElseThrow(IllegalSelectorException::new)
                .getKey()
        );
    }

    public Stream<SugarStructure> structureStream(){

        return structures.keySet().stream();
    }

    public WithinRunConsensus getRawSpectrum() {

        return rawSpectrum;
    }

    public double getMz() {

        return rawSpectrum.getPrecursor().getMz();
    }

    public int getCharge() {

        return rawSpectrum.getPrecursor().getCharge();
    }

    public Peak getPrecursor() {

        return rawSpectrum.getPrecursor();
    }

    public WithinRunStructureVertex makeWithinRunStructureVertex() {

        final WithinRunStructureVertex structureVertex = new WithinRunStructureVertex(rawSpectrum, processedSpectrum);
        final double sum = structures.values().stream().mapToLong(Long::longValue).sum();
        for(Map.Entry<SugarStructure, Long> entry : structures.entrySet()) {

            structureVertex.addAnnotation(new SpectrumAnnotation(entry.getValue()/sum, SpectrumAnnotation.Type.MANUAL, entry.getKey()));
        }
        return structureVertex;
    }

    public boolean isAnnotated() {

        return !structures.isEmpty();
    }

    public String calcMassLabel(final GlycanMassCalculator massCalculator) {

        final Peak precursor = this.getPrecursor();
        final double mzZ1;
        if (precursor.getCharge() == 1) {

            mzZ1 = precursor.getMz();
        } else {

            final double compositionMass = massCalculator.calcCompositionMass(precursor.getMz(), precursor.getCharge());
            mzZ1 = massCalculator.calcMz(compositionMass, 1);
        }
        return Integer.toString((int) mzZ1);
    }
}
