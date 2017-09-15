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
package org.expasy.mzjava.tools.glycoforest.data;

import org.expasy.mzjava.proteomics.mol.modification.Modification;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class CompositionDifferenceTest {

    final Modification dHex = Modification.parseModification("d-hex:C6O4H10"); //deoxyHexose (fuc)
    final Modification hex = Modification.parseModification("hex:C6O5H10"); //hex (galactose, glucose, mannose)
    final Modification hexNac = Modification.parseModification("hexNAc:C8O5N1H13"); //hexNAc (galNAc & glcNAc)
    final Modification neuAc = Modification.parseModification("neuAc:C11H17N1O8"); //neu5Ac

    @Test
    public void testGetMolecularMass() throws Exception {

        CompositionDifference compDiff = new CompositionDifference(dHex, hex);

        Assert.assertEquals(dHex.getMolecularMass() + hex.getMolecularMass(), compDiff.getMolecularMass(), 0.000000001);
    }

    @Test
    public void testSize() throws Exception {

        CompositionDifference compDiff = new CompositionDifference(dHex, hex, neuAc);

        Assert.assertEquals(3, compDiff.size());
    }

    @Test
    public void testGetLabel() throws Exception {

        CompositionDifference compDiff = new CompositionDifference(dHex, hexNac, neuAc);

        Assert.assertEquals("d-hex, hexNAc, neuAc", compDiff.getLabel());
    }

    @Test
    public void testEmpty() throws Exception {

        CompositionDifference compDiff = new CompositionDifference();

        Assert.assertEquals("-", compDiff.getLabel());
        Assert.assertEquals(0.0, compDiff.getMolecularMass(), 0.000000001);
    }
}
