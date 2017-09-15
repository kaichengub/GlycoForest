package org.expasy.glycoforest.mol;

import org.expasy.mzjava.core.mol.Composition;

import static org.expasy.mzjava.core.mol.Composition.parseComposition;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public enum SugarUnit {

    Hex(parseComposition("C6H10O5"), false, false),
    Fuc(parseComposition("C6H10O4"), true, false),
    HexNAc(parseComposition("C8O5NH13"), false, false),
    Neu5Ac(parseComposition("C11O8NH17"), true, false),
    Neu5Gc(parseComposition("C11O9NH17"), true, false),
    Kdn(parseComposition("C9H14O8"), true, false),
    S(parseComposition("SO3"), true, true),
    Xyl(parseComposition("C5H8O4"), true, false);

    private final double unitMass;
    private final Composition composition;
    private final boolean terminating;
    private final boolean substituent;

    SugarUnit(Composition composition, boolean terminating, boolean substituent){

        this.composition = composition;
        this.unitMass = composition.getMolecularMass();
        this.terminating = terminating;
        this.substituent = substituent;
    }

    public Composition getComposition() {

        return composition;
    }

    public double getUnitMass() {

        return unitMass;
    }

    public boolean isTerminating() {

        return terminating;
    }

    public boolean isExtensible() {

        return !terminating;
    }

    public boolean isSubstituent() {

        return substituent;
    }

    public boolean isMonosaccharide(){

        return !substituent;
    }
}
