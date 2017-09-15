package org.expasy.glycoforest.mol;

import com.google.common.base.Preconditions;
import org.expasy.mzjava.core.mol.Atom;
import org.expasy.mzjava.core.mol.AtomicSymbol;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.peaklist.Polarity;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.core.ms.spectrum.IonType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycanMassCalculator {

    private final Composition chargeCarrier;
    private final Polarity polarity;
    private final Composition reducingEndComposition;

    private final Map<IonType, Composition> ionTypeCompositionMassMap;

    private static final Composition water = Composition.parseComposition("H2O");

    public GlycanMassCalculator(Composition chargeCarrier, Polarity polarity, Composition reducingEndComposition) {

        if (polarity == Polarity.UNKNOWN)
            throw new IllegalArgumentException("Polarity has to be defined, was " + polarity);

        this.chargeCarrier = chargeCarrier;
        this.polarity = polarity;
        this.reducingEndComposition = reducingEndComposition;

        ionTypeCompositionMassMap = new EnumMap<>(IonType.class);
        ionTypeCompositionMassMap.put(IonType.b, Composition.parseComposition("H-2"));
        ionTypeCompositionMassMap.put(IonType.c, Composition.parseComposition("O"));
        ionTypeCompositionMassMap.put(IonType.y, Composition.parseComposition("OH4"));
        ionTypeCompositionMassMap.put(IonType.z, Composition.parseComposition("H2"));
        ionTypeCompositionMassMap.put(IonType.p, Composition.parseComposition("H2O"));
    }

    public double calcReducedMass(SugarComposition composition) {

        return composition.getMass() + water.getMolecularMass() + reducingEndComposition.getMolecularMass();
    }

    public double calcMz(double mass, int charge) {

        Preconditions.checkArgument(charge > 0);

        final double chargeMassDelta = getChargeMassDelta(charge);

        return (mass + water.getMolecularMass() + reducingEndComposition.getMolecularMass() + chargeMassDelta) / charge;
    }

    public double calcMz(SugarComposition composition, int charge) {

        return calcMz(composition.getMass(), charge);
    }

    public int calcNominalMass(SugarComposition composition, int charge) {

        Preconditions.checkArgument(charge >= 0);

        return composition.monomerStream().mapToInt(sugarUnit -> calcMassNumberSum(sugarUnit.getComposition())).sum() +
                calcMassNumberSum(water) +
                calcMassNumberSum(reducingEndComposition) +
                (calcMassNumberSum(chargeCarrier) * charge * (polarity == Polarity.POSITIVE ? 1 : -1));
    }

    private int calcMassNumberSum(Composition composition) {

        int sum = 0;
        for (Atom atom : composition.getAtoms()) {

            sum += atom.getMassNumber() * composition.getCount(atom);
        }
        return sum;
    }

    public double calcMz(SugarComposition composition, IonType ionType, int charge) {

        Preconditions.checkArgument(charge > 0);
        Preconditions.checkArgument(ionType.equals(IonType.y) || ionType.equals(IonType.z) || ionType.equals(IonType.b) || ionType.equals(IonType.c), "Not valid IonType, for Glycosidic fragment only b,c,z,y Ion are allowed");

        final double chargeMassDelta = getChargeMassDelta(charge);

        final double reducingEndMass = ionType.getFragmentType() == FragmentType.FORWARD ? reducingEndComposition.getMolecularMass() : 0.0;

        return (composition.getMass() + ionTypeCompositionMassMap.get(ionType).getMolecularMass() + reducingEndMass + chargeMassDelta) / charge;
    }

    public Composition calcComposition(SugarComposition composition, IonType ionType, int charge) {

        Preconditions.checkArgument(charge > 0);
        Preconditions.checkArgument(ionType.equals(IonType.p) || ionType.equals(IonType.y) || ionType.equals(IonType.z) || ionType.equals(IonType.b) || ionType.equals(IonType.c), "Not valid IonType, for Glycosidic fragment only p, b,c,z,y Ion are allowed");

        final Composition correctedChargeCarrier = polarity == Polarity.POSITIVE ? chargeCarrier : negate(chargeCarrier);
        final Stream<Composition> compStream = Stream.of(
                composition.monomerStream().map(SugarUnit::getComposition),
                Stream.of(ionTypeCompositionMassMap.get(ionType)),
                (ionType.getFragmentType() == FragmentType.FORWARD || ionType.getFragmentType() == FragmentType.INTACT) ? Stream.of(reducingEndComposition) : Stream.<Composition>empty(),
                Stream.generate(() -> correctedChargeCarrier).limit(charge)
        ).flatMap(Function.identity());

        return new Composition(compStream.toArray(Composition[]::new));
    }

    private Composition negate(Composition composition) {

        Composition.Builder builder = new Composition.Builder();

        for (Atom atom : composition.getAtoms()) {
            builder.add(atom, -composition.getCount(atom));
        }
        builder.charge(-composition.getCharge());
        return builder.build();
    }

    public double calcCompositionMass(double mz, int charge) {

        Preconditions.checkArgument(charge > 0);

        final double chargeMassDelta = getChargeMassDelta(charge);

        return (mz * charge) - (water.getMolecularMass() + reducingEndComposition.getMolecularMass() + chargeMassDelta);
    }

    private double getChargeMassDelta(int charge) {

        final double chargeMassDelta;
        switch (polarity) {
            case POSITIVE:
                chargeMassDelta = charge * chargeCarrier.getMolecularMass();
                break;
            case NEGATIVE:
                chargeMassDelta = -charge * chargeCarrier.getMolecularMass();
                break;
            default:
                throw new IllegalStateException("Unknown polarity " + polarity);
        }
        return chargeMassDelta;
    }

    public static GlycanMassCalculator newEsiNegativeReduced() {

        return new GlycanMassCalculator(new Composition.Builder().add(AtomicSymbol.H, 1).charge(1).build(), Polarity.NEGATIVE, Composition.parseComposition("H2"));
    }

    public static GlycanMassCalculator newEsiPositiveReduced() {

        return new GlycanMassCalculator(new Composition.Builder().add(AtomicSymbol.Na, 1).charge(1).build(), Polarity.POSITIVE, Composition.parseComposition("H2"));
    }
}
