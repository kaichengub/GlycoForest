package org.expasy.glycoforest.writer;

import org.expasy.glycoforest.mol.AbstractSugarStructure;
import org.expasy.glycoforest.mol.StructureLinkage;
import org.expasy.glycoforest.mol.SugarUnit;

import java.util.Optional;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class IupacCondensedWriter extends AbstractIupacWriter {

    @Override
    protected String getOpeningBracket() {

        return "]";
    }

    @Override
    protected String getClosingBracket() {

        return "[";
    }


    @Override
    protected String formatLinkage(Optional<StructureLinkage> optionalLinkage, AbstractSugarStructure structure) {

        if (optionalLinkage.isPresent()) {
            if (structure.getEdgeTarget(optionalLinkage.get()).isUnit(SugarUnit.S)) {

                return "(6)";
            } else {

                return "(" + optionalLinkage.get().toString() + ")";
            }
        } else {

            return "(-)";
        }

    }


    @Override
    protected String formatSugarUnit(SugarUnit sugarUnit) {

        switch (sugarUnit) {

            case Neu5Ac:
                return "NeuAc";

            case Neu5Gc:
                return "NeuGc";

            case Hex:
            case Fuc:
            case HexNAc:
            case Kdn:
            case S:
                return sugarUnit.name();
            default:
                throw new IllegalStateException("Cannot get name for " + sugarUnit);

        }
    }

    @Override
    protected String formatRoot(SugarUnit sugarUnit) {

        return sugarUnit.name();
    }
}
