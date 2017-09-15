package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarStructure;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SpectrumAnnotation {

    public enum Type {MANUAL, AUTOMATIC}

    public final double score;
    public final Type type;
    public final SugarStructure structure;

    public SpectrumAnnotation(double score, Type type, SugarStructure structure) {

        this.score = score;
        this.type = type;
        this.structure = structure;
    }

    public double getScore() {

        return score;
    }

    public SugarStructure getStructure() {

        return structure;
    }

    public Type getType() {

        return type;
    }
}
