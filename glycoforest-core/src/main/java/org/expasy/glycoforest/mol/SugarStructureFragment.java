package org.expasy.glycoforest.mol;

import com.google.common.base.Preconditions;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.core.ms.spectrum.IonType;

import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructureFragment extends AbstractSugarStructure {

    private final FragmentType fragmentType;

    protected SugarStructureFragment(FragmentType fragmentType, SugarVertex root) {

        super(root);
        this.fragmentType = fragmentType;
    }

    public FragmentType getFragmentType() {

        return fragmentType;
    }

    public double calculateMz(IonType ionType, int charge, GlycanMassCalculator massCalculator) {

        checkArgument(ionType.getFragmentType() == fragmentType);
        Preconditions.checkArgument(ionType.equals(IonType.y) || ionType.equals(IonType.z) || ionType.equals(IonType.b) || ionType.equals(IonType.c), "Not valid IonType, for Glycosidic fragment only b,c,z,y Ion are allowed");
        checkNotNull(ionType);
        checkArgument(charge != 0, "Cannot calculate m/z, charge was 0");

        return massCalculator.calcMz(getComposition(), ionType, charge);
    }

    public Composition calculateComposition(IonType ionType, int charge, GlycanMassCalculator massCalculator) {

        checkArgument(ionType.getFragmentType() == fragmentType);
        Preconditions.checkArgument(ionType.equals(IonType.y) || ionType.equals(IonType.z) || ionType.equals(IonType.b) || ionType.equals(IonType.c), "Not valid IonType, for Glycosidic fragment only b,c,z,y Ion are allowed");
        checkNotNull(ionType);
        checkArgument(charge != 0, "Cannot calculate m/z, charge was 0");

        return massCalculator.calcComposition(getComposition(), ionType, charge);
    }

    @Override
    public String toString() {

        return fragmentType + " : (" +
                vertexSet() +
                ", " +
                edgeSet().stream().map(edge -> "{" + getEdgeSource(edge) + ", " + getEdgeTarget(edge) + (edge.isDefined() ? " : " + edge.toString() : "") + "}").collect(Collectors.toList()) +
                ')';
    }

    public static class Builder extends AbstractBuilder<Builder, SugarStructureFragment>{

        public Builder(final FragmentType fragmentType, final SugarUnit root) {

            super(root, (rootVertex) -> new SugarStructureFragment(fragmentType, rootVertex));
        }

        @Override
        protected Builder thisReference() {

            return this;
        }

        public SugarStructureFragment build() {

            return doBuild();
        }
    }
}
