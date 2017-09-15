package org.expasy.glycoforest.app.factories;

import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;

import java.io.Serializable;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public interface ProcessorChainFactory extends Serializable {

    <A extends PeakAnnotation> PeakProcessorChain<A> build();
}
