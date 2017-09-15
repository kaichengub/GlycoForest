package org.expasy.glycoforest.mol;

import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import scala.Tuple2;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class MissingFragmentFunction {

    public int missingFragments(SugarStructure smaller, SugarStructure larger) {

        final SugarComposition smallerComposition = smaller.getComposition();
        final SugarComposition compositionDelta = larger.getComposition().minus(smallerComposition);
        final List<Tuple2<SugarStructureFragment, SugarStructureFragment>> s1Fragments = smaller.fragmentPairs();
        final List<Tuple2<SugarStructureFragment, SugarStructureFragment>> s2Fragments = larger.fragmentPairs();
        int missing = findMissing(FragmentType.FORWARD, compositionDelta, s1Fragments, s2Fragments, smallerComposition);
        missing += findMissing(FragmentType.REVERSE, compositionDelta, s1Fragments, s2Fragments, smallerComposition);
        return missing;
    }

    private int findMissing(FragmentType fragmentType, SugarComposition compositionDelta, List<Tuple2<SugarStructureFragment, SugarStructureFragment>> smaller, List<Tuple2<SugarStructureFragment, SugarStructureFragment>> larger, SugarComposition smallerComposition) {

        final Function<Tuple2<SugarStructureFragment, SugarStructureFragment>, SugarStructureFragment> typeExtractor;
        if (fragmentType == FragmentType.FORWARD) {
            typeExtractor = Tuple2::_1;
        } else if(fragmentType == FragmentType.REVERSE){
            typeExtractor = Tuple2::_2;
        } else {
            throw new IllegalStateException();
        }
        final Set<SugarComposition> smallerCompositions = smaller.stream()
                .map(typeExtractor)
                .map(AbstractSugarStructure::getComposition)
                .collect(Collectors.toSet());
        smallerCompositions.add(compositionDelta);
        smallerCompositions.add(smallerComposition);

        final Set<SugarComposition> largerCompositions = larger.stream()
                .map(typeExtractor)
                .map(AbstractSugarStructure::getComposition)
                .collect(Collectors.toSet());

        return findMissing(largerCompositions, smallerCompositions, compositionDelta);
    }

    private int findMissing(Set<SugarComposition> largerCompositions, Set<SugarComposition> smallerCompositions, SugarComposition compositionDelta) {

        int missing = 0;
        for(SugarComposition current : largerCompositions) {

            if(!smallerCompositions.contains(current) && !smallerCompositions.contains(current.minus(compositionDelta)))
                missing += 1;
        }
        return missing;
    }
}
