package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
class EdgeKeyComparator implements Comparator<EdgeKey>, Serializable {

    @Override
    public int compare(EdgeKey k1, EdgeKey k2) {

        return k1.compareTo(k2);
    }
}
