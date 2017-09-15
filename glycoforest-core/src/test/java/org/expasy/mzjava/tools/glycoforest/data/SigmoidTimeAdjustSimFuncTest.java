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

import org.expasy.mzjava.core.ms.spectrum.RetentionTimeList;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SigmoidTimeAdjustSimFuncTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testCalcSimilarity() throws Exception {

        SimFunc simFunc = mock(SimFunc.class);
        when(simFunc.calcSimilarity(Mockito.any(PeakList.class), Mockito.any(PeakList.class))).thenReturn(1.0, 0.5);
        when(simFunc.getBestScore()).thenReturn(1.0);
        when(simFunc.getWorstScore()).thenReturn(0.0);

        SigmoidTimeAdjustSimFunc sigmoidTimeAdjustSimFunc = new SigmoidTimeAdjustSimFunc(simFunc, 1.2, -0.2, -60);

        Assert.assertEquals(1, sigmoidTimeAdjustSimFunc.calcSimilarity(newSpectrum(128), newSpectrum(129)), 0.0000001);
        Assert.assertEquals(0.599995497, sigmoidTimeAdjustSimFunc.calcSimilarity(newSpectrum(128), newSpectrum(129)), 0.0000001);
    }

    private MsnSpectrum newSpectrum(double retentionTime) {

        RetentionTimeList rtList = new RetentionTimeList();
        rtList.add(retentionTime, TimeUnit.SECOND);

        MsnSpectrum spectrum = Mockito.mock(MsnSpectrum.class);
        when(spectrum.getRetentionTimes()).thenReturn(rtList);

        return spectrum;
    }
}
