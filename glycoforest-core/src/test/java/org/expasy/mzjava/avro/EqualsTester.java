package org.expasy.mzjava.avro;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public interface EqualsTester<O> {

    boolean isEquals(O o1, O o2);
}