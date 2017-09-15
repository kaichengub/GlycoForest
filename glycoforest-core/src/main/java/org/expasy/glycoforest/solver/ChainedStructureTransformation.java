package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ChainedStructureTransformation extends StructureTransformation {

    private final StructureTransformation[] transformationChain;

    public ChainedStructureTransformation(StructureTransformation... transformationChain) {

        super(makeName(transformationChain), calcMassShift(transformationChain));
        this.transformationChain = transformationChain;
    }

    private static String makeName(StructureTransformation[] transformationChain) {

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < transformationChain.length; i++) {

            StructureTransformation transformation = transformationChain[i];
            if(i > 0)
                buff.append(", ");
            buff.append(transformation.getName());
        }
        return buff.toString();
    }

    private static double calcMassShift(StructureTransformation[] transformationChain) {

        double massShift = 0;
        for(StructureTransformation transformation : transformationChain)
            massShift += transformation.massShift();
        return massShift;
    }

    @Override
    public List<SugarStructure> generateCandidates(SugarStructure src, boolean allowDuplicates) {

        List<SugarStructure> sources = new ArrayList<>();
        List<SugarStructure> generated = new ArrayList<>();
        generated.add(src);

        for(StructureTransformation transformation : transformationChain) {

            sources.clear();
            sources.addAll(generated);
            generated.clear();

            for(SugarStructure current : sources) {

                doAdd(transformation.generateCandidates(current, allowDuplicates), generated, allowDuplicates);
            }
        }

        return generated;
    }

    @Override
    public String toString() {

        return "chained " + Arrays.toString(Stream.of(transformationChain).map(Object::toString).toArray());
    }
}
