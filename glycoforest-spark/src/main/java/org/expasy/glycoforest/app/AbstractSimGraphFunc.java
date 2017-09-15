package org.expasy.glycoforest.app;

import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraph;
import org.expasy.mzjava.core.ms.cluster.SimilarityGraphBuilder;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.spark.Cached;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class AbstractSimGraphFunc<A extends PeakAnnotation> implements Serializable {

    protected final Cached<SimFunc<A, A>> cachedSimFunc;
    protected final Cached<Tolerance> cachedPrecursorTolerance;
    protected final Cached<PeakProcessorChain<A>> cachedProcessorChain;

    public AbstractSimGraphFunc(Cached<PeakProcessorChain<A>> processorChain, Cached<Tolerance> precursorTolerance, Cached<SimFunc<A, A>> simFunc) {

        this.cachedProcessorChain = processorChain;
        this.cachedPrecursorTolerance = precursorTolerance;
        this.cachedSimFunc = simFunc;
    }

    protected <S extends PeakList<A>, G extends SimilarityGraph<S>> void buildGraphIgnoreCharge(final List<S> rawSpectra, final SimilarityGraphBuilder<S, G> builder) {

        rawSpectra.sort((s1, s2) -> Double.compare(s1.getPrecursor().getMz(), s2.getPrecursor().getMz()));

        final PeakProcessorChain<A> processorChain = cachedProcessorChain.get();
        List<S> processedSpectra;
        if (processorChain.isEmpty()) {
            processedSpectra = rawSpectra;
        } else {
            processedSpectra = rawSpectra.stream().map(spectrum -> {

                //A copy of S is always S
                //noinspection unchecked
                return (S)spectrum.copy(processorChain);
            }).collect(Collectors.toList());
        }

        final Tolerance precursorTolerance = cachedPrecursorTolerance.get();
        final SimFunc<A, A> simFunc = cachedSimFunc.get();

        checkArgument(rawSpectra.size() == processedSpectra.size());

        for (int i = 0; i < processedSpectra.size(); i++) {

            S spectrum1 = processedSpectra.get(i);
            builder.add(rawSpectra.get(i));
            double mz1 = spectrum1.getPrecursor().getMz();

            for (int j = i + 1; j < processedSpectra.size(); j++) {

                S spectrum2 = processedSpectra.get(j);
                double mz2 = spectrum2.getPrecursor().getMz();

                if (precursorTolerance.check(mz1, mz2) == Tolerance.Location.LARGER)
                    break;

                checkState(precursorTolerance.withinTolerance(mz1, mz2));

                double score = simFunc.calcSimilarity(spectrum1, spectrum2);
                if (!Double.isNaN(score))
                    builder.add(rawSpectra.get(i), rawSpectra.get(j), score);
            }
        }
    }
}
