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
package org.expasy.mzjava.tools.glycoforest.io.ms.hadoop;

import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.hadoop.io.MockDataInput;
import org.expasy.mzjava.hadoop.io.MockDataOutput;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.GlycoConsensusSpectrum;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycoConsensusSpectrumValueTest {

    private final String base64 = "AgICADMzMzMz64ZAAAAAAAAAWUAEEmNsdXN0ZXJfMXsUrkfheuw/hooWRbTCiT8zMzMzM+uGQPyp\n" +
            "8dJNYlA/AK6s0q3Hr+isR4GswdC3xuLi6QECDAgAAIA/AAAgQQAAAABAAACgQQIAGPyp8dJNYlA/\n" +
            "AAAAAAAAVEAAAABAQAAA8EEAAACAQAAAIEICACpqvHSTGATwPwAAAAAAACBAAAA=";

    @Test
    public void testWrite() throws Exception {

        GlycoConsensusSpectrum consensus = new GlycoConsensusSpectrum(3, PeakList.Precision.FLOAT, new HashSet<>());
        consensus.setName("cluster_1");
        consensus.getPrecursor().setValues(733.4, 100, -1);
        consensus.setScoreStats(0.89, 0.0125784);
        consensus.setPrecursorStats(733.4, 0.001);
        consensus.setId(UUID.fromString("23acd0be-3ada-4b17-8b1d-3ae642f7d4ff"));
        consensus.setMsLevel(2);

        consensus.addSorted(new double[]{1, 2, 3, 4}, new double[]{10, 20, 30, 40});
        consensus.addAnnotation(1, new LibPeakAnnotation(12, 0.001, 80));
        consensus.addAnnotation(3, new LibPeakAnnotation(21, 1.001, 8));

        GlycoConsensusSpectrumValue value = new GlycoConsensusSpectrumValue();

        value.set(consensus);

        MockDataOutput out = new MockDataOutput(512);
        value.write(out);

        assertEquals(base64, out.getBase64());
    }

    @Test
    public void testReadFields() throws Exception {

        GlycoConsensusSpectrumValue value = new GlycoConsensusSpectrumValue();
        value.readFields(new MockDataInput(base64));

        GlycoConsensusSpectrum consensus = value.get();

        assertThat(consensus.getPrecursor(), is(new Peak(733.4, 100, -1)));
        assertThat(consensus.getId(), is(UUID.fromString("23acd0be-3ada-4b17-8b1d-3ae642f7d4ff")));
        assertThat(consensus.getName(), is("cluster_1"));
        assertThat(consensus.getSimScoreMean(), is(0.89));
        assertThat(consensus.getSimScoreStdev(), is(0.0125784));
        assertThat(consensus.getPrecursorMzMean(), is(733.4));
        assertThat(consensus.getPrecursorMzStdev(), is(0.001));
        assertThat(consensus.getMsLevel(), is(2));

        assertThat(consensus.size(), is(4));

        assertThat(consensus.getMz(0), is(1.0));
        assertThat(consensus.getMz(1), is(2.0));
        assertThat(consensus.getMz(2), is(3.0));
        assertThat(consensus.getMz(3), is(4.0));

        assertThat(consensus.getIntensity(0), is(10.0));
        assertThat(consensus.getIntensity(1), is(20.0));
        assertThat(consensus.getIntensity(2), is(30.0));
        assertThat(consensus.getIntensity(3), is(40.0));

        assertThat(consensus.getAnnotationIndexes(), is(new int[]{1, 3}));
        assertThat(consensus.getAnnotations(1), is(Collections.singletonList(new LibPeakAnnotation(12, 0.001, 80))));
        assertThat(consensus.getAnnotations(3), is(Collections.singletonList(new LibPeakAnnotation(21, 1.001, 8))));

        //Check that everything is reset on second call to read
        value.readFields(new MockDataInput(base64));
        consensus = value.get();
        assertEquals(4, consensus.size());
        assertThat(consensus.getMz(0), is(1.0));
        assertThat(consensus.getMz(1), is(2.0));
        assertThat(consensus.getMz(2), is(3.0));
        assertThat(consensus.getMz(3), is(4.0));

        assertThat(consensus.getIntensity(0), is(10.0));
        assertThat(consensus.getIntensity(1), is(20.0));
        assertThat(consensus.getIntensity(2), is(30.0));
        assertThat(consensus.getIntensity(3), is(40.0));
    }
}
