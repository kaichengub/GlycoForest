package org.expasy.glycoforest.glycomod;

import com.google.common.base.Preconditions;
import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.utils.Counter;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
class TerminatingCountPredicate implements Predicate<SugarComposition> {

    private final Set<SugarUnit> terminating;

    public TerminatingCountPredicate(SugarUnit[] monomers) {

        terminating = Arrays.stream(monomers).filter(SugarUnit::isTerminating).collect(Collectors.toSet());
    }

    @Override
    public boolean test(SugarComposition composition) {

        int terminatingCount = terminating.stream().mapToInt(composition::getCount).sum();
        final Counter counter = new Counter();
        composition.forEachEntry((sugar, count) -> {

            Preconditions.checkState(count >= 0);
            counter.increment(count);
            return true;
        });

        return terminatingCount  < counter.getCount() - terminatingCount;
    }
}
