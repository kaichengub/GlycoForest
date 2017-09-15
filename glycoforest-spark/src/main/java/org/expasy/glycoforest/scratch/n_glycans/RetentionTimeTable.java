package org.expasy.glycoforest.scratch.n_glycans;

import com.google.common.collect.Range;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RetentionTimeTable implements Serializable {

    protected final Map<String, List<Entry>> data;

    public RetentionTimeTable(final Map<String, List<Entry>> data) {

        this.data = data;
    }

    public List<SugarStructure> getStructure(final String run, Range<Double> retentionTimeInterval, Predicate<SugarStructure> structurePredicate) {

        final List<Entry> runData = data.get(run);

        if (runData == null)
            return Collections.emptyList();

        return runData.stream()
                .filter(e -> retentionTimeInterval.contains(e.rt))
                .map(e -> e.structure)
                .filter(structurePredicate::test)
                .collect(Collectors.toList());
    }

    public Stream<String> runStream() {

        return data.keySet().stream();
    }

    protected static class Entry implements Serializable {

        private final double rt;
        private final SugarStructure structure;

        public Entry(final double rt, final SugarStructure structure, final TimeUnit timeUnit) {

            this.rt = timeUnit.convert(rt, TimeUnit.SECOND);
            this.structure = structure;
        }

        public double getRt() {

            return rt;
        }

        public SugarStructure getStructure() {

            return structure;
        }
    }
}
