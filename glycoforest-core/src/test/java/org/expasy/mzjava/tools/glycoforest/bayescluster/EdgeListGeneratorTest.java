/**
 * Copyright (c) 2010, SIB. All rights reserved.
 *
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL SIB/GENEBIO BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.expasy.mzjava.tools.glycoforest.bayescluster;

import org.expasy.mzjava.core.ms.cluster.DenseSimilarityGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeListGeneratorTest {

    @Test
    public void testAddEdges() throws Exception {

        UUID nodeA1 = UUID.fromString("a1000000-0000-0000-0000-000000000000");
        UUID nodeA2 = UUID.fromString("a2000000-0000-0000-0000-000000000000");
        UUID nodeA3 = UUID.fromString("a3000000-0000-0000-0000-000000000000");

        UUID nodeB1 = UUID.fromString("b1000000-0000-0000-0000-000000000000");
        UUID nodeB2 = UUID.fromString("b2000000-0000-0000-0000-000000000000");

        List<UUID> nodes1 = Arrays.asList(nodeA1, nodeA2, nodeA3);
        List<UUID> nodes2 = Arrays.asList(nodeB1, nodeB2);

        DenseSimilarityGraph.Builder<UUID> edgeList = new DenseSimilarityGraph.Builder<>();
        EdgeListGenerator.addBetweenEdges(nodes1, nodes2, edgeList, 0.5, 0.01);

        Assert.assertEquals(6, edgeList.edgeCount());
    }

    @Test
    public void testAddEdges2() throws Exception {

        UUID nodeA1 = UUID.fromString("a1000000-0000-0000-0000-000000000000");
        UUID nodeA2 = UUID.fromString("a2000000-0000-0000-0000-000000000000");
        UUID nodeA3 = UUID.fromString("a3000000-0000-0000-0000-000000000000");
        UUID nodeA4 = UUID.fromString("a4000000-0000-0000-0000-000000000000");

        List<UUID> nodes = Arrays.asList(nodeA1, nodeA2, nodeA3, nodeA4);

        DenseSimilarityGraph.Builder<UUID> edgeList = new DenseSimilarityGraph.Builder<>();
        EdgeListGenerator.addWithinEdges(nodes, edgeList, 0.5, 0.01);

        Assert.assertEquals(6, edgeList.edgeCount());
    }
}
