package org.expasy.glycoforest.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.expasy.mzjava.hadoop.io.AbstractAvroWritable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SequenceFileStreams {

    public static <V> Stream<V> sequenceFileValueStream(Configuration conf, Path path, Writable key, AbstractAvroWritable<V> value){

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new SequenceFileValueIterator<>(conf, path, key, value), Spliterator.NONNULL), false);
    }

    public static <K extends Writable, V extends Writable, T> Stream<T> sequenceFileValueStream(Configuration conf, Path path, K key, V value, BiFunction<K, V, T> function){

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new SequenceFileIterator<>(conf, path, key, value, function), Spliterator.NONNULL), false);
    }

    public static <K extends Writable, V extends Writable, T> Iterator<T> sequenceFileValueIterator(Configuration conf, Path path, K key, V value, BiFunction<K, V, T> function){

        return new SequenceFileIterator<>(conf, path, key, value, function);
    }

    private static class SequenceFileValueIterator<V> implements Iterator<V> {

        private final SequenceFile.Reader reader;

        private final Writable key;
        private final AbstractAvroWritable<V> value;
        private V next = null;

        private boolean closed = false;

        public SequenceFileValueIterator(Configuration conf, Path path, Writable key, AbstractAvroWritable<V> value) {

            checkNotNull(conf);
            checkNotNull(path);
            checkNotNull(key);
            checkNotNull(value);

            this.key = key;
            this.value = value;
            try {

                reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public boolean hasNext() {

            if (next != null) {
                return true;
            } else {
                try {
                    next = readNext();
                    return (next != null);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        private V readNext() throws IOException {

            if(closed || !reader.next(key, value)) {

                if (!closed) {

                    reader.close();
                    closed = true;
                }
                return null;
            } else {

                return value.get();
            }
        }

        @Override
        public V next() {

            if (next != null || hasNext()) {

                V line = next;
                next = null;
                return line;
            } else {
                throw new NoSuchElementException();
            }
        }
    }

    private static class SequenceFileIterator<K extends Writable, V extends Writable, T> implements Iterator<T> {

        private final SequenceFile.Reader reader;

        private final K key;
        private final V value;
        private final BiFunction<K, V, T> function;
        private T next = null;

        private boolean closed = false;

        public SequenceFileIterator(Configuration conf, Path path, K key, V value, BiFunction<K, V, T> function) {

            checkNotNull(conf);
            checkNotNull(path);
            checkNotNull(key);
            checkNotNull(value);
            checkNotNull(function);

            this.key = key;
            this.value = value;
            this.function = function;
            try {

                reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));
            } catch (IOException e) {

                throw new IllegalStateException(e);
            }
        }

        @Override
        public boolean hasNext() {

            if (next != null) {
                return true;
            } else {
                try {
                    next = readNext();
                    return (next != null);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }

        private T readNext() throws IOException {

            if(closed || !reader.next(key, value)) {

                if (!closed) {

                    reader.close();
                    closed = true;
                }
                return null;
            } else {

                return function.apply(key, value);
            }
        }

        @Override
        public T next() {

            if (next != null || hasNext()) {

                T line = next;
                next = null;
                return line;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
