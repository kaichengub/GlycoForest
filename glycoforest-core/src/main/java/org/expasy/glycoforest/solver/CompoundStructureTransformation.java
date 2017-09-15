package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CompoundStructureTransformation extends StructureTransformation {

    private final List<StructureTransformation> transformations;

    public CompoundStructureTransformation(String name, List<StructureTransformation> transformations) {

        super(name, transformations.get(0).massShift());

        double delta = 0.001;
        double expectedMassShift = massShift();
        for(StructureTransformation transformation : transformations) {

            if (Math.abs(expectedMassShift - transformation.massShift()) > delta)
                throw new IllegalArgumentException("Mass shifts are not the same " + Arrays.toString(transformations.stream().mapToDouble(StructureTransformation::massShift).toArray()));
        }
        this.transformations = transformations;
    }

    public CompoundStructureTransformation(List<StructureTransformation> transformations) {

        this(createName(transformations), transformations);
    }

    private static String createName(List<StructureTransformation> transformations) {

        return transformations.stream().map(StructureTransformation::getName).collect(Collectors.joining("; "));
    }

    @Override
    public List<SugarStructure> generateCandidates(SugarStructure src, boolean allowDuplicates) {

        checkNotNull(src);
        checkNotNull(allowDuplicates);

        List<SugarStructure> allCandidates = new ArrayList<>();
        for(StructureTransformation transformation : transformations) {

            doAdd(transformation.generateCandidates(src, allowDuplicates), allCandidates, allowDuplicates);
        }

        return allCandidates;
    }
}
