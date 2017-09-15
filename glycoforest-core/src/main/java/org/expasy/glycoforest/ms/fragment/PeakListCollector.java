package org.expasy.glycoforest.ms.fragment;

import org.expasy.glycoforest.ms.spectrum.StructurePeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class PeakListCollector<PL extends PeakList<StructurePeakAnnotation>> implements Collector<SugarStructurePeak, PL, PL> {

    private final Supplier<PL> supplier;
    private final double mzDelta;

    public PeakListCollector(Supplier<PL> supplier, final double mzDelta) {

        this.supplier = supplier;
        this.mzDelta = mzDelta;
    }

    @Override
    public Supplier<PL> supplier() {

        return supplier;
    }

    @Override
    public BiConsumer<PL, SugarStructurePeak> accumulator() {

        return (peakList, peak) -> {

            final int lastIndex = peakList.size() - 1;
            if(lastIndex > -1 && peak.getMz() < peakList.getMz(lastIndex)){

                throw new IllegalStateException("Peaks are not sorted. Last mz " + peakList.getMz(lastIndex) + " new mz " + peak.getMz());
            } else if (lastIndex > -1 && (peak.getMz() - peakList.getMz(lastIndex) < mzDelta)) {

                peakList.add(peakList.getMz(lastIndex), peak.getIntensity(), peak.newAnnotation());
            } else {

                peakList.add(peak.getMz(), peak.getIntensity(), peak.newAnnotation());
            }
        };
    }

    @Override
    public BinaryOperator<PL> combiner() {

        return (pl1, pl2) -> {

            pl1.addPeaks(pl2);
            return pl1;
        };
    }

    @Override
    public Function<PL, PL> finisher() {

        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {

        return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }
}
