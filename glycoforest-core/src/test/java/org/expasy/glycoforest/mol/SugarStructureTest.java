package org.expasy.glycoforest.mol;

import org.expasy.mzjava.core.mol.Composition;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.FragmentType;
import org.expasy.mzjava.core.ms.spectrum.IonType;
import org.expasy.mzjava.glycomics.io.mol.glycoct.GlycoCTReader;
import org.expasy.mzjava.glycomics.mol.Anomericity;
import org.expasy.mzjava.glycomics.mol.Glycan;
import org.expasy.mzjava.glycomics.ms.fragment.GlycanFragmenter;
import org.expasy.mzjava.glycomics.ms.spectrum.GlycanSpectrum;
import org.junit.Assert;
import org.junit.Test;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

import static org.expasy.glycoforest.mol.SugarUnit.*;
import static org.mockito.Mockito.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarStructureTest {

    @Test
    public void testBuilder() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc)
                .pop()
                .add(Hex).add(HexNAc).add(Hex)
                .build();

        final List<SugarVertex> sugars = new ArrayList<>(graph.vertexSet());
        Collections.sort(sugars, (v1, v2) -> Integer.compare(v1.getId(), v2.getId()));

        Assert.assertEquals(5, sugars.size());
        Assert.assertEquals(SugarUnit.HexNAc, sugars.get(0).getUnit());
        Assert.assertEquals(SugarUnit.HexNAc, sugars.get(1).getUnit());
        Assert.assertEquals(SugarUnit.Hex, sugars.get(2).getUnit());
        Assert.assertEquals(SugarUnit.HexNAc, sugars.get(3).getUnit());
        Assert.assertEquals(SugarUnit.Hex, sugars.get(4).getUnit());

        Assert.assertEquals(4, graph.edgeSet().size());
        Assert.assertEquals(true, graph.containsEdge(sugars.get(0), sugars.get(1)));
        Assert.assertEquals(true, graph.containsEdge(sugars.get(0), sugars.get(2)));
        Assert.assertEquals(true, graph.containsEdge(sugars.get(2), sugars.get(3)));
        Assert.assertEquals(true, graph.containsEdge(sugars.get(3), sugars.get(4)));
    }

    @Test
    public void testFromGlycan() throws Exception {

        final String glycoct = "RES\n" +
                "1b:o-dgal-HEX-0:0|1:aldi\n" +
                "2s:n-acetyl\n" +
                "3b:b-dgal-HEX-1:5\n" +
                "4b:a-dglc-HEX-1:5\n" +
                "5s:n-acetyl\n" +
                "6b:b-dgal-HEX-1:5\n" +
                "7b:b-dglc-HEX-1:5\n" +
                "8s:n-acetyl\n" +
                "LIN\n" +
                "1:1d(2+1)2n\n" +
                "2:1o(3+1)3d\n" +
                "3:3o(4+1)4d\n" +
                "4:4d(2+1)5n\n" +
                "5:4o(4+1)6d\n" +
                "6:1o(6+1)7d\n" +
                "7:7d(2+1)8n";

        final SugarStructure graph = SugarStructure.fromGlycan(new GlycoCTReader().read(glycoct, "id"));
        Assert.assertEquals(true,
                graph.isIsomorphic(
                        new SugarStructure.Builder("", HexNAc)
                                .branch().add(HexNAc)
                                .pop()
                                .add(Hex).add(HexNAc).add(Hex)
                                .build(),
                        IsomorphismType.TOPOLOGY
                )
        );
    }

    @Test
    public void testFromGlycanNeu5Ac() throws Exception {

        final String glycoct = "RES\n" +
                "1b:o-dgal-HEX-0:0|1:aldi\n" +
                "2s:n-acetyl\n" +
                "3b:a-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "4s:n-acetyl\n" +
                "LIN\n" +
                "1:1d(2+1)2n\n" +
                "2:1o(6+2)3d\n" +
                "3:3d(5+1)4n";

        final SugarStructure graph = SugarStructure.fromGlycan(new GlycoCTReader().read(glycoct, "id"));
        Assert.assertEquals(true,
                graph.isIsomorphic(
                        new SugarStructure.Builder("", HexNAc).add(Neu5Ac).build(),
                        IsomorphismType.TOPOLOGY
                )
        );
    }

    @Test
    public void testFromGlycanNeu5Gc() throws Exception {

        final String glycoct = "RES\n" +
                "1b:o-dgal-HEX-0:0|1:aldi\n" +
                "2s:n-acetyl\n" +
                "3b:a-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "4s:n-glycolyl\n" +
                "LIN\n" +
                "1:1d(2+1)2n\n" +
                "2:1o(6+2)3d\n" +
                "3:3d(5+1)4n";

        final SugarStructure graph = SugarStructure.fromGlycan(new GlycoCTReader().read(glycoct, "id"));
        Assert.assertEquals(true,
                graph.isIsomorphic(
                        new SugarStructure.Builder("", HexNAc).add(Neu5Gc).build(),
                        IsomorphismType.TOPOLOGY
                )
        );
    }

    @Test
    public void testFromGlycanSulfate() throws Exception {

        final String glycoct = "RES\n" +
                "1b:o-dgal-HEX-0:0|1:aldi\n" +
                "2s:n-acetyl\n" +
                "3b:b-dgal-HEX-1:5\n" +
                "4b:b-dglc-HEX-1:5\n" +
                "5s:n-acetyl\n" +
                "6s:sulfate\n" +
                "LIN\n" +
                "1:1d(2+1)2n\n" +
                "2:1o(3+1)3d\n" +
                "3:1o(6+1)4d\n" +
                "4:4d(2+1)5n\n" +
                "5:4o(6+1)6n";

        final SugarStructure graph = SugarStructure.fromGlycan(new GlycoCTReader().read(glycoct, "id"));
        Assert.assertEquals(true,
                graph.isIsomorphic(
                        new SugarStructure.Builder("", HexNAc)
                                .branch().add(HexNAc).add(S)
                                .pop()
                                .add(Hex)
                                .build(),
                        IsomorphismType.TOPOLOGY
                )
        );
    }

    @Test
    public void testIsIsomorphic() throws Exception {

        final SugarStructure graph1 = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc).add(Hex)
                .pop()
                .add(Hex).add(HexNAc).add(Hex)
                .build();

        final SugarStructure graph2 = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(Hex)
                .pop()
                .add(HexNAc).add(Hex).add(HexNAc).add(Hex)
                .build();

        Assert.assertEquals(true, graph1.isIsomorphic(graph2, IsomorphismType.TOPOLOGY));

        final SugarStructure graph3 = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(Hex).add(Hex).add(Hex)
                .pop()
                .add(HexNAc).add(Hex)
                .build();

        Assert.assertEquals(false, graph1.isIsomorphic(graph3, IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testIsIsomorphic2() throws Exception {

        final SugarStructure structure1 = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc).add(HexNAc)
                .pop().add(Hex).add(HexNAc)
                .build();

        final SugarStructure structure2 = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc).add(HexNAc)
                .pop().add(Hex).add(HexNAc)
                .build();

        Assert.assertEquals(true, structure1.isIsomorphic(structure2, IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, structure2.isIsomorphic(structure1, IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testIsIsomorphicWithConnectivity() throws Exception {

        final SugarStructure structure1 = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc, Anomericity.alpha, 1, 3).add(HexNAc)
                .pop().add(Hex).add(HexNAc)
                .build();

        final SugarStructure structure2 = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc, Anomericity.alpha, 1, 3).add(HexNAc)
                .pop().add(Hex).add(HexNAc)
                .build();

        final SugarStructure structureDiff = new SugarStructure.Builder("", HexNAc)
                .branch().add(HexNAc, Anomericity.beta, 1, 3).add(HexNAc)
                .pop().add(Hex).add(HexNAc)
                .build();

        Assert.assertEquals(true, structure1.isIsomorphic(structure2, IsomorphismType.LINKAGE));
        Assert.assertEquals(true, structure2.isIsomorphic(structure1, IsomorphismType.LINKAGE));

        Assert.assertEquals(false, structure2.isIsomorphic(structureDiff, IsomorphismType.LINKAGE));
        Assert.assertEquals(false, structureDiff.isIsomorphic(structure2, IsomorphismType.LINKAGE));

        Assert.assertEquals(true, structure2.isIsomorphic(structureDiff, IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, structureDiff.isIsomorphic(structure2, IsomorphismType.TOPOLOGY));
    }

    /**
     * <pre>
     *          HexNac
     *                \
     *                 HexNAc
     *               /
     * Hex-HexNAc-Hex
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testDfsFromRoot() throws Exception {

        final SugarStructure.Builder builder = new SugarStructure.Builder("", HexNAc);
        final SugarVertex node0 = builder.getHead();
        builder.branch();
        builder.add(HexNAc);
        final SugarVertex node1 = builder.getHead();
        builder.pop();
        builder.add(Hex);
        final SugarVertex node2 = builder.getHead();
        builder.add(HexNAc);
        final SugarVertex node3 = builder.getHead();
        builder.add(Hex);
        final SugarVertex node4 = builder.getHead();

        final SugarStructure graph = builder.build();
        TraversalAcceptor acceptor = mock(TraversalAcceptor.class);
        graph.dfs(acceptor);

        verify(acceptor).accept(Optional.empty(), node0, Optional.empty());
        verify(acceptor).accept(Optional.of(node0), node1, Optional.of(graph.getEdge(node0, node1)));
        verify(acceptor).accept(Optional.of(node0), node2, Optional.of(graph.getEdge(node0, node2)));
        verify(acceptor).accept(Optional.of(node2), node3, Optional.of(graph.getEdge(node2, node3)));
        verify(acceptor).accept(Optional.of(node3), node4, Optional.of(graph.getEdge(node3, node4)));
        verifyNoMoreInteractions(acceptor);
    }

    /**
     * <pre>
     *          HexNac
     *                \
     *                 HexNAc
     *               /
     * Hex-HexNAc-Hex
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testDfsFromLeaf() throws Exception {

        final SugarStructure.Builder builder = new SugarStructure.Builder("", HexNAc);
        final SugarVertex node0 = builder.getHead();
        builder.branch();
        builder.add(HexNAc);
        final SugarVertex node1 = builder.getHead();
        builder.pop();
        builder.add(Hex);
        final SugarVertex node2 = builder.getHead();
        builder.add(HexNAc);
        final SugarVertex node3 = builder.getHead();
        builder.add(Hex);
        final SugarVertex node4 = builder.getHead();

        final SugarStructure graph = builder.build();
        TraversalAcceptor acceptor = mock(TraversalAcceptor.class);
        graph.dfs(node1, acceptor);

        verify(acceptor).accept(Optional.empty(), node1, Optional.empty());
        verifyNoMoreInteractions(acceptor);
    }

    @Test
    public void testSetRoot() throws Exception {

        final SugarStructure.Builder builder = new SugarStructure.Builder("test", HexNAc);
        builder.branch().add(Hex).add(HexNAc);
        SugarVertex hexNAc2 = builder.getHead();
        builder.pop().add(HexNAc);

        SugarStructure structure = builder.build();

        final SugarStructure newStructure = structure.setRoot("new root", hexNAc2);
        Assert.assertEquals(true, newStructure.isIsomorphic(
                new SugarStructure.Builder("expected", HexNAc).add(Hex).add(HexNAc).add(HexNAc)
                        .build()
                , IsomorphismType.ROOTED_LINKAGE
        ));
    }

    /**
     * Extending
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * with Hex-Fuc
     *
     * @throws Exception
     */
    @Test
    public void testExtend() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        List<SugarStructure> extensions = graph.extend("", new SugarStructure.Builder("", Hex).add(Fuc).build(), (v, g) -> true);

        Assert.assertEquals(3, extensions.size());
        Assert.assertEquals(true, extensions.get(0).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .branch()
                        .add(Hex)
                        .pop()
                        .add(Hex).add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
        Assert.assertEquals(true, extensions.get(1).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc).add(Hex).add(Fuc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
        Assert.assertEquals(true, extensions.get(2).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Hex).add(Hex).add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    /**
     * Extending
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * with Hex-Fuc but only for nodes that have a degree == 1
     *
     * @throws Exception
     */
    @Test
    public void testExtend2() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        List<SugarStructure> extensions = graph.extend("", new SugarStructure.Builder("", Hex).add(Fuc).build(), (v, g) -> g.degreeOf(v) == 1);

        Assert.assertEquals(2, extensions.size());
        Assert.assertEquals(true, extensions.get(0).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc).add(Hex).add(Fuc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
        Assert.assertEquals(true, extensions.get(1).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Hex).add(Hex).add(Fuc)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    /**
     * Inserting
     * <pre>
     *
     *     Fuc
     *      |
     *    -Hex-
     *
     * </pre>
     * into
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * expecting
     * <pre>
     *        Fuc
     *         |
     * HexNAc-Hex
     *           \
     *           HexNAc
     *          /
     *       Hex
     *
     * </pre>
     * and
     * <pre>
     *
     *  HexNAc
     *        \
     *         HexNAc
     *        /
     * Hex-Hex
     *      |
     *     Fuc
     *
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testInsert() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        final SugarStructure insertion = new SugarStructure.Builder("", Hex).add(Fuc).build();
        List<SugarStructure> extensions = graph.insert("", insertion, insertion.getRoot(), (p, c, g) -> true);

        Assert.assertEquals(2, extensions.size());
        Assert.assertEquals(true, extensions.get(0).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(Hex).branch().add(Fuc)
                        .pop()
                        .add(HexNAc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
        Assert.assertEquals(true, extensions.get(1).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Hex).branch().add(Fuc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    /**
     * Inserting
     * <pre>
     *   out   int
     * -HexNAc-Hex-
     *
     * </pre>
     * into
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * expecting
     * <pre>
     *
     * HexNAc-HexNAc-Hex
     *                  \
     *                  HexNAc
     *                 /
     *              Hex
     *
     * </pre>
     * and
     * <pre>
     *
     *         HexNAc
     *               \
     *                HexNAc
     *               /
     * Hex-HexNAc-Hex
     *
     *
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testInsert2() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        final SugarStructure.Builder builder = new SugarStructure.Builder("", Hex).add(HexNAc);

        List<SugarStructure> extensions = graph.insert("", builder.build(), builder.getHead(), (p, c, g) -> true);

        Assert.assertEquals(2, extensions.size());
        Assert.assertEquals(true, extensions.get(0).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(Hex).add(HexNAc).add(HexNAc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
        Assert.assertEquals(true, extensions.get(1).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Hex).add(HexNAc).add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    /**
     * Inserting
     * <pre>
     *   out   int
     * -HexNAc-Hex-
     *
     * </pre>
     * into any edge that has a Hex as child
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * expecting
     * <pre>
     *
     *         HexNAc
     *               \
     *                HexNAc
     *               /
     * Hex-HexNAc-Hex
     *
     *
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testInsert2_WithPredicate() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        final SugarStructure.Builder builder = new SugarStructure.Builder("", Hex).add(HexNAc);

        List<SugarStructure> extensions = graph.insert("", builder.build(), builder.getHead(), (p, c, g) -> Hex.equals(c.getUnit()));

        Assert.assertEquals(1, extensions.size());
        Assert.assertEquals(true, extensions.get(0).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Hex).add(HexNAc).add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    /**
     * Mutating the Hex in
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * to Neu5Ac, expecting
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     * Neu5Ac
     *
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testMutate() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        final List<SugarStructure> mutations = graph.substitute("", Neu5Ac, (n, g) -> Hex.equals(n.getUnit()));
        Assert.assertEquals(1, mutations.size());

        Assert.assertEquals(true, mutations.get(0).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Neu5Ac)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    /**
     * Mutating the Hex in
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * to Neu5Ac, expecting
     * <pre>
     *
     * HexNAc
     *       \
     *        Neu5Ac
     *       /
     *   Hex
     *
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testMutateRoot() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        final List<SugarStructure> mutations = graph.substitute("", Neu5Ac, (v, g) -> g.getRoot().equals(v));
        Assert.assertEquals(1, mutations.size());

        Assert.assertEquals(true, mutations.get(0).isIsomorphic(
                new SugarStructure.Builder("", Neu5Ac)
                        .branch()
                        .add(HexNAc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    /**
     * Mutating the leaf HexNac in
     * <pre>
     *
     * HexNAc
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     * to
     * <pre>
     *
     *     Fuc
     *      |
     *     Hex
     *
     * </pre>
     * expecting
     * <pre>
     *
     *    Fuc
     *     |
     *    Hex
     *       \
     *        HexNAc
     *       /
     *    Hex
     *
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testMutate2() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex)
                .build();

        final List<SugarStructure> mutations = graph.substitute("",
                new SugarStructure.Builder("", Hex).add(Fuc).build(),
                (n, g) -> HexNAc.equals(n.getUnit()) && g.degreeOf(n) == 1
        );
        Assert.assertEquals(1, mutations.size());

        Assert.assertEquals(true, mutations.get(0).isIsomorphic(
                new SugarStructure.Builder("", HexNAc)
                        .branch()
                        .add(Hex).add(Fuc)
                        .pop()
                        .add(Hex)
                        .build(),
                IsomorphismType.TOPOLOGY
                )
        );
    }

    @Test
    public void testMutate3() throws Exception {

        final SugarStructure graph = new SugarStructure.Builder("", Hex).add(HexNAc).build();

        final List<SugarStructure> mutations = graph.substitute("",
                new SugarStructure.Builder("", HexNAc).build(),
                (n, g) -> Hex.equals(n.getUnit())
        );
        Assert.assertEquals(1, mutations.size());

        final SugarStructure sugarStructure = mutations.get(0);
        Assert.assertEquals(true, sugarStructure.isIsomorphic(
                new SugarStructure.Builder("", HexNAc).add(HexNAc).build(), IsomorphismType.TOPOLOGY
                )
        );

        Assert.assertEquals(true, sugarStructure.vertexSet().contains(sugarStructure.getRoot()));
    }

    @Test
    public void testOutDegreeOf() throws Exception {

        final SugarStructure.Builder builder = new SugarStructure.Builder("", HexNAc);
        SugarVertex root = builder.getHead();
        builder.branch()
                .add(HexNAc);
        SugarVertex leaf1 = builder.getHead();
        builder.pop()
                .add(Hex);

        final SugarStructure graph = builder.build();

        Assert.assertEquals(0, graph.inDegreeOf(root));
        Assert.assertEquals(2, graph.outDegreeOf(root));
        Assert.assertEquals(1, graph.inDegreeOf(leaf1));
        Assert.assertEquals(0, graph.outDegreeOf(leaf1));
    }

    @Test
    public void testHasSubGraph() throws Exception {

        SugarStructure smallGraph = new SugarStructure.Builder("958-4", HexNAc)
                .branch()
                .add(HexNAc).add(Hex)
                .pop()
                .add(Hex).add(HexNAc)
                .build();

        SugarStructure largeGraph1 = new SugarStructure.Builder("1301-4", HexNAc)
                .branch()
                .add(HexNAc)
                .pop()
                .add(Hex).add(HexNAc).add(HexNAc)
                .branch()
                .add(Hex)
                .pop()
                .add(Fuc)
                .build();

        SugarStructure largeGraph2 = new SugarStructure.Builder("1301-8", HexNAc)
                .branch()
                .add(HexNAc).add(Hex).add(HexNAc)
                .pop()
                .add(Hex)
                .branch()
                .add(HexNAc)
                .add(Fuc)
                .build();

        Assert.assertEquals(true, largeGraph1.hasSubGraph(smallGraph));
        Assert.assertEquals(false, largeGraph1.hasRootedSubGraph(smallGraph));
        Assert.assertEquals(true, largeGraph2.hasSubGraph(smallGraph));
        Assert.assertEquals(true, largeGraph2.hasRootedSubGraph(smallGraph));
    }

    /**
     * Removing HexNac from
     * <pre>
     *
     *         HexNAc
     *               \
     *                HexNAc
     *               /
     *    Hex-HexNac
     * </pre>
     * <p>
     * expecting:
     * <pre>
     *
     *                HexNAc
     *               /
     *    Hex-HexNac
     *
     * and
     *
     *         HexNAc
     *               \
     *                HexNAc
     *               /
     *           Hex
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testRemove() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("Start", HexNAc)
                .branch().add(HexNAc)
                .pop().add(HexNAc).add(Hex)
                .build();

        final List<SugarStructure> removed = structure.remove("Removed", (v, g) -> v.isUnit(HexNAc));

        Assert.assertEquals(2, removed.size());

        Assert.assertEquals(true, removed.get(0).isIsomorphic(
                new SugarStructure.Builder("Test", HexNAc).add(HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY
        ));
        Assert.assertEquals(true, removed.get(1).isIsomorphic(
                new SugarStructure.Builder("Test", HexNAc)
                        .branch().add(HexNAc)
                        .pop().add(Hex).build(),
                IsomorphismType.TOPOLOGY
        ));
    }

    /**
     * Removing HexNac from
     * <pre>
     *
     *         HexNAc
     *               \
     *                HexNAc
     *               /
     *    Hex-HexNac
     * </pre>
     * <p>
     * expecting: []
     *
     * @throws Exception
     */
    @Test
    public void testRemoveNotPresent() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("Start", HexNAc)
                .branch().add(HexNAc)
                .pop().add(HexNAc).add(Hex)
                .build();

        final List<SugarStructure> removed = structure.remove("Removed", (v, g) -> v.isUnit(Neu5Ac));

        Assert.assertEquals(0, removed.size());
    }

    /**
     * Removing Neu5Ac fom
     * <pre>
     *
     *     Neu5Ac-HexNAc
     * </pre>
     * <p>
     * expecting:
     * <pre>
     *
     *     HexNAc
     * </pre>
     *
     * @throws Exception
     */
    @Test
    public void testRemove2() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("Start", HexNAc).add(Neu5Ac).build();

        final List<SugarStructure> removed = structure.remove("Removed", (v, g) -> v.isUnit(Neu5Ac));

        Assert.assertEquals(1, removed.size());

        Assert.assertEquals(true, removed.get(0).isIsomorphic(new SugarStructure.Builder("Test", HexNAc).build(), IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testCopyConstructor() throws Exception {

        SugarStructure src = new SugarStructure.Builder("Src", HexNAc).build();
        SugarStructure copy = new SugarStructure("Copy", src);
        Assert.assertEquals(true, copy.isIsomorphic(src, IsomorphismType.TOPOLOGY));
    }

    @Test
    public void testToGlycan() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Neu5Ac).build();

        final Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "3s:n-acetyl\n" +
                "4s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+2)2d\n" +
                "2:2d(5+1)3n\n" +
                "3:1d(2+1)4n", "test");

        final Glycan glycan = structure.toGlycan(Composition.parseComposition("H2"));

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanWithS() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(S).build();

        final Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2s:sulfate\n" +
                "3s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2n\n" +
                "2:1d(2+1)3n", "test");

        final Glycan glycan = structure.toGlycan(Composition.parseComposition("H2"));

        Assert.assertEquals(expected, glycan);
    }

    @Test
    public void testToGlycanWithNeu5Ac() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).branch().add(Hex)
                .pop().add(Neu5Ac)
                .build();

        final Glycan expected = new GlycoCTReader().read("RES\n" +
                "1b:o-HEX-0:0|1:aldi\n" +
                "2b:x-HEX-1:5\n" +
                "3b:x-dgro-dgal-NON-2:6|1:a|2:keto|3:d\n" +
                "4s:n-acetyl\n" +
                "5s:n-acetyl\n" +
                "LIN\n" +
                "1:1o(-1+1)2d\n" +
                "2:1o(-1+2)3d\n" +
                "3:3d(5+1)4n\n" +
                "4:1d(2+1)5n", "test");

        final Glycan glycan = structure.toGlycan(Composition.parseComposition("H2"));

        Assert.assertEquals(expected, glycan);


        final GlycanFragmenter fragmenter = new GlycanFragmenter(EnumSet.of(IonType.b, IonType.y), true, false, PeakList.Precision.FLOAT, 1, 0);
        final GlycanSpectrum spectrum = fragmenter.fragment(glycan, 1);
        Assert.assertEquals(5, spectrum.size());
        Assert.assertEquals(161.05, spectrum.getMz(0), 0.01);
        Assert.assertEquals(290.09, spectrum.getMz(1), 0.01);
        Assert.assertEquals(384.15, spectrum.getMz(2), 0.01);
        Assert.assertEquals(513.19, spectrum.getMz(3), 0.01);
        Assert.assertEquals(675.25, spectrum.getMz(4), 0.01);
    }

    @Test
    public void testFragment749() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc)
                .branch().add(HexNAc).add(Hex)
                .pop().add(Hex)
                .build();

        final List<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragments = structure.fragmentPairs();

        Assert.assertEquals(3, fragments.size());
        Assert.assertEquals(true, fragments.get(0)._1().isIsomorphic(new SugarStructure.Builder("FORWARD (HexNAc 0 : HexNAc 1) : ([HexNAc 1, Hex 2]", HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, fragments.get(0)._2().isIsomorphic(new SugarStructure.Builder("REVERSE (HexNAc 0 : HexNAc 1)", HexNAc).add(Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, fragments.get(1)._1().isIsomorphic(new SugarStructure.Builder("FORWARD (HexNAc 1 : Hex 2)", Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, fragments.get(1)._2().isIsomorphic(new SugarStructure.Builder("REVERSE (HexNAc 1 : Hex 2)", HexNAc)
                .branch().add(HexNAc)
                .pop().add(Hex).build(), IsomorphismType.TOPOLOGY)
        );
        Assert.assertEquals(true, fragments.get(2)._1().isIsomorphic(new SugarStructure.Builder("FORWARD (HexNAc 0 : Hex 3)", Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(true, fragments.get(2)._2().isIsomorphic(new SugarStructure.Builder("REVERSE (HexNAc 0 : Hex 3)", HexNAc)
                        .branch().add(HexNAc)
                        .pop().add(Hex).build(), IsomorphismType.TOPOLOGY
                )
        );

        Assert.assertEquals(FragmentType.FORWARD, fragments.get(0)._1().getFragmentType());
        Assert.assertEquals(FragmentType.FORWARD, fragments.get(1)._1().getFragmentType());
        Assert.assertEquals(FragmentType.FORWARD, fragments.get(2)._1().getFragmentType());

        Assert.assertEquals(FragmentType.REVERSE, fragments.get(0)._2().getFragmentType());
        Assert.assertEquals(FragmentType.REVERSE, fragments.get(1)._2().getFragmentType());
        Assert.assertEquals(FragmentType.REVERSE, fragments.get(2)._2().getFragmentType());
    }

    @Test
    public void testFragment384() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex)
                .build();

        List<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragments = structure.fragmentPairs();

        Assert.assertEquals(1, fragments.size());
        final Tuple2<SugarStructureFragment, SugarStructureFragment> tuple = fragments.get(0);
        Assert.assertEquals(true, tuple._1().isIsomorphic(new SugarStructure.Builder("FORWARD (HexNAc 0 : Hex 1)", Hex).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(FragmentType.FORWARD, tuple._1().getFragmentType());
        Assert.assertEquals(true, tuple._2().isIsomorphic(new SugarStructure.Builder("REVERSE (HexNAc 0 : Hex 1)", HexNAc).build(), IsomorphismType.TOPOLOGY));
        Assert.assertEquals(FragmentType.REVERSE, tuple._2().getFragmentType());
    }

    @Test
    public void testFragment222() throws Exception {

        final SugarStructure structure = new SugarStructure.Builder("test", HexNAc)
                .build();

        final List<Tuple2<SugarStructureFragment, SugarStructureFragment>> fragments = structure.fragmentPairs();
        Assert.assertEquals(0, fragments.size());
    }

    @Test
    public void testFragmentStream() throws Exception {

        SugarStructure structure = new SugarStructure.Builder("test", HexNAc).add(Hex).build();

        List<SugarStructureFragment> fragments = structure.fragmentStream().collect(Collectors.toList());

        Assert.assertEquals(3, fragments.size());
    }

    @Test
    public void testIsSubGraph() throws Exception {

        final SugarStructure graph1 = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(HexNAc).add(Hex)
                .pop()
                .add(Hex).add(HexNAc).add(Hex)
                .build();

        final SugarStructure graph2 = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(Hex)
                .pop()
                .add(HexNAc)
                .build();

        Assert.assertEquals(true, graph1.isSubGraph(graph2, IsomorphismType.ROOTED_TOPOLOGY));

        final SugarStructure graph3 = new SugarStructure.Builder("", HexNAc)
                .branch()
                .add(Hex).add(Hex).add(Hex)
                .pop()
                .add(HexNAc).add(Hex)
                .build();

        Assert.assertEquals(false, graph1.isIsomorphic(graph3, IsomorphismType.TOPOLOGY));
    }
}