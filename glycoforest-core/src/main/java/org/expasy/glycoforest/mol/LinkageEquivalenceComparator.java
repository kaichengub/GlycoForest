package org.expasy.glycoforest.mol;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import java.util.Comparator;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class LinkageEquivalenceComparator implements Comparator<StructureLinkage> {

    @Override
    public int compare(StructureLinkage s1, StructureLinkage s2) {

        return ComparisonChain.start()
                .compare(s1.getAnomericity().orElse(null), s2.getAnomericity().orElse(null), Ordering.natural().nullsFirst())
                .compare(s1.getAnomericCarbon().orElse(null), s2.getAnomericCarbon().orElse(null), Ordering.natural().nullsFirst())
                .compare(s1.getLinkedCarbon().orElse(null), s2.getLinkedCarbon().orElse(null), Ordering.natural().nullsFirst())
                .result();
    }
}
