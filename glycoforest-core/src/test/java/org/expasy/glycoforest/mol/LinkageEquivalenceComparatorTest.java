package org.expasy.glycoforest.mol;

import org.expasy.mzjava.glycomics.mol.Anomericity;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class LinkageEquivalenceComparatorTest {

    @Test
    public void testCompare() throws Exception {

        final LinkageEquivalenceComparator equivalenceComparator = new LinkageEquivalenceComparator();

        assertEquals(0, equivalenceComparator.compare(new StructureLinkage(), new StructureLinkage()));
        assertEquals(1, equivalenceComparator.compare(new StructureLinkage(Anomericity.alpha, null, null), new StructureLinkage()));
        assertEquals(-1, equivalenceComparator.compare(new StructureLinkage(), new StructureLinkage(Anomericity.alpha, null, null)));

        assertEquals(0, equivalenceComparator.compare(new StructureLinkage(Anomericity.alpha, null, null), new StructureLinkage(Anomericity.alpha, null, null)));
        assertEquals(1, equivalenceComparator.compare(new StructureLinkage(Anomericity.alpha, 1, null), new StructureLinkage(Anomericity.alpha, null, null)));
        assertEquals(-1, equivalenceComparator.compare(new StructureLinkage(Anomericity.alpha, null, null), new StructureLinkage(Anomericity.alpha, 1, null)));

        assertEquals(0, equivalenceComparator.compare(new StructureLinkage(Anomericity.alpha, 1, 6), new StructureLinkage(Anomericity.alpha, 1, 6)));
        assertEquals(1, equivalenceComparator.compare(new StructureLinkage(Anomericity.alpha, 1, 6), new StructureLinkage(Anomericity.alpha, 1, 5)));
    }
}