package org.expasy.glycoforest.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class NavigableList<K extends Comparable<K>, V> {

    private final List<K> keyList;
    private final List<V> valueList;

    private static final int SMALLER = -1;
    private static final int LARGER = 1;

    private NavigableList(List<Entry<K, V>> entries) {

        keyList = new ArrayList<>(entries.size());
        valueList = new ArrayList<>(entries.size());

        Collections.sort(entries);
        for (Entry<K, V> entry : entries) {

            keyList.add(entry.key);
            valueList.add(entry.value);
        }
    }

    public Stream<V> values(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {

        if (fromKey.compareTo(toKey) != -1)
            throw new IllegalArgumentException("From key has to be smaller than to key. Was " + fromKey + " and " + toKey);

        final int fromIndex = getFromIndex(fromKey, fromInclusive);
        final int toIndex = getToIndex(toKey, toInclusive);

        return valueList.subList(fromIndex, toIndex).stream();
    }

    private int getFromIndex(K fromKey, boolean fromInclusive) {

        int fromIndex = Collections.binarySearch(keyList, fromKey);
        if (fromIndex < 0) {

            fromIndex = -(fromIndex + 1);
        } else {

            if (fromInclusive) {

                fromIndex = firstIndex(fromIndex, fromKey);
            } else {

                fromIndex = lastIndex(fromIndex, fromKey) + 1;
            }
        }

        return fromIndex;
    }

    private int getToIndex(K toKey, boolean toInclusive) {

        int toIndex = Collections.binarySearch(keyList, toKey);
        if (toIndex < 0) {

            toIndex = -(toIndex + 1);
        } else {

            if (toInclusive) {

                toIndex = lastIndex(toIndex, toKey) + 1;
            } else {

                toIndex = firstIndex(toIndex, toKey);
            }
        }

        return toIndex;
    }

    private int firstIndex(int start, K key) {

        int index = start;
        while (index > 0 && key.compareTo(keyList.get(index - 1)) != LARGER)
            index -= 1;

        return index;
    }

    private int lastIndex(int start, K key) {

        int index = start;
        final int size = keyList.size();
        while (index + 1 < size && key.compareTo(keyList.get(index + 1)) != SMALLER)
            index += 1;

        return index;
    }

    public Stream<V> values() {

        return valueList.stream();
    }

    public int size() {

        return keyList.size();
    }

    public static class Builder<K extends Comparable<K>, V> {

        private final List<Entry<K, V>> entries;

        public Builder() {

            this(1000);
        }

        public Builder(int initialCapacity) {

            entries = new ArrayList<>(initialCapacity);
        }

        public Builder<K, V> add(K key, V value) {

            checkNotNull(key);

            entries.add(new Entry<>(key, value));

            return this;
        }

        public NavigableList<K, V> build() {

            return new NavigableList<>(entries);
        }
    }

    private static class Entry<K extends Comparable<K>, V> implements Comparable<Entry<K, V>> {

        final K key;
        final V value;

        public Entry(K key, V value) {

            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Entry<K, V> o) {

            return key.compareTo(o.key);
        }
    }
}
