package org.expasy.glycoforest.app;

import com.google.common.base.Preconditions;
import org.expasy.glycoforest.solver.StructureTransformation;
import org.expasy.glycoforest.solver.WithinRunStructureVertex;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
class OpenSearchResult {

    private final WithinRunStructureVertex reference;
    private final WithinRunStructureVertex query;
    private final double massShift;
    private final double score;
    private final StructureTransformation structureTransformation;

    public OpenSearchResult(WithinRunStructureVertex reference, WithinRunStructureVertex query, double massShift, double score, final StructureTransformation structureTransformation) {

        this.structureTransformation = structureTransformation;

        Preconditions.checkArgument(reference.getCharge() == query.getCharge());

        this.reference = reference;
        this.query = query;
        this.massShift = massShift;
        this.score = score;
    }

    public WithinRunStructureVertex getReference() {

        return reference;
    }

    public WithinRunStructureVertex getQuery() {

        return query;
    }

    public double getMassShift() {

        return massShift;
    }

    public double getScore() {

        return score;
    }

    public int getCharge() {

        return reference.getCharge();
    }

    public StructureTransformation getStructureTransformation() {

        return structureTransformation;
    }
}
