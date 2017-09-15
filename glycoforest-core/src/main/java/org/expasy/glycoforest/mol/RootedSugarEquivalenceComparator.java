package org.expasy.glycoforest.mol;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RootedSugarEquivalenceComparator implements Comparator<SugarVertex> {

    private final Set<SugarVertex> roots;

    public RootedSugarEquivalenceComparator(SugarVertex root1, SugarVertex root2) {

        roots = new HashSet<>(2);
        roots.add(root1);
        roots.add(root2);
    }

    @Override
    public int compare(SugarVertex sugar1, SugarVertex sugar2) {

        if (roots.contains(sugar1) && roots.contains(sugar2))
            return 0;
        else if (roots.contains(sugar1))
            return -1;
        else if (roots.contains(sugar2))
            return 1;
        else
            return sugar1.getUnit().compareTo(sugar2.getUnit());
    }
}
