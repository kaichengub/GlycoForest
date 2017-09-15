package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.apache.hadoop.io.RawComparator;

/**
 * Sorts the FEdgeKeyWritable from larges to smallest
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FEdgeKeyReverseRawComparator implements RawComparator<FEdgeKeyWritable> {

    @Override
    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {

        return Float.compare(readFloat(b2, s2), readFloat(b1, s1));
    }

    /**
     * Parse a float from a byte array.
     */
    public static float readFloat(byte[] bytes, int start) {

        return Float.intBitsToFloat(readInt(bytes, start));
    }

    /**
     * Parse an integer from a byte array.
     */
    public static int readInt(byte[] bytes, int start) {

        return ((bytes[start] & 0xff) << 24) +
                ((bytes[start + 1] & 0xff) << 16) +
                ((bytes[start + 2] & 0xff) << 8) +
                (bytes[start + 3] & 0xff);

    }

    @Override
    public int compare(FEdgeKeyWritable o1, FEdgeKeyWritable o2) {

        return o1.compareTo(o2);
    }
}
