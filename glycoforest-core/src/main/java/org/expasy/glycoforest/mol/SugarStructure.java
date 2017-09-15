package org.expasy.glycoforest.mol;

import com.google.common.collect.Sets;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.glycomics.mol.*;
import org.jgrapht.Graphs;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * To create a SugarGraph the builder is used.
 * <p>
 * For example the sugar:
 * <pre>
 *          HexNac
 *                \
 *                 HexNAc
 *               /
 * Hex-HexNAc-Hex
 * </pre>
 * Can be built using
 * <pre><code>
 * <p>
 * final SugarGraph graph = new SugarGraph.Builder(HexNAc)
 *          .branch()
 *          .add(HexNAc)
 *          .pop()
 *          .add(Hex).add(HexNAc).add(Hex)
 *          .build();
 * </code></pre>
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructure extends AbstractSugarStructure {

    private final String label;

    protected SugarStructure(String label, SugarVertex root) {

        super(root);
        this.label = label;
    }

    protected SugarStructure(String newLabel, AbstractSugarStructure src) {

        super(src);
        this.label = newLabel;
    }

    public static SugarStructure fromGlycan(Glycan src) {

        final MonosaccharideConverter converter = new MonosaccharideConverter(src);

        final SugarStructure graph = new SugarStructure(src.getDatabaseIdentifier(), converter.convert(src.getRoot()));
        src.forEachLinkage(SaccharideGraph.Traversal.DFS, (parent, child, linkage) -> {

            graph.doAdd(converter.convert(parent), converter.convert(child));
            converter.addSubstituentsOf(child, graph);
        });
//        graph.idCounter = converter.idCounter;

        return graph;
    }

    public Glycan toGlycan(Composition rootCompositionDelta){

        return toGlycan(getRoot(), rootCompositionDelta);
    }

    public Glycan toGlycan(SugarVertex start, Composition rootCompositionDelta){

        final ConvertTraversalAcceptor converter = new ConvertTraversalAcceptor(label, rootCompositionDelta);
        dfs(start, converter);
        return converter.build();
    }

    public String getLabel() {

        return label;
    }

    public SugarStructure setLabel(String newLabel) {

        return new SugarStructure(newLabel, this);
    }

    private TIntObjectMap<SugarVertex> add(AbstractSugarStructure fragment, SugarVertex extensionNode) {

        final TIntObjectMap<SugarVertex> idMap = new TIntObjectHashMap<>();
        fragment.dfs((parentOpt, child, linkage) -> {

            if (parentOpt.isPresent()) {

                addCopyOf(idMap.get(parentOpt.get().getId()), child, idMap);
            } else {

                idMap.put(child.getId(), doAdd(extensionNode, child.getUnit()));
            }
        });
        return idMap;
    }

    public List<SugarStructure> extend(String newLabel, AbstractSugarStructure extension, VertexPredicate predicate) {

        return vertexSet().stream()
                .filter(v -> predicate.test(v, this))
                .map(node -> {

                    final SugarStructure copy = new SugarStructure(newLabel, this);
                    copy.add(extension, node);
                    return copy;
                })
                .collect(Collectors.toList());
    }

    public List<SugarStructure> insert(String newLabel, AbstractSugarStructure insertion, SugarVertex out, EdgePredicate predicate) {

        return edgeSet().stream()
                .filter(edge -> predicate.test(getEdgeSource(edge), getEdgeTarget(edge), this))
                .map(edge -> {

                    final SugarStructure copy = new SugarStructure(newLabel, this);
                    final SugarVertex s = copy.getEdgeSource(edge);
                    final SugarVertex t = copy.getEdgeTarget(edge);
                    copy.doRemove(edge);

                    SugarVertex outCopy = copy.add(insertion, s).get(out.getId());
                    copy.doAdd(outCopy, t);
                    return copy;
                })
                .collect(Collectors.toList());
    }

    public List<SugarStructure> substitute(String newLabel, SugarUnit newClass, VertexPredicate predicate) {

        return vertexSet().stream()
                .filter(node -> predicate.test(node, this))
                .map(candidate -> {

                    final SugarStructure copy = new SugarStructure(newLabel, this);
                    copy.replace(candidate, newClass);
                    return copy;
                })
                .collect(Collectors.toList());
    }

    public List<SugarStructure> substitute(String newLabel, AbstractSugarStructure replacement, VertexPredicate predicate) {

        return vertexSet().stream()
                .filter(node -> predicate.test(node, this))
                .map(candidate -> {

                    final SugarStructure copy = new SugarStructure(newLabel, this);
                    final SugarVertex replacementRoot = copy.replace(candidate, replacement.getRoot().getUnit());
                    final TIntObjectMap<SugarVertex> idMap = new TIntObjectHashMap<>();
                    idMap.put(replacement.getRoot().getId(), replacementRoot);
                    replacement.dfs((parentOpt, child, linkage) -> {

                        if (parentOpt.isPresent()) {

                            copy.addCopyOf(idMap.get(parentOpt.get().getId()), child, idMap);
                        }
                    });
                    return copy;
                })
                .collect(Collectors.toList());
    }

    public List<SugarStructure> remove(String newLabel,  VertexPredicate predicate) {

        return vertexSet().stream()
                .filter(node -> predicate.test(node, this) && !node.equals(getRoot()))
                .map(toRemove -> {

                    final SugarStructure copy = new SugarStructure(newLabel, this);
                    final List<SugarVertex> inNodes = copy.incomingEdgesOf(toRemove).stream().map(e -> copy.oppositeVertex(e, toRemove)).collect(Collectors.toList());
                    if (inNodes.size() != 1)
                        throw new IllegalStateException(toRemove + " has no parent");
                    final List<SugarVertex> outNodes = copy.outgoingEdgesOf(toRemove).stream().map(e -> copy.oppositeVertex(e, toRemove)).collect(Collectors.toList());

                    copy.doRemove(toRemove);

                    final SugarVertex inNode = inNodes.get(0);
                    for (SugarVertex outNode : outNodes) {

                        copy.doAdd(inNode, outNode);
                    }

                    return copy;
                })
                .collect(Collectors.toList());
    }

    /**
     * Add a sugar that is a copy of template to the parent. The copy is cached in the
     * templateIdToCopyMap so that only one copy is made for each template id.
     *
     * @param parent              the parent
     * @param template            the Sugar that serves as a template
     * @param templateIdToCopyMap the map that holds previously copied sugars
     */
    private void addCopyOf(SugarVertex parent, SugarVertex template, TIntObjectMap<SugarVertex> templateIdToCopyMap) {

        final SugarVertex newChild = templateIdToCopyMap.get(template.getId());
        if (newChild != null)
            doAddEdge(parent, newChild);
        else
            templateIdToCopyMap.put(template.getId(), doAdd(parent, template.getUnit()));
    }

    public List<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragmentPairs() {

        final List<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragments = new ArrayList<>();
        for(StructureLinkage edge : edgeSet()) {

            final SugarVertex edgeTarget = getEdgeTarget(edge);

            final FragmentTraversalAcceptor forwardTraversal = new FragmentTraversalAcceptor(FragmentType.FORWARD, edge.toString(), edgeTarget);
            doDFS(forwardTraversal, edgeTarget, Sets.newHashSet(edgeTarget));

            final FragmentTraversalAcceptor reverseTraversal = new FragmentTraversalAcceptor(FragmentType.REVERSE, edge.toString(), getRoot());
            doDFS(reverseTraversal, getRoot(), Sets.newHashSet(edgeTarget));
            fragments.add(new Tuple2<>(forwardTraversal.getFragment(), reverseTraversal.getFragment()));
        }

        return fragments;
    }

    public Stream<SugarStructureFragment> fragmentStream() {

        final Stream.Builder<SugarStructureFragment> streamBuilder = Stream.builder();

        final FragmentTraversalAcceptor intactTraversal = new FragmentTraversalAcceptor(FragmentType.INTACT, getLabel(), getRoot());
        doDFS(intactTraversal, getRoot(), Sets.newHashSet());
        streamBuilder.add(intactTraversal.getFragment());
        for(StructureLinkage edge : edgeSet()) {

            final SugarVertex edgeTarget = getEdgeTarget(edge);

            final FragmentTraversalAcceptor forwardTraversal = new FragmentTraversalAcceptor(FragmentType.FORWARD, edge.toString(), edgeTarget);
            doDFS(forwardTraversal, edgeTarget, Sets.newHashSet(edgeTarget));

            final FragmentTraversalAcceptor reverseTraversal = new FragmentTraversalAcceptor(FragmentType.REVERSE, edge.toString(), getRoot());
            doDFS(reverseTraversal, getRoot(), Sets.newHashSet(edgeTarget));
            streamBuilder.add(forwardTraversal.getFragment());
            streamBuilder.add(reverseTraversal.getFragment());
        }

        return streamBuilder.build();
    }

    public SugarStructure setRoot(String label, SugarVertex vertex) {

        SugarStructure structure = new SugarStructure(label, vertex);

        Set<SugarVertex> visited = new HashSet<>();
        visited.add(vertex);
        Queue<SugarVertex> vertexQueue = new ArrayDeque<>();
        vertexQueue.add(vertex);

        while (!vertexQueue.isEmpty()) {

            SugarVertex currentParent = vertexQueue.poll();
            List<SugarVertex> neighbours = Graphs.neighborListOf(this, currentParent);
            for(SugarVertex child : neighbours) {

                if(visited.add(child)) {

                    vertexQueue.add(child);
                    structure.doAdd(child);
                    structure.doAddEdge(currentParent, child);
                }
            }
        }

        return structure;
    }

    public static final class Builder extends AbstractBuilder<Builder, SugarStructure> {

        public Builder(String label, SugarUnit root) {

            super(root, (rootVertex) -> new SugarStructure(label, rootVertex));
        }

        @Override
        protected Builder thisReference() {

            return this;
        }

        public SugarStructure build() {

            return doBuild();
        }
    }

    @Override
    public String toString() {

        return label + " : (" +
                vertexSet() +
                ", " +
                edgeSet().stream().map(edge -> "{" + getEdgeSource(edge) + ", " + getEdgeTarget(edge) + (edge.isDefined() ? " : " + edge.toString() : "") + "}").collect(Collectors.toList()) +
                ')';
    }

    private static class MonosaccharideConverter {

        private final Glycan glycan;

        private final Map<Monosaccharide, Integer> monosaccharideMap = new IdentityHashMap<>();
        private final Map<Integer, SugarVertex> sugarMap = new HashMap<>();
        private final String nAcetyl = "NAcetyl";
        private final String nGlycolyl = "NGlycolyl";
        private final String sulfate = "Sulfate";
        private final Set<String> knownSubstituents = Sets.newHashSet(sulfate, nAcetyl, nGlycolyl);

        private int idCounter = 0;

        private MonosaccharideConverter(Glycan glycan) {

            this.glycan = glycan;
        }

        public SugarVertex convert(Monosaccharide monosaccharide) {

            if (!monosaccharideMap.containsKey(monosaccharide)) {

                SugarUnit sugarUnit;
                final NodeSet<Substituent> substituents = glycan.getSubstituentChildren(monosaccharide);
                switch (monosaccharide.getMonosaccharideClass()) {

                    case Hex:

                        long nacCount = substituents.stream().filter(s -> nAcetyl.equals(s.getName())).count();
                        if (nacCount == 0) {

                            sugarUnit = SugarUnit.Hex;
                        } else if (nacCount == 1) {

                            sugarUnit = SugarUnit.HexNAc;
                        } else {

                            throw new IllegalStateException("Have to many NAcetyl set: " + substituents.stream().map(GlycanNode::getName).collect(Collectors.toList()));
                        }
                        break;
                    case dHex:
                        sugarUnit = SugarUnit.Fuc;
                        break;
                    case Kdn:

                        if (substituents.stream().filter(s -> nAcetyl.equals(s.getName())).count() == 1) {

                            sugarUnit = SugarUnit.Neu5Ac;
                        } else if (substituents.stream().filter(s -> nGlycolyl.equals(s.getName())).count() == 1) {

                            sugarUnit = SugarUnit.Neu5Gc;
                        } else {

                            throw new IllegalStateException("Cannot convert substituent set: " + substituents.stream().map(GlycanNode::getName).collect(Collectors.toList()));
                        }
                        break;
                    default:
                        throw new IllegalStateException("Cannot convert " + monosaccharide.getMonosaccharideClass());
                }

                final int targetId = idCounter++;
                SugarVertex sugar = new SugarVertex(sugarUnit, targetId);

                monosaccharideMap.put(monosaccharide, targetId);
                sugarMap.put(targetId, sugar);
            }

            return sugarMap.get(monosaccharideMap.get(monosaccharide));
        }

        public void addSubstituentsOf(Monosaccharide srcParent, AbstractSugarStructure sugarStructure) {

            final NodeSet<Substituent> substituents = glycan.getSubstituentChildren(srcParent);
            if(!substituents.stream().allMatch(s -> knownSubstituents.contains(s.getName())))
                throw new IllegalStateException("Cannot convert substituent set: " + substituents.stream().map(GlycanNode::getName).collect(Collectors.toList()));

            final SugarVertex destParent = sugarMap.get(monosaccharideMap.get(srcParent));
            substituents.stream()
                    .filter(s -> sulfate.equals(s.getName()))
                    .forEach(sulphate -> sugarStructure.doAdd(destParent, new SugarVertex(SugarUnit.S, idCounter++)));
        }
    }

    private class FragmentTraversalAcceptor implements TraversalAcceptor {

        private final SugarStructureFragment structureFragment;
        private int maxId = 0;

        public FragmentTraversalAcceptor(FragmentType fragmentType, String label, SugarVertex root) {

            this.structureFragment = new SugarStructureFragment(fragmentType, root);
            maxId = Math.max(maxId, root.getId());
        }

        @Override
        public void accept(Optional<SugarVertex> parent, SugarVertex child, Optional<StructureLinkage> linkage) {

            final SugarVertex source = parent.get();
            final StructureLinkage edge1 = getEdge(source, child);
            structureFragment.doAdd(child);
            maxId = Math.max(maxId, child.getId());
            structureFragment.doAddEdge(source, child, edge1);
        }

        public SugarStructureFragment getFragment() {

            return structureFragment;
        }
    }
}
