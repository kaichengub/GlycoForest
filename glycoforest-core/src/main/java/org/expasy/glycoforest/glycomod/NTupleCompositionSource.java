package org.expasy.glycoforest.glycomod;

import org.expasy.glycoforest.mol.SugarComposition;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.utils.MixedRadixNtupleGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class NTupleCompositionSource implements CompositionSource {

    private final int numMonomers;
    private final List<Predicate<SugarComposition>> predicates;
    private final SugarUnit[] monomers = SugarUnit.values();

    public NTupleCompositionSource(int numMonomers, List<Predicate<SugarComposition>> predicates) {

        this.numMonomers = numMonomers;
        this.predicates = predicates;
    }

    @Override
    public void createCompositions(Consumer<SugarComposition> consumer) {

        MixedRadixNtupleGenerator generator = new MixedRadixNtupleGenerator(ntuple -> {

            SugarComposition composition = new SugarComposition(Arrays.stream(ntuple).mapToObj(i -> monomers[i]));

            if(predicates.stream().allMatch(p -> p.test(composition)))
                consumer.accept(composition);
        });

        for(int n = 2; n <= numMonomers; n++)
            generator.generate(n, monomers.length);
    }

    @Override
    public int estimateSize() {

        return (int)Math.pow(numMonomers, monomers.length);
    }
}
