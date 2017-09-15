package org.expasy.glycoforest.mol;

import org.expasy.mzjava.glycomics.mol.Anomericity;
import org.jgrapht.Graphs;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class AbstractSugarStructure extends SimpleGraph<SugarVertex, StructureLinkage> {

    private SugarVertex root;
    private int idCounter = 1;

    protected AbstractSugarStructure(SugarVertex root) {

        super((v1, v2) -> new StructureLinkage());
        this.root = root;
        super.addVertex(root);
    }

    protected AbstractSugarStructure(AbstractSugarStructure src) {

        super((v1, v2) -> new StructureLinkage());
        this.root = src.root;
        this.idCounter = src.idCounter;

        src.vertexSet().forEach(super::addVertex);
        src.edgeSet().forEach(edge -> super.addEdge(src.getEdgeSource(edge), src.getEdgeTarget(edge), edge));
    }

    @Override
    public StructureLinkage addEdge(SugarVertex sourceVertex, SugarVertex targetVertex) {

        throw new UnsupportedOperationException("Use the builder instead");
    }

    @Override
    public boolean addEdge(SugarVertex sourceVertex, SugarVertex targetVertex, StructureLinkage monomerEdge) {

        throw new UnsupportedOperationException("Use the builder instead");
    }

    @Override
    public boolean addVertex(SugarVertex sugar) {

        throw new UnsupportedOperationException("Use the builder instead");
    }

    @Override
    public StructureLinkage removeEdge(SugarVertex sourceVertex, SugarVertex targetVertex) {

        throw new UnsupportedOperationException("MonomerGraph is immutable");
    }

    @Override
    public boolean removeEdge(StructureLinkage monomerEdge) {

        throw new UnsupportedOperationException("MonomerGraph is immutable");
    }

    @Override
    public boolean removeVertex(SugarVertex sugar) {

        throw new UnsupportedOperationException("MonomerGraph is immutable");
    }

    @Override
    public boolean removeAllEdges(Collection<? extends StructureLinkage> edges) {

        // We are allowing the removal of an empty edge collection because the replace()
        // method requires super.removeVertex() to be called. Which in turn calls this
        // method. replace() guarantees that there are no longer any edges attached to
        // the vertex being replaced.
        // Preserving immutability.
        if (!edges.isEmpty()) {
            throw new UnsupportedOperationException("MonomerGraph is immutable");
        } else return super.removeAllEdges(edges);
    }

    @Override
    public Set<StructureLinkage> removeAllEdges(SugarVertex sourceVertex, SugarVertex targetVertex) {

        throw new UnsupportedOperationException("MonomerGraph is immutable");
    }

    @Override
    public boolean removeAllVertices(Collection<? extends SugarVertex> vertices) {

        throw new UnsupportedOperationException("MonomerGraph is immutable");
    }

    @Override
    protected boolean removeAllEdges(StructureLinkage[] edges) {

        throw new UnsupportedOperationException("MonomerGraph is immutable");
    }

    @Override
    public Set<StructureLinkage> outgoingEdgesOf(SugarVertex vertex) {

        return edgesOf(vertex).stream().filter(e -> getEdgeSource(e).equals(vertex)).collect(Collectors.toSet());
    }

    @Override
    public int outDegreeOf(SugarVertex vertex) {

        return edgesOf(vertex).stream().mapToInt(e -> getEdgeSource(e).equals(vertex) ? 1 : 0).sum();
    }

    @Override
    public Set<StructureLinkage> incomingEdgesOf(SugarVertex vertex) {

        return edgesOf(vertex).stream().filter(e -> getEdgeTarget(e).equals(vertex)).collect(Collectors.toSet());
    }

    @Override
    public int inDegreeOf(SugarVertex vertex) {

        return edgesOf(vertex).stream().mapToInt(e -> getEdgeTarget(e).equals(vertex) ? 1 : 0).sum();
    }

    public SugarVertex oppositeVertex(StructureLinkage edge, SugarVertex vertex) {

        return Graphs.getOppositeVertex(this, edge, vertex);
    }

    public SugarVertex getRoot() {

        return root;
    }

    protected void doAdd(SugarVertex parent, SugarVertex child) {

        super.addVertex(child);
        super.addEdge(parent, child);
        idCounter = Math.max(idCounter, Math.max(parent.getId(), child.getId()) + 1);
    }

    protected SugarVertex doAdd(SugarVertex parent, SugarUnit childClass) {

        final SugarVertex child = new SugarVertex(childClass, idCounter++);
        super.addVertex(child);
        super.addEdge(parent, child);
        return child;
    }

    protected SugarVertex doAdd(SugarVertex parent, SugarUnit childClass, Optional<Anomericity> anomericity, Optional<Integer> anomericCarbon, Optional<Integer> linkedCarbon) {

        final SugarVertex child = new SugarVertex(childClass, idCounter++);
        super.addVertex(child);
        super.addEdge(parent, child, new StructureLinkage(anomericity, anomericCarbon, linkedCarbon));
        return child;
    }

    protected boolean doAdd(SugarVertex vertex) {

        return super.addVertex(vertex);
    }

    protected boolean doAddEdge(SugarVertex source, SugarVertex target, StructureLinkage edge) {

        checkNotNull(source);
        checkNotNull(target);
        checkNotNull(edge);

        return super.addEdge(source, target, edge);
    }

    protected boolean doRemove(SugarVertex vertex) {

        new ArrayList<>(edgesOf(vertex)).forEach(this::doRemove);
        return super.removeVertex(vertex);
    }

    protected boolean doRemove(StructureLinkage edge) {

        return super.removeEdge(edge);
    }

    protected void doAddEdge(SugarVertex parent, SugarVertex child) {

        super.addEdge(parent, child);
    }

    public void dfs(TraversalAcceptor traversalAcceptor) {

        traversalAcceptor.accept(Optional.<SugarVertex>empty(), root, Optional.empty());
        doDFS(traversalAcceptor, root, new HashSet<>());
    }

    public void dfs(SugarVertex start, TraversalAcceptor traversalAcceptor) {

        traversalAcceptor.accept(Optional.<SugarVertex>empty(), start, Optional.empty());
        doDFS(traversalAcceptor, start, new HashSet<>());
    }

    protected void doDFS(TraversalAcceptor traversalAcceptor, SugarVertex parent, Set<SugarVertex> visited) {

        final List<StructureLinkage> structureLinkages = new ArrayList<>(outgoingEdgesOf(parent));
        structureLinkages.sort(Comparator.comparing(l -> getEdgeTarget(l).getId()));
        for (StructureLinkage edge : structureLinkages) {

            final SugarVertex child = getEdgeTarget(edge);
            if (visited.add(child)) {

                traversalAcceptor.accept(Optional.of(parent), child, Optional.of(edge));
                doDFS(traversalAcceptor, child, visited);
            }
        }
    }

    public SugarComposition getComposition() {

        final SugarComposition.Builder builder = new SugarComposition.Builder();
        vertexSet().stream().forEach(sugar -> builder.add(sugar.getUnit()));
        return builder.build();
    }

    protected SugarVertex replace(SugarVertex node, SugarUnit newClass) {

        // need a copy of the edges otherwise will get a ConcurrentModificationException on removeEdge
        final List<StructureLinkage> touchingEdgesList = new ArrayList<>(edgesOf(node));
        final Set<SugarVertex> parents = touchingEdgesList.stream().map(this::getEdgeSource).filter(sugar -> !sugar.equals(node)).collect(Collectors.toSet());
        final Set<SugarVertex> children = touchingEdgesList.stream().map(this::getEdgeTarget).filter(sugar -> !sugar.equals(node)).collect(Collectors.toSet());

        touchingEdgesList.forEach(super::removeEdge);
        super.removeVertex(node);

        final SugarVertex replacement = new SugarVertex(newClass, node.getId());
        if(node == root)
            root = replacement;
        super.addVertex(replacement);
        parents.forEach(parent -> doAddEdge(parent, replacement));
        children.forEach(child -> doAddEdge(replacement, child));

        return replacement;
    }

    public boolean isIsomorphic(AbstractSugarStructure other, IsomorphismType type) {

        final VF2GraphIsomorphismInspector<SugarVertex, StructureLinkage> isomorphismInspector;
        switch (type) {

            case TOPOLOGY:

                isomorphismInspector = new VF2GraphIsomorphismInspector<>(this, other, new SugarEquivalenceComparator(), null, true);
                break;
            case ROOTED_TOPOLOGY:

                isomorphismInspector = new VF2GraphIsomorphismInspector<>(this, other, new RootedSugarEquivalenceComparator(getRoot(), other.getRoot()), null, true);
                break;
            case LINKAGE:

                isomorphismInspector = new VF2GraphIsomorphismInspector<>(this, other, new SugarEquivalenceComparator(), new LinkageEquivalenceComparator(), true);
                break;
            case ROOTED_LINKAGE:

                isomorphismInspector = new VF2GraphIsomorphismInspector<>(this, other, new RootedSugarEquivalenceComparator(getRoot(), other.getRoot()), new LinkageEquivalenceComparator(), true);
                break;
            default:
                throw new IllegalStateException("Cannot calculate isomorphism for " + type);
        }

        return isomorphismInspector.isomorphismExists();
    }

    public boolean isSubGraph(AbstractSugarStructure other, IsomorphismType type) {

        final VF2SubgraphIsomorphismInspector<SugarVertex, StructureLinkage> isomorphismInspector;
        switch (type) {

            case TOPOLOGY:

                isomorphismInspector = new VF2SubgraphIsomorphismInspector<>(this, other, new SugarEquivalenceComparator(), null, true);
                break;
            case ROOTED_TOPOLOGY:

                isomorphismInspector = new VF2SubgraphIsomorphismInspector<>(this, other, new RootedSugarEquivalenceComparator(getRoot(), other.getRoot()), null, true);
                break;
            case LINKAGE:

                isomorphismInspector = new VF2SubgraphIsomorphismInspector<>(this, other, new SugarEquivalenceComparator(), new LinkageEquivalenceComparator(), true);
                break;
            case ROOTED_LINKAGE:

                isomorphismInspector = new VF2SubgraphIsomorphismInspector<>(this, other, new RootedSugarEquivalenceComparator(getRoot(), other.getRoot()), new LinkageEquivalenceComparator(), true);
                break;
            default:
                throw new IllegalStateException("Cannot calculate isomorphism for " + type);
        }

        return isomorphismInspector.isomorphismExists();
    }

    public boolean hasSubGraph(AbstractSugarStructure subGraph) {

        return new VF2SubgraphIsomorphismInspector<>(this, subGraph, new SugarEquivalenceComparator(), null, false)
                .isomorphismExists();
    }

    public boolean hasRootedSubGraph(AbstractSugarStructure subGraph) {

        return new VF2SubgraphIsomorphismInspector<>(this, subGraph, new RootedSugarEquivalenceComparator(getRoot(), subGraph.getRoot()), null, false)
                .isomorphismExists();
    }

    public static abstract class AbstractBuilder<B, G extends AbstractSugarStructure> {

        protected AbstractSugarStructure graph;
        private final Deque<SugarVertex> stack = new ArrayDeque<>();
        private SugarVertex head;

        public AbstractBuilder(SugarUnit root, Function<SugarVertex, G> graphGenerator) {

            checkNotNull(root);

            head = new SugarVertex(root, 0);
            graph = graphGenerator.apply(head);
        }

        protected abstract B thisReference();

        public B add(SugarUnit sugar) {

            checkNotNull(sugar);
            checkState();

            head = graph.doAdd(head, sugar);
            return thisReference();
        }

        public B add(SugarUnit sugar, Anomericity anomericity, Integer anomericCarbon, Integer linkedCarbon) {

            return add(sugar, Optional.ofNullable(anomericity), Optional.ofNullable(anomericCarbon), Optional.ofNullable(linkedCarbon));
        }

        public B add(SugarUnit sugar, Optional<Anomericity> anomericity, Optional<Integer> anomericCarbon, Optional<Integer> linkedCarbon) {

            checkNotNull(sugar);
            checkState();

            head = graph.doAdd(head, sugar, anomericity, anomericCarbon, linkedCarbon);
            return thisReference();
        }

        /**
         * Create a branch at the current sugar. Pop can be used
         * to move back through the branch points.
         *
         * @return the builder
         */
        public B branch() {

            stack.add(head);
            return thisReference();
        }

        /**
         * Set the head of the builder to the last sugar that
         * was tagged as a branch point
         *
         * @return the builder
         */
        public B pop() {

            head = stack.removeLast();
            return thisReference();
        }

        private void checkState() {

            if (graph == null)
                throw new IllegalStateException("Builder cannot be reused");
        }

        public SugarVertex getHead() {

            return head;
        }

        public B addEdge(SugarVertex parent, SugarVertex child) {

            checkNotNull(parent);
            checkNotNull(child);
            checkState();

            graph.doAddEdge(parent, child);

            return thisReference();
        }

        protected G doBuild() {

            final AbstractSugarStructure tmpGraph = graph;
            graph = null;
            //noinspection unchecked
            return (G) tmpGraph;
        }
    }
}
