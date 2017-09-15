package org.expasy.glycoforest.app;

import com.google.common.collect.ComparisonChain;
import gnu.trove.map.TIntIntMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ClusterFmf implements Serializable {

        int[] extractCharge(TIntIntMap chargeMap) {

            final List<int[]> chargeCount = new ArrayList<>(chargeMap.size());
            chargeMap.forEachEntry((charge, count) -> {
                if (charge > 0)
                    chargeCount.add(new int[]{charge, count});
                return true;
            });

            if(chargeCount.isEmpty())
                return new int[]{1};

            return chargeCount.stream()
                    .sorted((c1, c2) -> ComparisonChain.start().compare(c2[1], c1[1]).compare(c2[0], c1[0]).result())
                    .mapToInt(c -> c[0])
                    .toArray();
        }
}
