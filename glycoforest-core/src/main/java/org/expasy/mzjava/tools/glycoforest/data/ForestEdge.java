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

/**
* @author Oliver Horlacher
* @version sqrt -1
*/
public class ForestEdge implements Comparable<ForestEdge> {

    private final double score;
    private final ForestSpectrumNode parent;
    private final ForestSpectrumNode child;
    private final CompositionDifference modification;

    public ForestEdge(double score, ForestSpectrumNode parent, ForestSpectrumNode child, CompositionDifference modification) {

        this.score = score;
        this.parent = parent;
        this.child = child;
        this.modification = modification;
    }

    public double getScore() {

        return score;
    }

    public CompositionDifference getModification() {

        return modification;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForestEdge forestEdge = (ForestEdge) o;

        return Double.compare(forestEdge.score, score) == 0 &&
                !(child != null ? !child.equals(forestEdge.child) : forestEdge.child != null) &&
                !(parent != null ? !parent.equals(forestEdge.parent) : forestEdge.parent != null);
    }

    @Override
    public int hashCode() {

        int result;
        long temp;
        temp = score != +0.0d ? Double.doubleToLongBits(score) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (child != null ? child.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ForestEdge o) {

        return Double.compare(score, o.score);
    }

    public ForestSpectrumNode getParent() {

        return parent;
    }

    public ForestSpectrumNode getChild() {

        return child;
    }

    @Override
    public String toString() {

        return "NdpEdge{" +
                "parent=" + parent +
                ", child=" + child +
                ", score=" + score +
                '}';
    }
}
