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

import com.google.common.base.Optional;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.avro.io.AbstractPeakListReader;
import org.expasy.mzjava.avro.io.LibPeakAnnotationReader;
import org.expasy.mzjava.avro.io.PeakReader;
import org.expasy.mzjava.avro.io.UUIDReader;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.GlycoConsensusSpectrum;

import java.io.IOException;
import java.util.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycoConsensusSpectrumReader extends AbstractPeakListReader<LibPeakAnnotation, GlycoConsensusSpectrum> {

    private final PeakReader peakReader = new PeakReader();
    private final UUIDReader uuidReader = new UUIDReader();

    public GlycoConsensusSpectrumReader(Optional<PeakList.Precision> precisionOverride, List<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>> peakProcessors) {

        super(new LibPeakAnnotationReader[]{new LibPeakAnnotationReader()}, precisionOverride, peakProcessors);
    }

    @Override
    public Class getObjectClass() {

        return GlycoConsensusSpectrum.class;
    }

    @Override
    protected GlycoConsensusSpectrum newPeakList(PeakList.Precision precision, double constantIntensity) {

        return new GlycoConsensusSpectrum(100, precision, new HashSet<>());
    }

    @Override
    public GlycoConsensusSpectrum read(Decoder in) throws IOException {

        Peak precursor = peakReader.read(in);
        int msLevel = in.readInt();
        String name = in.readString();
        double simScoreMean = in.readDouble();
        double simScoreStdev = in.readDouble();
        double precursorMzMean = in.readDouble();
        double precursorMzStdev = in.readDouble();
        List<UUID> memberIds = readArray(uuidReader, new ArrayList<>(), in);

        GlycoConsensusSpectrum spectrum = super.read(in);
        spectrum.setPrecursor(precursor);
        spectrum.setMsLevel(msLevel);
        spectrum.setName(name);
        spectrum.setScoreStats(simScoreMean, simScoreStdev);
        spectrum.setPrecursorStats(precursorMzMean, precursorMzStdev);
        spectrum.addMemberIds(memberIds);

        return spectrum;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("precursor", peakReader.createSchema()));
        fields.add(createSchemaField("msLevel", Schema.create(Schema.Type.INT)));

        fields.add(createSchemaField("name", Schema.create(Schema.Type.STRING)));
        fields.add(createSchemaField("simScoreMean", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("simScoreStdev", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("precursorMzMean", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("precursorMzStdev", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("memberIds", Schema.createArray(uuidReader.createSchema())));

        super.createRecordFields(fields);
    }
}
