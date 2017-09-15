package org.expasy.glycoforest.solver2;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureMultimap<V> {

    private final IsomorphismType isomorphismType;
    private final List<Entry<V>> entries;

    private StructureMultimap(List<Entry<V>> entries, IsomorphismType isomorphismType) {

        this.entries = entries;
        this.isomorphismType = isomorphismType;
    }

    public int size() {

        return entries.size();
    }

    public List<V> get(SugarStructure sugarStructure) {

        for(Entry<V> entry : entries) {

            if(entry.getKey().isIsomorphic(sugarStructure, isomorphismType))
                return new ArrayList<>(entry.values);
        }
        return Collections.emptyList();
    }

    public static <V> StructureMultimap<V> union(StructureMultimap<V> map1, StructureMultimap<V> map2) {

        final StructureMultimap.Builder<V> union = new Builder<>(map1);

        for (int i = 0; i < map2.entries.size(); i++) {

            union.add(map2.entries.get(i).key, map2.entries.get(i).valuesStream());
        }
        return union.build();
    }

    public Stream<Entry<V>> stream() {

        return entries.stream();
    }

    public static <V> StructureMultimap<V> intersect(StructureMultimap<V> map1, StructureMultimap<V> map2) {

        final IsomorphismType isomorphismType = map1.isomorphismType;
        final StructureMultimap.Builder<V> intersect = new StructureMultimap.Builder<>(isomorphismType);

        final List<Entry<V>> entries1 = map1.entries;
        final List<Entry<V>> entries2 = map2.entries;
        final BitSet map2Used = new BitSet(entries2.size());

        for (final Entry<V> entry1 : entries1) {

            for (int j = 0; j < entries2.size(); j++) {

                final Entry<V> entry2 = entries2.get(j);
                if (!map2Used.get(j) && entry1.key.isIsomorphic(entry2.key, isomorphismType)) {

                    intersect.add(entry1.key, Stream.concat(entry1.valuesStream(), entries2.get(j).valuesStream()));
                    map2Used.set(j);
                }
            }
        }

        return intersect.build();
    }

    public static class Entry<V> {

        private final SugarStructure key;
        private final List<V> values;

        private Entry(SugarStructure key, V value) {

            this.key = key;
            this.values = new ArrayList<>();
            values.add(value);
        }

        public Entry(SugarStructure key, Stream<V> stream) {

            this.key = key;
            this.values = stream.collect(Collectors.toList());
        }

        public Entry(Entry<V> entry) {

            this.key = entry.key;
            this.values = new ArrayList<>(entry.values);
        }

        public SugarStructure getKey() {

            return key;
        }

        public Stream<V> valuesStream() {

            return values.stream();
        }

        public int valueCount() {

            return values.size();
        }

        private void add(V value) {

            values.add(value);
        }

        private void addStream(Stream<V> stream) {

            stream.forEach(values::add);
        }

        @Override
        public String toString() {

            return key + ", size = " + values.size();
        }
    }

    public static class Builder<V> {

        private final IsomorphismType isomorphismType;
        private List<Entry<V>> entries = new ArrayList<>();

        public Builder(IsomorphismType isomorphismType) {

            this.isomorphismType = isomorphismType;
        }

        public Builder(StructureMultimap<V> src) {

            isomorphismType = src.isomorphismType;
            entries = new ArrayList<>(src.size());

            copy(src);
        }

        private void copy(StructureMultimap<V> src) {

            for (int i = 0; i < src.entries.size(); i++) {

                entries.add(new Entry<>(src.entries.get(i)));
            }
        }

        public Builder<V> add(SugarStructure key, V value) {

            nonNull(key);
            nonNull(value);

            for (final Entry<V> entry : entries) {

                final SugarStructure current = entry.getKey();
                if (current.isIsomorphic(key, isomorphismType)) {

                    entry.add(value);
                    return this;
                }
            }

            entries.add(new Entry<>(key, value));

            return this;
        }

        public Builder<V> add(SugarStructure key, V... values) {

            return add(key, Stream.of(values));
        }

        public <T> Builder<V> add(Stream<T> stream, Function<T, SugarStructure> keyFunction, Function<T, V> valueFunction) {

            stream.forEach(element -> add(keyFunction.apply(element), valueFunction.apply(element)));

            return this;
        }

        public Builder<V> addAll(Stream<Entry<V>> stream) {

            stream.forEach(element -> add(element.getKey(), element.valuesStream()));

            return this;
        }

        public Builder<V> add(SugarStructure key, Stream<V> valueStream) {

            nonNull(key);
            nonNull(valueStream);

            for (final Entry<V> entry : entries) {

                SugarStructure current = entry.getKey();
                if (current.isIsomorphic(key, isomorphismType)) {

                    entry.addStream(valueStream);
                    return this;
                }
            }

            entries.add(new Entry<>(key, valueStream));
            return this;
        }

        public Builder<V> intersect(StructureMultimap<V> multimap) {

            if(entries.isEmpty()) {

                copy(multimap);
                return this;
            }

            final BitSet entryUsed = new BitSet(entries.size());

            for (final Entry<V> entry1 : multimap.entries) {

                for (int j = 0; j < entries.size(); j++) {

                    final Entry<V> entry2 = entries.get(j);
                    if (!entryUsed.get(j) && entry1.key.isIsomorphic(entry2.key, isomorphismType)) {

                        entry2.addStream(multimap.entries.get(j).valuesStream());
                        entryUsed.set(j);
                    }
                }
            }

            for(int i = entries.size() - 1; i >= 0; i--) {

                if(!entryUsed.get(i))
                    entries.remove(i);
            }

            return this;
        }

        public StructureMultimap<V> consolidateAndBuild(Function<List<V>, Stream<V>> consolidationFunction) {

            final List<Entry<V>> consolidatedEntries = entries.stream()
                    .map(entry -> new Entry<>(entry.key, consolidationFunction.apply(entry.values)))
                    .filter(entry -> entry.valueCount() > 0)
                    .collect(Collectors.toList());

            return new StructureMultimap<>(consolidatedEntries, isomorphismType);
        }

        public StructureMultimap<V> build(){

            List<Entry<V>> tmp = entries;
            entries = null;
            return new StructureMultimap<>(tmp, isomorphismType);
        }
    }
}
