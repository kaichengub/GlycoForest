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
import org.expasy.mzjava.avro.io.*;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.openide.util.lookup.ServiceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
@ServiceProvider(service=AvroReader.class)
public class BetweenRunConsensusReader extends AbstractPeakListReader<LibPeakAnnotation, BetweenRunConsensus> {

    private final PeakReader peakReader = new PeakReader();
    private final UUIDReader uuidReader = new UUIDReader();

    public BetweenRunConsensusReader() {

        this(Optional.absent(), Collections.emptyList());
    }

    public BetweenRunConsensusReader(Optional<PeakList.Precision> precisionOverride, List<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>> processorList) {

        super(new LibPeakAnnotationReader[]{new LibPeakAnnotationReader()}, precisionOverride, processorList);
    }

    @Override
    public Class getObjectClass() {

        return BetweenRunConsensus.class;
    }

    @Override
    protected BetweenRunConsensus newPeakList(PeakList.Precision precision, double constantIntensity) {

        return new BetweenRunConsensus(precision);
    }

    @Override
    public BetweenRunConsensus read(Decoder in) throws IOException {

        final Peak precursor = peakReader.read(in);
        final int msnMemberCount = in.readInt();
        final int withinRunMemberCount = in.readInt();
        final double simScoreMean = in.readDouble();
        final double simScoreStdev = in.readDouble();
        final double minWithinRunMz = in.readDouble();
        final double maxWithinRunMz = in.readDouble();
        final ArrayList<UUID> runIds = readArray(uuidReader, new ArrayList<>(), in);

        BetweenRunConsensus spectrum = super.read(in);
        spectrum.setPrecursor(precursor);
        spectrum.setFields(simScoreMean, simScoreStdev, msnMemberCount, withinRunMemberCount, minWithinRunMz, maxWithinRunMz, runIds);

        return spectrum;
    }

    @Override
    protected void createRecordFields(List<Schema.Field> fields) {

        fields.add(createSchemaField("precursor", peakReader.createSchema()));
        fields.add(createSchemaField("msnMemberCount", Schema.create(Schema.Type.INT)));
        fields.add(createSchemaField("withinRunMemberCount", Schema.create(Schema.Type.INT)));
        fields.add(createSchemaField("simScoreMean", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("simScoreStdev", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("minWithinRunMz", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("maxWithinRunMz", Schema.create(Schema.Type.DOUBLE)));
        fields.add(createSchemaField("runIds", Schema.createArray(uuidReader.createSchema())));

        super.createRecordFields(fields);
    }
}
