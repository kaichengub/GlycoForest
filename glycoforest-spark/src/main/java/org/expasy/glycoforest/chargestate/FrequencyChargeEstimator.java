package org.expasy.glycoforest.chargestate;

import org.expasy.mzjava.core.ms.peaklist.PeakList;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Estimates the charge state
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FrequencyChargeEstimator implements MemberChargeEstimator {

    @Override
    public int[] estimateChargeState(Collection<? extends PeakList> members) {

        if(members.isEmpty())
            throw new IllegalArgumentException("Members list was empty, cannot estimate charge state.");

        Map<Integer, Long> chargeFrequency = members.stream().map(member -> member.getPrecursor().getCharge())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        int mostCommonCharge = chargeFrequency.entrySet().stream().max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get().getKey();
        if(mostCommonCharge == 0)
            return new int[]{1};
        else
            return new int[]{mostCommonCharge};
    }
}
