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
package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.ms.spectrum.GlycanReferenceSpectrum;
import org.expasy.mzjava.avro.io.AbstractPeakListReader;
import org.expasy.mzjava.avro.io.AvroReader;
import org.expasy.mzjava.avro.io.LibPeakAnnotationReader;
import org.expasy.mzjava.avro.io.PeakReader;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroReader.class)
public class GlycanReferenceSpectrumReader extends AbstractPeakListReader<LibPeakAnnotation, GlycanReferenceSpectrum> {

    private final PeakReader peakReader = new PeakReader();
    private final SugarStructureReader structureReader = new SugarStructureReader();

    public GlycanReferenceSpectrumReader() {

        this(Optional.absent(), Collections.emptyList());
    }

    public GlycanReferenceSpectrumReader(Optional<PeakList.Precision> precisionOverride, List<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>> processorList) {

        super(new LibPeakAnnotationReader[]{new LibPeakAnnotationReader()}, precisionOverride, processorList);
    }

    @Override
    public Class getObjectClass() {

        return WithinRunConsensus.class;
    }

    @Override
    protected GlycanReferenceSpectrum newPeakList(PeakList.Precision precision, double constantIntensity) {

        throw new UnsupportedOperationException();
    }

    @Override
    public GlycanReferenceSpectrum read(Decoder in) throws IOException {

        final Peak precursor = peakReader.read(in);
        final SugarStructure structure = structureReader.read(in);

        final int memberCount = in.readInt();
        final double simScoreMean = in.readDouble();
        final double simScoreStdev = in.readDouble();
        final double coverage = in.readDouble();

        GlycanReferenceSpectrum spectrum = super.read(in, (precision, constatnIntensity) -> new GlycanReferenceSpectrum(structure, simScoreMean, simScoreStdev, coverage, memberCount, precision));
        spectrum.setPrecursor(precursor);

        return spectrum;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("precursor", peakReader.createSchema()));
        fields.add(createSchemaField("sugarStructure", structureReader.createSchema()));
        fields.add(createSchemaField("memberCount", Schema.create(Schema.Type.INT)));
        fields.add(createSchemaField("simScoreMean", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("simScoreStdev", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("coverage", Schema.create(Schema.Type.DOUBLE)));

        super.createRecordFields(fields);
    }
}
