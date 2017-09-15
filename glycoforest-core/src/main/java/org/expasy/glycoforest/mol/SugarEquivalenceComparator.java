package org.expasy.glycoforest.mol;

import java.util.Comparator;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarEquivalenceComparator implements Comparator<SugarVertex> {

    @Override
    public int compare(SugarVertex sugar1, SugarVertex sugar2) {

        return sugar1.getUnit().compareTo(sugar2.getUnit());
    }
}
