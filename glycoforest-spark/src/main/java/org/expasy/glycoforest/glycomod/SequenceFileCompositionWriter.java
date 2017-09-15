package org.expasy.glycoforest.glycomod;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.SequenceFile;
import org.expasy.mzjava.utils.Counter;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SequenceFileCompositionWriter {

    private static void write(final String path, final String name, int numMonomers) throws IOException {

        final String tmpName = name + ".tmp";

        final FloatWritable key = new FloatWritable();
        final SugarCompositionWritable value = new SugarCompositionWritable();

        final LocalFileSystem fs = FileSystem.getLocal(new Configuration());
        final Path tmpPath = new Path(path, tmpName);

        SequenceFile.Writer writer = SequenceFile.createWriter(fs.getConf(),
                        SequenceFile.Writer.file(tmpPath),
                        SequenceFile.Writer.keyClass(key.getClass()),
                        SequenceFile.Writer.valueClass(value.getClass()),
                        SequenceFile.Writer.compression(SequenceFile.CompressionType.NONE));

        CompositionSource compositionSource = new NTupleCompositionSource(numMonomers, Collections.emptyList());

        final Counter counter = new Counter();
        compositionSource.createCompositions(composition -> {

            key.set((float) composition.getMass());
            value.set(composition);
            try {
                writer.append(key, value);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            counter.increment();

            if(counter.getCount() % 100000 == 0) {

                final Counter size = new Counter();
                composition.forEachEntry((sugar, n) -> {
                    size.increment(n);
                    return true;
                });

                System.out.println(counter.getCount() + " size " + size.getCount());                      //sout
            }
        });

        writer.close();

        System.out.println("Sorting");                      //sout
        SequenceFile.Sorter sorter = new SequenceFile.Sorter(fs, new FloatWritable.Comparator(), key.getClass(), value.getClass(), fs.getConf());
        sorter.sort(new Path[]{tmpPath}, new Path(path, name), true);
    }
}
