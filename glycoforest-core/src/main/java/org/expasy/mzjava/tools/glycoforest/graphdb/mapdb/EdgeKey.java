package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeKey implements Comparable<EdgeKey>, Serializable {

    private float mz;
    private int count;

    public EdgeKey() {

    }

    public EdgeKey(float mz, int count) {

        setValues(mz, count);
    }

    public void setValues(float mz, int count) {

        this.mz = mz;
        this.count = count;
    }

    public float getMz() {

        return mz;
    }

    public int getCount() {

        return count;
    }

    @Override
    public int compareTo(EdgeKey o) {

        int c = Float.compare(mz, o.mz);

        if(c == 0)
            c = Integer.compare(count, o.count);

        return c;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeKey edgeKey = (EdgeKey) o;
        return Objects.equals(mz, edgeKey.mz) &&
                Objects.equals(count, edgeKey.count);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mz, count);
    }

    @Override
    public String toString() {

        return "EdgeKey{" +
                "mz=" + mz +
                ", count=" + count +
                '}';
    }
}
