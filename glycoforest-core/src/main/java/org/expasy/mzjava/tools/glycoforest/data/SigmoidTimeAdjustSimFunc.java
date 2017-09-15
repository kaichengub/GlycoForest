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

import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrasim.SimFunc;
import org.expasy.mzjava.core.ms.spectrum.MsnSpectrum;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;

/**
 * Uses the Logistic function below to weigh the sim score according to the retention time difference.
 * <p/>
 * <pre>
 *         c
 * w = ---------
 *           -b(t+offset)
 *     1 + e
 * </pre>
 *
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SigmoidTimeAdjustSimFunc<A extends PeakAnnotation> implements SimFunc<A, A> {

    private final SimFunc<A, A> simFunc;
    private final double c;
    private final double b;
    private final double offset;
    private final double maxScore;
    private final double minScore;

    public SigmoidTimeAdjustSimFunc(SimFunc<A, A> simFunc, double c, double b, double offset) {

        this.simFunc = simFunc;
        this.c = c;
        this.b = b;
        this.offset = offset;

        double bestScore = simFunc.getBestScore();
        double worstScore = simFunc.getWorstScore();

        if (bestScore > worstScore) {

            maxScore = bestScore;
            minScore = worstScore;
        } else if (bestScore < worstScore) {

            maxScore = worstScore;
            minScore = bestScore;
        } else {

            throw new IllegalStateException("Best and worst scores cannot be equal");
        }
    }

    @Override
    public double calcSimilarity(PeakList<A> plX, PeakList<A> plY) {

        MsnSpectrum spectrum1 = (MsnSpectrum)plX;
        MsnSpectrum spectrum2 = (MsnSpectrum)plY;

        double sim = simFunc.calcSimilarity(plX, plY);

        double t = Math.abs(spectrum1.getRetentionTimes().getFirst().getTime() - spectrum2.getRetentionTimes().getLast().getTime());

        double weight = c / (1 + Math.exp(-b * (t + offset)));

        double score = sim * weight;
        if (score > maxScore)
            score = maxScore;
        else if (score < minScore)
            score = minScore;

        return score;
    }

    @Override
    public int getTotalPeakCount() {

        return simFunc.getTotalPeakCount();
    }

    @Override
    public double getBestScore() {

        return simFunc.getBestScore();
    }

    @Override
    public double getWorstScore() {

        return simFunc.getWorstScore();
    }
}
