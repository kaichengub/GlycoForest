package org.expasy.glycoforest.mol;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.expasy.mzjava.core.mol.Composition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarComposition {

    public static final SugarComposition EMPTY = new SugarComposition();

    private final TObjectIntMap<SugarUnit> composition = new TObjectIntHashMap<>(SugarUnit.values().length, Constants.DEFAULT_LOAD_FACTOR, Integer.MIN_VALUE);

    private double mass;

    public SugarComposition() {

        mass = 0;
    }

    public SugarComposition(Stream<SugarUnit> stream) {

        final Sum tmpMass = new Sum();
        stream.forEach(sugar -> {

            composition.adjustOrPutValue(sugar, 1, 1);
            tmpMass.increment(sugar.getUnitMass());
        });

        mass = tmpMass.getResult();
    }

    public SugarComposition(SugarUnit... sugarUnits) {

        this(Arrays.stream(sugarUnits));
    }

    private SugarComposition(SugarComposition src) {

        src.composition.forEachEntry((k, v) -> {

            composition.put(k, v);
            return true;
        });
        mass = src.mass;
    }

    public void forEachEntry(TObjectIntProcedure<? super SugarUnit> procedure){

        composition.forEachEntry(procedure);
    }

    public Stream<SugarUnit> monomerStream() {

        final List<SugarUnit> monomers = new ArrayList<>(IntStream.of(composition.values()).sum());

        composition.forEachEntry((sugarUnit, count) -> {

            for(int i = 0; i < count; i++) {

                monomers.add(sugarUnit);
            }
            return true;
        });

        return monomers.stream();
    }

    public IntStream valueStream(){

        return IntStream.of(composition.values());
    }

    public boolean contains(SugarUnit target) {

        return composition.containsKey(target);
    }

    public int getCount(SugarUnit sugarUnit) {

        return composition.containsKey(sugarUnit) ? composition.get(sugarUnit) : 0;
    }

    public Composition toAtomComposition(){

        return new Composition(monomerStream().map(SugarUnit::getComposition).toArray(Composition[]::new));
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SugarComposition that = (SugarComposition) o;
        return composition.equals(that.composition);
    }

    @Override
    public int hashCode() {

        return composition.hashCode();
    }

    public SugarComposition add(SugarUnit sugarUnit) {

        final SugarComposition newComposition = new SugarComposition(this);
        newComposition.composition.adjustOrPutValue(sugarUnit, 1, 1);
        newComposition.updateMass();
        return newComposition;
    }

    public SugarComposition minus(SugarComposition composition) {

        SugarComposition result = new SugarComposition(this);

        composition.forEachEntry((sugarUnit, count) -> {

            final int adjustResult = result.composition.adjustOrPutValue(sugarUnit, -count, -count);
            if (adjustResult == 0)
                result.composition.remove(sugarUnit);
            return true;
        });

        result.updateMass();

        return result;
    }

    public SugarComposition negate() {

        SugarComposition negated = new SugarComposition();

        composition.forEachEntry(((sugarUnit, count) -> {

            negated.composition.put(sugarUnit, -count);
            return true;
        }));

        negated.updateMass();
        return negated;
    }

    private void updateMass() {

        final Sum massShift = new Sum();
        composition.forEachEntry((u, c) -> {

            massShift.increment(u.getUnitMass() * c);
            return true;
        });

        mass = massShift.getResult();
    }

    @Override
    public String toString() {

        StringBuilder buff = new StringBuilder();
        buff.append('{');
        for(SugarUnit unit : SugarUnit.values()) {

            if(composition.containsKey(unit)) {

                int count = composition.get(unit);
                if(buff.length() > 1)
                    buff.append(", ");
                buff.append(count).append(" ").append(unit);
            }

        }
        buff.append('}');
        return buff.toString();
    }

    public double getMass() {

        return mass;
    }

    public int size() {

        return IntStream.of(composition.values()).sum();
    }

    public static class Builder {

        private final List<SugarUnit> units = new ArrayList<>();

        public SugarComposition build(){

            return new SugarComposition(units.stream());
        }

        public Builder add(SugarUnit sugarUnit) {

            units.add(sugarUnit);

            return this;
        }

        public Builder put(SugarUnit sugarUnit, int count) {

            for(int i = 0; i < count; i++)
                units.add(sugarUnit);

            return this;
        }
    }
}
