package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureAddition extends StructureTransformation {

    private final Set<SugarExtension> extensions;
    private final VertexPredicate vertexPredicate;
    private final EdgePredicate edgePredicate;

    public StructureAddition(String name, Set<SugarExtension> extensions, VertexPredicate vertexPredicate, EdgePredicate edgePredicate) {

        super(name, extractMassShift(extensions));

        checkNotNull(extensions);

        this.extensions = extensions;
        this.vertexPredicate = vertexPredicate;
        this.edgePredicate = edgePredicate;
    }

    private static double extractMassShift(Set<SugarExtension> extensions) {

        checkArgument(!extensions.isEmpty());

        double[] masses = extensions.stream().mapToDouble(ext -> ext.getComposition().getMass()).sorted().toArray();
        if(masses[0] != masses[masses.length - 1])
            throw new IllegalArgumentException("Extensions do not have same mass " + Arrays.toString(masses));

        return masses[0];
    }

    public static StructureAddition noTerminal(SugarUnit extension) {

        return new StructureAddition("+" + extension.toString(), Collections.singleton(new SugarExtension.Builder(extension.toString(), extension).setOut().build()),
                (v, g) -> v.getUnit().isExtensible() && g.outDegreeOf(v) < 2,
                (p, c, g) -> true);
    }

    public static StructureAddition noInsertNoTerminal(SugarUnit extension) {

        return new StructureAddition("+" + extension.toString(), Collections.singleton(new SugarExtension.Builder(extension.toString(), extension).setOut().build()),
                (v, g) -> v.getUnit().isExtensible() && g.outDegreeOf(v) < 2,
                (p, c, g) -> false);
    }

    public static StructureAddition noInsertExtendSameTerminal(SugarUnit extension) {

        return new StructureAddition("+" + extension.toString(), Collections.singleton(new SugarExtension.Builder(extension.toString(), extension).setOut().build()),
                (v, g) -> (v.getUnit().isExtensible() || v.isUnit(extension)) && g.outDegreeOf(v) < 2,
                (p, c, g) -> false);
    }

    @Override
    public List<SugarStructure> generateCandidates(SugarStructure src, boolean allowDuplicates) {

        checkNotNull(src);
        checkNotNull(allowDuplicates);

        List<SugarStructure> allCandidates = new ArrayList<>();
        for(SugarExtension extension : extensions) {

            doAdd(src.extend(src.getLabel() + " " + name, extension, vertexPredicate), allCandidates, allowDuplicates);
            doAdd(src.insert(src.getLabel() + " " + name, extension, extension.getOut(), edgePredicate), allCandidates, allowDuplicates);
        }

        return allCandidates;
    }
}
