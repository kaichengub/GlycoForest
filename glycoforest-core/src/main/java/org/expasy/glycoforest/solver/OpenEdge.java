package org.expasy.glycoforest.solver;

import java.util.Arrays;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class OpenEdge {

    private final int charge;
    private final double score;
    private final double massShift;
    private final StructureTransformation transformation;
    private final StructureVertex vertex1;
    private final StructureVertex vertex2;

    public OpenEdge(int charge, double score, double massShift, StructureTransformation transformation, StructureVertex vertex1, StructureVertex vertex2) {

        this.charge = charge;
        this.score = score;
        this.massShift = massShift;
        this.transformation = transformation;
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    public int getCharge() {

        return charge;
    }

    public double getScore() {

        return score;
    }

    public double getMassShift() {

        return massShift;
    }

    public StructureTransformation getTransformation() {

        return transformation;
    }

    public StructureVertex getVertex1() {

        return vertex1;
    }

    public StructureVertex getVertex2() {

        return vertex2;
    }

    @Override
    public String toString() {

        return "OpenEdge{" +
                "z=" + charge +
                "score=" + score +
                ", " + transformation +
                ", vertex1=" + Arrays.toString(vertex1.annotationStream().map(annotation -> annotation.getStructure().getLabel()).toArray()) +
                ", vertex2=" + Arrays.toString(vertex2.annotationStream().map(annotation -> annotation.getStructure().getLabel()).toArray())+
                '}';
    }
}
