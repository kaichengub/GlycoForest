package org.expasy.glycoforest.mol;

import com.google.common.base.Optional;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.glycomics.mol.*;
import org.openide.util.Lookup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ConvertTraversalAcceptor implements TraversalAcceptor {

    private final MonosaccharideLookup monosaccharideLookup = Lookup.getDefault().lookup(MonosaccharideLookup.class);
    private final SubstituentLookup substituentLookup = Lookup.getDefault().lookup(SubstituentLookup.class);

    private final Glycan.Builder builder = new Glycan.Builder();
    private final Map<SugarVertex, Monosaccharide> conversionMap = new HashMap<>();
    private final String dbIdentifier;
    private final Composition endComposition;

    private final Composition waterLoss = Composition.parseComposition("O-1H-1");
    private final Composition hydrogenLoss = Composition.parseComposition("H-1");

    public ConvertTraversalAcceptor(String dbIdentifier, Composition endComposition) {

        this.dbIdentifier = dbIdentifier;

        this.endComposition = endComposition;
    }

    @Override
    public void accept(java.util.Optional<SugarVertex> parent, SugarVertex child, java.util.Optional<StructureLinkage> linkage) {

        final boolean monosaccharide = child.getUnit().isMonosaccharide();
        if (parent.isPresent()) {

            if (monosaccharide) {
                convertSugarVertex(parent.get(), child);
            } else {
                convertSubstituentVertex(parent.get(), child);
            }
        } else {

            if (monosaccharide) {
                convertRoot(child);
            } else {
                throw new IllegalStateException("Cannot use a substituent as the root of a glycan. Root was " + child);
            }
        }
    }

    private void convertSubstituentVertex(SugarVertex parent, SugarVertex child) {

        final Substituent substituent;
        final SubstituentLinkage linkage;

        switch (child.getUnit()) {

            case S:
                substituent = substituentLookup.getNew("Sulfate");
                linkage = new SubstituentLinkage(Optional.<Integer>absent(), Optional.of(Composition.parseComposition("H-1")));
                break;

            default:
                throw new IllegalStateException("Cannot convert " + child);
        }

        builder.add(substituent, conversionMap.get(parent), linkage);
    }

    private void convertSugarVertex(SugarVertex parent, SugarVertex child) {

        Monosaccharide parentNode = conversionMap.get(parent);
        Monosaccharide childNode = convertMonosaccharide(child);

        if (parentNode == null)
            throw new NullPointerException("Null parent for edge " + parent + " " + child);

        builder.add(childNode, parentNode, convertLinkage(parent, child));
        addSubstituents(child, childNode);
    }

    private GlycosidicLinkage convertLinkage(SugarVertex parent, SugarVertex child) {

        SugarUnit unit = child.getUnit();
        switch (unit) {

            case Neu5Ac:
            case Neu5Gc:
            case Kdn:
                return new GlycosidicLinkage(Optional.<Anomericity>absent(), Optional.of(2), Optional.<Integer>absent(),
                        Optional.of(waterLoss), Optional.of(hydrogenLoss));
            case Hex:
            case HexNAc:
            case Fuc:
                return new GlycosidicLinkage(Optional.<Anomericity>absent(), Optional.of(1), Optional.<Integer>absent(),
                        Optional.of(waterLoss), Optional.of(hydrogenLoss));
            default:
                throw new IllegalStateException("Don't know how to convert link fom " + parent + " to " + child);
        }
    }

    private void convertRoot(SugarVertex root) {

        Monosaccharide rootNode = builder.setRoot(convertMonosaccharide(root), Optional.<Anomericity>absent(), dbIdentifier, endComposition);
        addSubstituents(root, rootNode);
    }

    private void addSubstituents(SugarVertex vertex, Monosaccharide monosaccharide) {

        for (SubstituentTuple tuple : getSubstituents(vertex)) {

            builder.add(tuple.substituent, monosaccharide, tuple.linkage);
        }
    }

    private Collection<SubstituentTuple> getSubstituents(SugarVertex vertex) {

        switch (vertex.getUnit()) {

            case Neu5Ac:
            case HexNAc:
                return Collections.singleton(new SubstituentTuple(substituentLookup.getNew("NAcetyl"), new SubstituentLinkage(2, waterLoss)));
            case Neu5Gc:
                return Collections.singleton(new SubstituentTuple(substituentLookup.getNew("NGlycolyl"), new SubstituentLinkage(5, waterLoss)));
            default:
                return Collections.emptySet();
        }
    }

    private Monosaccharide convertMonosaccharide(SugarVertex vertex) {

        final Monosaccharide monosaccharide;
        switch (vertex.getUnit()) {

            case Fuc:
                monosaccharide = monosaccharideLookup.getNew("Fuc");
                break;
            case Hex:
            case HexNAc:
                monosaccharide =  monosaccharideLookup.getNew("Hex");
                break;
            case Neu5Ac:
            case Neu5Gc:
            case Kdn:
                monosaccharide =  monosaccharideLookup.getNew("Kdn");
                break;
            default:
                throw new IllegalStateException("Cannot convert " + vertex);
        }

        conversionMap.put(vertex, monosaccharide);
        return monosaccharide;
    }

    public Glycan build() {

        return builder.build();
    }

    private static class SubstituentTuple {

        private final Substituent substituent;
        private final SubstituentLinkage linkage;

        public SubstituentTuple(Substituent substituent, SubstituentLinkage linkage) {

            this.substituent = substituent;
            this.linkage = linkage;
        }
    }
}
