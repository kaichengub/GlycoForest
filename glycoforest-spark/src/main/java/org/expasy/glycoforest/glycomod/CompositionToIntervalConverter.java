package org.expasy.glycoforest.glycomod;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.expasy.glycoforest.avro.io.DoubleRangeWriter;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.mzjava.core.mol.AtomicSymbol;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.Tolerance;

import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CompositionToIntervalConverter {

    public static void main(String[] args) throws IOException {

//        convert("C:\\Users\\ohorlach\\Documents\\tmp\\sugar_composition", "composition_10.hdcomp",
//                Arrays.asList(new CompositionSink(1), new CompositionSink(2)));

//        final List<Predicate<SugarComposition>> predicates = Arrays.asList(
//                composition -> composition.size() > 1,
//                composition -> composition.getCount(SugarUnit.HexNAc) >= 1,
//                composition -> {
//                    final int sCount = composition.getCount(SugarUnit.S);
//                    return sCount <= composition.size() - sCount - 1;
//                },
//                composition -> {
//
//                    Counter terminal = new Counter();
//                    Counter internal = new Counter();
//                    composition.forEachEntry((su, count) -> {
//
//                        (su.isTerminating() ? terminal : internal).increment(count);
//                        return true;
//                    });
//                    return internal.getCount() * 2 >= terminal.getCount();
//                }
//        );
//        convert("C:\\Users\\Oliver\\Documents\\tmp\\sugar_composition", "composition_10.hdcomp",
//                Arrays.asList(new CompositionSink(1, "o-linked_like_"), new CompositionSink(2, "o-linked_like_")), predicates);
    }

    private static void convert(final String path, final String name, List<CompositionSink> sinks, final List<Predicate<SugarComposition>> predicates) throws IOException {

        final FloatWritable key = new FloatWritable();
        final SugarCompositionWritable value = new SugarCompositionWritable();

        final Configuration conf = new Configuration();
        final SequenceFile.Reader reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(new Path(path, name)));

        final NumberFormat format = NumberFormat.getNumberInstance();
        int count = 0;
        int found = 0;
        while (reader.next(key, value)) {

            final SugarComposition composition = value.get();
            count += 1;
            if(predicates.stream().allMatch(p -> p.test(composition))) {

                found += 1;
                for(CompositionSink sink : sinks)
                    sink.add(composition);
            }
            if(count % 1000000 == 0)
                System.out.println("Processed " + format.format(count) + ", " + format.format(found));                      //sout
        }

        reader.close();

        System.out.println("Counted " + count);                      //sout

        for(CompositionSink sink : sinks)
            sink.toSeqFile(conf, path);
    }

    private static class CompositionSink {

        private final RangeSet<Double> rangeSet = TreeRangeSet.create();
        private final Tolerance tolerance = new AbsoluteTolerance(0.3);
        private final double waterMass = Composition.parseComposition("H2O").getMolecularMass();
        private final double reducingEndMassDelta = Composition.parseComposition("H2").getMolecularMass();
        private final double chargeDelta = -(new Composition.Builder().add(AtomicSymbol.H, 1).charge(1).build().getMolecularMass());
        private final String prefix;

        private final int charge;

        public CompositionSink(int charge, String prefix) {

            this.charge = charge;
            this.prefix = prefix;
        }

        public void add(SugarComposition composition) {

            double mz = (composition.getMass() + waterMass + reducingEndMassDelta + chargeDelta * charge) / charge;
            rangeSet.add(Range.closed(tolerance.getMin(mz), tolerance.getMax(mz)));
        }

        public void toSeqFile(Configuration conf, String path) throws IOException {

            final DoubleRangeWritable key = new DoubleRangeWritable();
            final NullWritable value = NullWritable.get();

            SequenceFile.Writer writer = SequenceFile.createWriter(conf,
                    SequenceFile.Writer.file(new Path(path, prefix + "ranges_minus_" + charge + ".hdrange")),
                    SequenceFile.Writer.keyClass(key.getClass()),
                    SequenceFile.Writer.valueClass(value.getClass()),
                    SequenceFile.Writer.compression(SequenceFile.CompressionType.NONE));

            Set<Range<Double>> ranges = rangeSet.asRanges();
            System.out.println("Minus " + charge + " range set size " + ranges.size());                      //sout

            for(Range<Double> range : ranges) {

                key.set(range);
                writer.append(key, value);
            }

            writer.close();
        }

        public String toString() {

            try {
                DoubleRangeWriter writer = new DoubleRangeWriter();

                StringWriter out = new StringWriter();
                JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
                gen.setPrettyPrinter(new DefaultPrettyPrinter());
                Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

                for(Range<Double> range : rangeSet.asRanges()) {

                    writer.write(range, encoder);

                }
                encoder.flush();

                return out.toString();
            } catch (IOException e) {

                throw new IllegalStateException(e);
            }
        }
    }
}
