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
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.expasy.mzjava.avro.AvroAssert;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.core.ms.spectrum.RetentionTimeInterval;
import org.expasy.mzjava.core.ms.spectrum.ScanNumberInterval;
import org.expasy.mzjava.core.ms.spectrum.TimeUnit;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.WithinRunConsensus;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunConsensusAvroTest {

    @Test
    public void testRead() throws Exception {

        WithinRunConsensusReader reader = new WithinRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>>emptyList());
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\n" +
                "  \"precursor\" : {\n" +
                "    \"polarity\" : \"POSITIVE\",\n" +
                "    \"charge\" : [ 1 ],\n" +
                "    \"mz\" : 733.305,\n" +
                "    \"intensity\" : 530.0\n" +
                "  },\n" +
                "  \"sourceId\" : {\n" +
                "    \"mostSignificantBits\" : -2380239986350995223,\n" +
                "    \"leastSignificantBits\" : -7080629348912532485\n" +
                "  },\n" +
                "  \"memberCount\" : 2,\n" +
                "  \"simScoreMean\" : 0.9115,\n" +
                "  \"simScoreStdev\" : 0.0021213203435596446,\n" +
                "  \"minScanNumber\" : 1,\n" +
                "  \"maxScanNumber\" : 2,\n" +
                "  \"minRetentionTime\" : 1305.6,\n" +
                "  \"maxRetentionTime\" : 1306.3,\n" +
                "  \"minMemberMz\" : 733.3,\n" +
                "  \"maxMemberMz\" : 733.31,\n" +
                "  \"id\" : {\n" +
                "    \"mostSignificantBits\" : -2710958556686430480,\n" +
                "    \"leastSignificantBits\" : -6932158121495022232\n" +
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

        WithinRunConsensus consensus = reader.read(in);

        Assert.assertEquals(UUID.fromString("da60bd8e-53e6-4ef0-9fcc-06eaa9733568"), consensus.getId());
        Assert.assertEquals(UUID.fromString("def7b057-ed5e-48e9-9dbc-8d1f37dcf3fb"), consensus.getRunId());
        Assert.assertEquals(PeakList.Precision.FLOAT, consensus.getPrecision());

        final double delta = 0.000000001;

        Assert.assertEquals(2, consensus.size());
        Assert.assertEquals(384.29998779296875, consensus.getMz(0), delta);
        Assert.assertEquals(125.0, consensus.getIntensity(0), delta);
        Assert.assertEquals(733.2999877929688, consensus.getMz(1), delta);
        Assert.assertEquals(234.0, consensus.getIntensity(1), delta);
        Assert.assertEquals(0.9115, consensus.getSimScoreMean(), delta);
        Assert.assertEquals(0.0021213203435596446, consensus.getSimScoreStdev(), delta);
        Assert.assertEquals(new ScanNumberInterval(1, 2), consensus.getScanNumberInterval());
        Assert.assertEquals(new RetentionTimeInterval(1305.6, 1306.3, TimeUnit.SECOND), consensus.getRetentionTimeInterval());
        Assert.assertEquals(733.3, consensus.getMinMemberMz(), delta);
        Assert.assertEquals(733.31, consensus.getMaxMemberMz(), delta);

        Assert.assertEquals(2, consensus.getMemberCount());
    }

    @Test
    public void testWrite() throws Exception {

        WithinRunConsensusWriter writer = new WithinRunConsensusWriter(Optional.<PeakList.Precision>absent());

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        gen.setPrettyPrinter(new DefaultPrettyPrinter());
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        SummaryStatistics scoreStats = new SummaryStatistics();
        scoreStats.addValue(0.91);
        scoreStats.addValue(0.913);

        SummaryStatistics mzStats = new SummaryStatistics();
        mzStats.addValue(733.3);
        mzStats.addValue(733.31);

        WithinRunConsensus consensus = new WithinRunConsensus(
                UUID.fromString("da60bd8e-53e6-4ef0-9fcc-06eaa9733568"),
                UUID.fromString("def7b057-ed5e-48e9-9dbc-8d1f37dcf3fb"),
                scoreStats,
                530.0,
                mzStats,
                new int[]{1},
                new ScanNumberInterval(1, 2),
                new RetentionTimeInterval(1305.6, 1306.3, TimeUnit.SECOND),
                PeakList.Precision.FLOAT);

        consensus.add(733.3, 234);
        consensus.add(384.3, 125);

        writer.write(consensus, encoder);
        encoder.flush();

        Assert.assertEquals("{\n" +
                        "  \"precursor\" : {\n" +
                        "    \"polarity\" : \"POSITIVE\",\n" +
                        "    \"charge\" : [ 1 ],\n" +
                        "    \"mz\" : 733.305,\n" +
                        "    \"intensity\" : 530.0\n" +
                        "  },\n" +
                        "  \"sourceId\" : {\n" +
                        "    \"mostSignificantBits\" : -2380239986350995223,\n" +
                        "    \"leastSignificantBits\" : -7080629348912532485\n" +
                        "  },\n" +
                        "  \"memberCount\" : 2,\n" +
                        "  \"simScoreMean\" : 0.9115,\n" +
                        "  \"simScoreStdev\" : 0.0021213203435596446,\n" +
                        "  \"minScanNumber\" : 1,\n" +
                        "  \"maxScanNumber\" : 2,\n" +
                        "  \"minRetentionTime\" : 1305.6,\n" +
                        "  \"maxRetentionTime\" : 1306.3,\n" +
                        "  \"minMemberMz\" : 733.3,\n" +
                        "  \"maxMemberMz\" : 733.31,\n" +
                        "  \"id\" : {\n" +
                        "    \"mostSignificantBits\" : -2710958556686430480,\n" +
                        "    \"leastSignificantBits\" : -6932158121495022232\n" +
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
    public void testCreateSchema() throws Exception {

        String expected = "{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"WithinRunConsensus\",\n" +
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
                "    \"name\" : \"sourceId\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"UUID\",\n" +
                "      \"namespace\" : \"java.util\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"mostSignificantBits\",\n" +
                "        \"type\" : \"long\"\n" +
                "      }, {\n" +
                "        \"name\" : \"leastSignificantBits\",\n" +
                "        \"type\" : \"long\"\n" +
                "      } ]\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"memberCount\",\n" +
                "    \"type\" : \"int\"\n" +
                "  }, {\n" +
                "    \"name\" : \"simScoreMean\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"simScoreStdev\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"minScanNumber\",\n" +
                "    \"type\" : \"int\"\n" +
                "  }, {\n" +
                "    \"name\" : \"maxScanNumber\",\n" +
                "    \"type\" : \"int\"\n" +
                "  }, {\n" +
                "    \"name\" : \"minRetentionTime\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"maxRetentionTime\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"minMemberMz\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"maxMemberMz\",\n" +
                "    \"type\" : \"double\"\n" +
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

        AvroAssert.assertSchema(expected, new WithinRunConsensusReader(Optional.<PeakList.Precision>absent(), Collections.<PeakProcessor<LibPeakAnnotation,LibPeakAnnotation>>emptyList()));
        AvroAssert.assertSchema(expected, new WithinRunConsensusWriter(Optional.<PeakList.Precision>absent()));
    }
}
