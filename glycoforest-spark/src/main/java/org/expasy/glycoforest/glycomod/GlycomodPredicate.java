package org.expasy.glycoforest.glycomod;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.expasy.mzjava.core.ms.peaklist.PeakList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycomodPredicate implements Predicate<PeakList>, Externalizable {

    private RangeSet<Double> rangeSet;

    public GlycomodPredicate() {

    }

    public GlycomodPredicate(final Configuration conf, final Path path) {

        try {

            final SequenceFile.Reader reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));

            final DoubleRangeWritable key = new DoubleRangeWritable();
            final NullWritable value = NullWritable.get();

            ImmutableRangeSet.Builder<Double> builder = ImmutableRangeSet.builder();
            while (reader.next(key, value)) {

                builder.add(key.get());
            }

            rangeSet = builder.build();
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean test(PeakList msnSpectrum) {

        return rangeSet.contains(msnSpectrum.getPrecursor().getMz());
    }

    public boolean test(double mz) {

        return rangeSet.contains(mz);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        Set<Range<Double>> ranges = rangeSet.asRanges();
        out.writeInt(ranges.size());
        final DoubleRangeWritable key = new DoubleRangeWritable();
        for(Range<Double> range : ranges) {

            key.set(range);
            key.write(out);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        final ImmutableRangeSet.Builder<Double> builder = ImmutableRangeSet.builder();
        final DoubleRangeWritable key = new DoubleRangeWritable();
        int size = in.readInt();
        for(int i = 0; i < size; i++) {

            key.readFields(in);
            builder.add(key.get());
        }
    }
}
