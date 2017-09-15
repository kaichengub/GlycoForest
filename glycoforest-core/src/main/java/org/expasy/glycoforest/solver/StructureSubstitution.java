package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.SugarExtension;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.glycoforest.mol.VertexPredicate;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureSubstitution extends StructureTransformation {

    private final VertexPredicate vertexPredicate;
    private final SugarExtension extension;

    public StructureSubstitution(String name, SugarExtension extension, double massShift, VertexPredicate vertexPredicate) {

        super(name, massShift);

        this.extension = extension;
        this.vertexPredicate = vertexPredicate;
    }

    public static StructureSubstitution singleSubstitution(SugarUnit src, SugarUnit dest){

        return new StructureSubstitution(src + " > " + dest, new SugarExtension.Builder(src + " > " + dest, dest).setOut().build(), dest.getUnitMass() - src.getUnitMass(), (v, g) -> v.getUnit().equals(src));
    }

    @Override
    public List<SugarStructure> generateCandidates(SugarStructure src, boolean allowDuplicates) {

        checkNotNull(src);

        List<SugarStructure> allCandidates = new ArrayList<>();
        final String newLabel = src.getLabel() + " " + name;
        if (extension.vertexSet().size() == 1) {

            doAdd(src.substitute(newLabel, extension.getRoot().getUnit(), vertexPredicate), allCandidates, allowDuplicates);
        } else {

            doAdd(src.substitute(newLabel, extension, vertexPredicate), allCandidates, allowDuplicates);
        }

        return allCandidates;
    }

    @Override
    public String getName() {

        return name;
    }
}
