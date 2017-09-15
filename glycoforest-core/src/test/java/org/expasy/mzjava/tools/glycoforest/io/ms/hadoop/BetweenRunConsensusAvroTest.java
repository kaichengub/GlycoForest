/**
 * Copyright (c) 2010, SIB. All rights reserved.
 * <p>
 * SIB (Swiss Institute of Bioinformatics) - http://www.isb-sib.ch Host -
 * http://mzjava.expasy.org
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the SIB/GENEBIO nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
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
import com.google.common.collect.Lists;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.expasy.mzjava.avro.AvroAssert;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class BetweenRunConsensusAvroTest {
    @Test
    public void testRead() throws Exception {

        BetweenRunConsensusReader reader = new BetweenRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>>emptyList());
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\n" +
                "  \"precursor\" : {\n" +
                "    \"polarity\" : \"UNKNOWN\",\n" +
                "    \"charge\" : [ ],\n" +
                "    \"mz\" : 733.3,\n" +
                "    \"intensity\" : 3.0\n" +
                "  },\n" +
                "  \"msnMemberCount\" : 45,\n" +
                "  \"withinRunMemberCount\" : 8,\n" +
                "  \"simScoreMean\" : 0.91245,\n" +
                "  \"simScoreStdev\" : 1.4E-4,\n" +
                "  \"minWithinRunMz\" : 732.9,\n" +
                "  \"maxWithinRunMz\" : 733.3,\n" +
                "  \"runIds\" : [ {\n" +
                "    \"mostSignificantBits\" : 674882980177201046,\n" +
                "    \"leastSignificantBits\" : -4958938003086651251\n" +
                "  }, {\n" +
                "    \"mostSignificantBits\" : -4259235767994268908,\n" +
                "    \"leastSignificantBits\" : -5572335692694768329\n" +
                "  }, {\n" +
                "    \"mostSignificantBits\" : -1152661488313023034,\n" +
                "    \"leastSignificantBits\" : -7228953203346197630\n" +
                "  } ],\n" +
                "  \"id\" : {\n" +
                "    \"mostSignificantBits\" : 365105805546114090,\n" +
                "    \"leastSignificantBits\" : -5349208500468332319\n" +
                "  },\n" +
                "  \"precision\" : \"FLOAT\",\n" +
                "  \"peaks\" : {\n" +
                "    \"org.expasy.mzjava_avro.core.ms.peaklist.FloatPeakList\" : {\n" +
                "      \"peaks\" : [ {\n" +
                "        \"mz\" : 384.3,\n" +
                "        \"i\" : 125.0\n" +
                "      }, {\n" +
                "        \"mz\" : 733.3,\n" +
                "        \"i\" : 234.0\n" +
                "      } ]\n" +
                "    }\n" +
                "  }\n" +
                "}");

        BetweenRunConsensus consensus = reader.read(in);

        Assert.assertEquals(0.91245, consensus.getSimScoreMean(), 0.000000001);
        Assert.assertEquals(0.00014, consensus.getSimScoreStdev(), 0.000000001);

        Assert.assertEquals(UUID.fromString("05111dcb-b502-4c2a-b5c3-cb35cc15c8e1"), consensus.getId());
        Assert.assertEquals(45, consensus.getMsnMemberCount());
        Assert.assertEquals(8, consensus.getWithinRunMemberCount());
        Assert.assertEquals(PeakList.Precision.FLOAT, consensus.getPrecision());
        Assert.assertEquals(new Peak(733.3, 3), consensus.getPrecursor());
        Assert.assertEquals(0.91245, consensus.getSimScoreMean(), 0.00000001);
        Assert.assertEquals(0.00014, consensus.getSimScoreStdev(), 0.00000001);
        Assert.assertEquals(732.9, consensus.getMinWithinRunMz(), 0.00000001);
        Assert.assertEquals(733.3, consensus.getMaxWithinRunMz(), 0.00000001);
        Assert.assertEquals(Lists.newArrayList(
                UUID.fromString("095daa7e-aa6b-4796-bb2e-5028e6d5d08d"),
                UUID.fromString("c4e427a2-aa10-4314-b2ab-163603b2b537"),
                UUID.fromString("f000ec7b-c2d4-45c6-9bad-995cb8a0b382"))
                , consensus.getRunIds()
        );

        Assert.assertEquals(2, consensus.size());
        Assert.assertEquals(384.29998779296875, consensus.getMz(0), 0.000000001);
        Assert.assertEquals(125.0, consensus.getIntensity(0), 0.000000001);
        Assert.assertEquals(733.2999877929688, consensus.getMz(1), 0.000000001);
        Assert.assertEquals(234.0, consensus.getIntensity(1), 0.000000001);
    }

    @Test
    public void testReadWithAnnotations() throws Exception {

        BetweenRunConsensusReader reader = new BetweenRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>>emptyList());
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\n" +
                "  \"precursor\" : {\n" +
                "    \"polarity\" : \"UNKNOWN\",\n" +
                "    \"charge\" : [ ],\n" +
                "    \"mz\" : 733.3,\n" +
                "    \"intensity\" : 3.0\n" +
                "  },\n" +
                "  \"msnMemberCount\" : 45,\n" +
                "  \"withinRunMemberCount\" : 8,\n" +
                "  \"simScoreMean\" : 0.91245,\n" +
                "  \"simScoreStdev\" : 1.4E-4,\n" +
                "  \"minWithinRunMz\" : 732.9,\n" +
                "  \"maxWithinRunMz\" : 733.3,\n" +
                "  \"runIds\" : [ {\n" +
                "    \"mostSignificantBits\" : 674882980177201046,\n" +
                "    \"leastSignificantBits\" : -4958938003086651251\n" +
                "  }, {\n" +
                "    \"mostSignificantBits\" : -4259235767994268908,\n" +
                "    \"leastSignificantBits\" : -5572335692694768329\n" +
                "  }, {\n" +
                "    \"mostSignificantBits\" : -1152661488313023034,\n" +
                "    \"leastSignificantBits\" : -7228953203346197630\n" +
                "  } ],\n" +
                "  \"id\" : {\n" +
                "    \"mostSignificantBits\" : 365105805546114090,\n" +
                "    \"leastSignificantBits\" : -5349208500468332319\n" +
                "  },\n" +
                "  \"precision\" : \"FLOAT\",\n" +
                "  \"peaks\" : {\n" +
                "    \"org.expasy.mzjava_avro.core.ms.peaklist.AnnotatedFloatPeakList\" : {\n" +
                "      \"peaks\" : [ {\n" +
                "        \"mz\" : 384.3,\n" +
                "        \"i\" : 125.0,\n" +
                "        \"annotations\" : [ ]\n" +
                "      }, {\n" +
                "        \"mz\" : 733.3,\n" +
                "        \"i\" : 234.0,\n" +
                "        \"annotations\" : [ {\n" +
                "          \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" : {\n" +
                "            \"mergedPeakCount\" : 23,\n" +
                "            \"mzStd\" : 0.09,\n" +
                "            \"intensityStd\" : 12.0\n" +
                "          }\n" +
                "        } ]\n" +
                "      } ]\n" +
                "    }\n" +
                "  }\n" +
                "}");

        BetweenRunConsensus consensus = reader.read(in);

        Assert.assertEquals(0.91245, consensus.getSimScoreMean(), 0.000000001);
        Assert.assertEquals(0.00014, consensus.getSimScoreStdev(), 0.000000001);

        Assert.assertEquals(UUID.fromString("05111dcb-b502-4c2a-b5c3-cb35cc15c8e1"), consensus.getId());
        Assert.assertEquals(45, consensus.getMsnMemberCount());
        Assert.assertEquals(8, consensus.getWithinRunMemberCount());
        Assert.assertEquals(PeakList.Precision.FLOAT, consensus.getPrecision());
        Assert.assertEquals(new Peak(733.3, 3), consensus.getPrecursor());
        Assert.assertEquals(0.91245, consensus.getSimScoreMean(), 0.00000001);
        Assert.assertEquals(0.00014, consensus.getSimScoreStdev(), 0.00000001);
        Assert.assertEquals(732.9, consensus.getMinWithinRunMz(), 0.00000001);
        Assert.assertEquals(733.3, consensus.getMaxWithinRunMz(), 0.00000001);
        Assert.assertEquals(Lists.newArrayList(
                        UUID.fromString("095daa7e-aa6b-4796-bb2e-5028e6d5d08d"),
                        UUID.fromString("c4e427a2-aa10-4314-b2ab-163603b2b537"),
                        UUID.fromString("f000ec7b-c2d4-45c6-9bad-995cb8a0b382"))
                , consensus.getRunIds()
        );

        Assert.assertEquals(2, consensus.size());
        Assert.assertEquals(384.29998779296875, consensus.getMz(0), 0.000000001);
        Assert.assertEquals(125.0, consensus.getIntensity(0), 0.000000001);

        Assert.assertEquals(733.2999877929688, consensus.getMz(1), 0.000000001);
        Assert.assertEquals(234.0, consensus.getIntensity(1), 0.000000001);
        Assert.assertEquals(1, consensus.getAnnotations(1).size());
        Assert.assertEquals(new LibPeakAnnotation(23, 0.09, 12), consensus.getFirstAnnotation(1).get());
    }

    @Test
    public void testWrite() throws Exception {

        BetweenRunConsensusWriter writer = new BetweenRunConsensusWriter(Optional.<PeakList.Precision>absent());

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        gen.setPrettyPrinter(new DefaultPrettyPrinter());
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        BetweenRunConsensus consensus = new BetweenRunConsensus(PeakList.Precision.FLOAT);
        consensus.getPrecursor().setValues(733.3, 3);
        consensus.setId(UUID.fromString("05111dcb-b502-4c2a-b5c3-cb35cc15c8e1"));
        consensus.setFields(0.91245, 0.00014, 45, 8, 732.9, 733.3, Lists.newArrayList(
                        UUID.fromString("095daa7e-aa6b-4796-bb2e-5028e6d5d08d"),
                        UUID.fromString("c4e427a2-aa10-4314-b2ab-163603b2b537"),
                        UUID.fromString("f000ec7b-c2d4-45c6-9bad-995cb8a0b382"))
        );

        consensus.add(733.3, 234);
        consensus.add(384.3, 125);

        writer.write(consensus, encoder);
        encoder.flush();

        Assert.assertEquals("{\n" +
                        "  \"precursor\" : {\n" +
                        "    \"polarity\" : \"UNKNOWN\",\n" +
                        "    \"charge\" : [ ],\n" +
                        "    \"mz\" : 733.3,\n" +
                        "    \"intensity\" : 3.0\n" +
                        "  },\n" +
                        "  \"msnMemberCount\" : 45,\n" +
                        "  \"withinRunMemberCount\" : 8,\n" +
                        "  \"simScoreMean\" : 0.91245,\n" +
                        "  \"simScoreStdev\" : 1.4E-4,\n" +
                        "  \"minWithinRunMz\" : 732.9,\n" +
                        "  \"maxWithinRunMz\" : 733.3,\n" +
                        "  \"runIds\" : [ {\n" +
                        "    \"mostSignificantBits\" : 674882980177201046,\n" +
                        "    \"leastSignificantBits\" : -4958938003086651251\n" +
                        "  }, {\n" +
                        "    \"mostSignificantBits\" : -4259235767994268908,\n" +
                        "    \"leastSignificantBits\" : -5572335692694768329\n" +
                        "  }, {\n" +
                        "    \"mostSignificantBits\" : -1152661488313023034,\n" +
                        "    \"leastSignificantBits\" : -7228953203346197630\n" +
                        "  } ],\n" +
                        "  \"id\" : {\n" +
                        "    \"mostSignificantBits\" : 365105805546114090,\n" +
                        "    \"leastSignificantBits\" : -5349208500468332319\n" +
                        "  },\n" +
                        "  \"precision\" : \"FLOAT\",\n" +
                        "  \"peaks\" : {\n" +
                        "    \"org.expasy.mzjava_avro.core.ms.peaklist.FloatPeakList\" : {\n" +
                        "      \"peaks\" : [ {\n" +
                        "        \"mz\" : 384.3,\n" +
                        "        \"i\" : 125.0\n" +
                        "      }, {\n" +
                        "        \"mz\" : 733.3,\n" +
                        "        \"i\" : 234.0\n" +
                        "      } ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                out.toString().replace("\r", ""));
    }

    @Test
    public void testWriteWithAnnotations() throws Exception {

        BetweenRunConsensusWriter writer = new BetweenRunConsensusWriter(Optional.<PeakList.Precision>absent());

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        gen.setPrettyPrinter(new DefaultPrettyPrinter());
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        BetweenRunConsensus consensus = new BetweenRunConsensus(PeakList.Precision.FLOAT);
        consensus.getPrecursor().setValues(733.3, 3);
        consensus.setId(UUID.fromString("05111dcb-b502-4c2a-b5c3-cb35cc15c8e1"));
        consensus.setFields(0.91245, 0.00014, 45, 8, 732.9, 733.3, Lists.newArrayList(
                        UUID.fromString("095daa7e-aa6b-4796-bb2e-5028e6d5d08d"),
                        UUID.fromString("c4e427a2-aa10-4314-b2ab-163603b2b537"),
                        UUID.fromString("f000ec7b-c2d4-45c6-9bad-995cb8a0b382"))
        );

        consensus.add(384.3, 125);
        consensus.add(733.3, 234, Collections.singletonList(new LibPeakAnnotation(23, 0.09, 12)));

        writer.write(consensus, encoder);
        encoder.flush();

        Assert.assertEquals("{\n" +
                        "  \"precursor\" : {\n" +
                        "    \"polarity\" : \"UNKNOWN\",\n" +
                        "    \"charge\" : [ ],\n" +
                        "    \"mz\" : 733.3,\n" +
                        "    \"intensity\" : 3.0\n" +
                        "  },\n" +
                        "  \"msnMemberCount\" : 45,\n" +
                        "  \"withinRunMemberCount\" : 8,\n" +
                        "  \"simScoreMean\" : 0.91245,\n" +
                        "  \"simScoreStdev\" : 1.4E-4,\n" +
                        "  \"minWithinRunMz\" : 732.9,\n" +
                        "  \"maxWithinRunMz\" : 733.3,\n" +
                        "  \"runIds\" : [ {\n" +
                        "    \"mostSignificantBits\" : 674882980177201046,\n" +
                        "    \"leastSignificantBits\" : -4958938003086651251\n" +
                        "  }, {\n" +
                        "    \"mostSignificantBits\" : -4259235767994268908,\n" +
                        "    \"leastSignificantBits\" : -5572335692694768329\n" +
                        "  }, {\n" +
                        "    \"mostSignificantBits\" : -1152661488313023034,\n" +
                        "    \"leastSignificantBits\" : -7228953203346197630\n" +
                        "  } ],\n" +
                        "  \"id\" : {\n" +
                        "    \"mostSignificantBits\" : 365105805546114090,\n" +
                        "    \"leastSignificantBits\" : -5349208500468332319\n" +
                        "  },\n" +
                        "  \"precision\" : \"FLOAT\",\n" +
                        "  \"peaks\" : {\n" +
                        "    \"org.expasy.mzjava_avro.core.ms.peaklist.AnnotatedFloatPeakList\" : {\n" +
                        "      \"peaks\" : [ {\n" +
                        "        \"mz\" : 384.3,\n" +
                        "        \"i\" : 125.0,\n" +
                        "        \"annotations\" : [ ]\n" +
                        "      }, {\n" +
                        "        \"mz\" : 733.3,\n" +
                        "        \"i\" : 234.0,\n" +
                        "        \"annotations\" : [ {\n" +
                        "          \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" : {\n" +
                        "            \"mergedPeakCount\" : 23,\n" +
                        "            \"mzStd\" : 0.09,\n" +
                        "            \"intensityStd\" : 12.0\n" +
                        "          }\n" +
                        "        } ]\n" +
                        "      } ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                out.toString().replace("\r", ""));
    }

    @Test
    public void testCreateSchema() throws Exception {

        String expected = "{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"BetweenRunConsensus\",\n" +
                "  \"namespace\" : \"org.expasy.mzjava_avro.tools.glycoforest.ms.spectrum\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"name\" : \"precursor\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"Peak\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"polarity\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"enum\",\n" +
                "          \"name\" : \"Polarity\",\n" +
                "          \"namespace\" : \"org.expasy.mzjava.core.ms.peaklist\",\n" +
                "          \"symbols\" : [ \"POSITIVE\", \"NEGATIVE\", \"UNKNOWN\" ]\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"name\" : \"charge\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : \"int\"\n" +
                "        }\n" +
                "      }, {\n" +
                "        \"name\" : \"mz\",\n" +
                "        \"type\" : \"double\"\n" +
                "      }, {\n" +
                "        \"name\" : \"intensity\",\n" +
                "        \"type\" : \"double\"\n" +
                "      } ]\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"msnMemberCount\",\n" +
                "    \"type\" : \"int\"\n" +
                "  }, {\n" +
                "    \"name\" : \"withinRunMemberCount\",\n" +
                "    \"type\" : \"int\"\n" +
                "  }, {\n" +
                "    \"name\" : \"simScoreMean\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"simScoreStdev\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"minWithinRunMz\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"maxWithinRunMz\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"runIds\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : {\n" +
                "        \"type\" : \"record\",\n" +
                "        \"name\" : \"UUID\",\n" +
                "        \"namespace\" : \"java.util\",\n" +
                "        \"fields\" : [ {\n" +
                "          \"name\" : \"mostSignificantBits\",\n" +
                "          \"type\" : \"long\"\n" +
                "        }, {\n" +
                "          \"name\" : \"leastSignificantBits\",\n" +
                "          \"type\" : \"long\"\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"id\",\n" +
                "    \"type\" : \"java.util.UUID\"\n" +
                "  }, {\n" +
                "    \"name\" : \"precision\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"enum\",\n" +
                "      \"name\" : \"Precision\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava.core.ms.peaklist\",\n" +
                "      \"symbols\" : [ \"DOUBLE\", \"FLOAT\", \"DOUBLE_FLOAT\", \"DOUBLE_CONSTANT\", \"FLOAT_CONSTANT\" ]\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"peaks\",\n" +
                "    \"type\" : [ {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"DoublePeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"DoublePeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"i\",\n" +
                "              \"type\" : \"double\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"FloatPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"FloatPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"float\"\n" +
                "            }, {\n" +
                "              \"name\" : \"i\",\n" +
                "              \"type\" : \"float\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"DoubleFloatPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"DoubleFloatPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"i\",\n" +
                "              \"type\" : \"float\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"DoubleConstantPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"intensity\",\n" +
                "        \"type\" : \"double\"\n" +
                "      }, {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"DoubleConstantPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"double\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"FloatConstantPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"intensity\",\n" +
                "        \"type\" : \"double\"\n" +
                "      }, {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"FloatConstantPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"float\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"AnnotatedDoublePeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedDoublePeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"i\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"annotations\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : [ {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"LibPeakAnnotation\",\n" +
                "                  \"namespace\" : \"org.expasy.mzjava_avro.core.ms.spectrum\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mergedPeakCount\",\n" +
                "                    \"type\" : \"int\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"mzStd\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"intensityStd\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  } ]\n" +
                "                } ]\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"AnnotatedFloatPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedFloatPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"float\"\n" +
                "            }, {\n" +
                "              \"name\" : \"i\",\n" +
                "              \"type\" : \"float\"\n" +
                "            }, {\n" +
                "              \"name\" : \"annotations\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"AnnotatedDoubleFloatPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedDoubleFloatPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"i\",\n" +
                "              \"type\" : \"float\"\n" +
                "            }, {\n" +
                "              \"name\" : \"annotations\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"AnnotatedDoubleConstantPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"intensity\",\n" +
                "        \"type\" : \"double\"\n" +
                "      }, {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedDoubleConstantPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"annotations\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    }, {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"AnnotatedFloatConstantPeakList\",\n" +
                "      \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"intensity\",\n" +
                "        \"type\" : \"double\"\n" +
                "      }, {\n" +
                "        \"name\" : \"peaks\",\n" +
                "        \"type\" : {\n" +
                "          \"type\" : \"array\",\n" +
                "          \"items\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedFloatConstantPeak\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"float\"\n" +
                "            }, {\n" +
                "              \"name\" : \"annotations\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "              }\n" +
                "            } ]\n" +
                "          }\n" +
                "        }\n" +
                "      } ]\n" +
                "    } ]\n" +
                "  } ]\n" +
                "}";

        AvroAssert.assertSchema(expected, new BetweenRunConsensusWriter(Optional.<PeakList.Precision>absent()));
        AvroAssert.assertSchema(expected, new BetweenRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>>emptyList()));
    }
}
