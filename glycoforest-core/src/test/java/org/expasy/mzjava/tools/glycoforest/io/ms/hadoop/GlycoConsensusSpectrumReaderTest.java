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
import org.apache.avro.io.DecoderFactory;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.GlycoConsensusSpectrum;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycoConsensusSpectrumReaderTest {

    @Test
    public void testRead() throws Exception {

        GlycoConsensusSpectrumReader reader = new GlycoConsensusSpectrumReader(
                Optional.<PeakList.Precision>absent(),
                Collections.<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>>emptyList());

        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\n" +
                "  \"precursor\" : {\n" +
                "    \"polarity\" : \"NEGATIVE\",\n" +
                "    \"charge\" : [ 1 ],\n" +
                "    \"mz\" : 733.01,\n" +
                "    \"intensity\" : 875.68\n" +
                "  },\n" +
                "  \"msLevel\" : 2,\n" +
                "  \"name\" : \"cluster_1\",\n" +
                "  \"simScoreMean\" : 0.89,\n" +
                "  \"simScoreStdev\" : 0.001,\n" +
                "  \"precursorMzMean\" : 733.01,\n" +
                "  \"precursorMzStdev\" : 0.001,\n" +
                "  \"memberIds\" : [ {\n" +
                "    \"mostSignificantBits\" : 6684906198052325836,\n" +
                "    \"leastSignificantBits\" : -5702870910582741928\n" +
                "  }, {\n" +
                "    \"mostSignificantBits\" : -8429858604094175353,\n" +
                "    \"leastSignificantBits\" : -5657824969867059291\n" +
                "  }, {\n" +
                "    \"mostSignificantBits\" : 7113966495152359336,\n" +
                "    \"leastSignificantBits\" : -5884712445074834741\n" +
                "  }, {\n" +
                "    \"mostSignificantBits\" : -5752031173824135011,\n" +
                "    \"leastSignificantBits\" : -8310288211163081712\n" +
                "  } ],\n" +
                "  \"id\" : {\n" +
                "    \"mostSignificantBits\" : -2693133135828597770,\n" +
                "    \"leastSignificantBits\" : -4936060129817699777\n" +
                "  },\n" +
                "  \"precision\" : \"DOUBLE\",\n" +
                "  \"peaks\" : {\n" +
                "    \"org.expasy.mzjava_avro.core.ms.peaklist.DoublePeakList\" : {\n" +
                "      \"peaks\" : [ {\n" +
                "        \"mz\" : 12.93234579876,\n" +
                "        \"i\" : 14.258741256354124\n" +
                "      }, {\n" +
                "        \"mz\" : 58.92698543776,\n" +
                "        \"i\" : 145.2587412563541\n" +
                "      }, {\n" +
                "        \"mz\" : 152.956783276,\n" +
                "        \"i\" : 545.2587412563541\n" +
                "      } ]\n" +
                "    }\n" +
                "  }\n" +
                "}");
        GlycoConsensusSpectrum spectrum = reader.read(in);

        assertThat(spectrum.getName(), is("cluster_1"));
        assertThat(spectrum.getPrecursorMzMean(), is(733.01));
        assertThat(spectrum.getPrecursorMzStdev(), is(0.001));
        assertThat(spectrum.getSimScoreMean(), is(0.89));
        assertThat(spectrum.getSimScoreStdev(), is(0.001));
        assertThat(spectrum.getMemberIds(), is(makeIds(
                "62b9e2d5-ff96-47a8-ae55-4d35b1fe7acb",
                "8b032043-4c32-4787-b17b-5e2a95bd1fa5",
                "5cc58ec4-dbb7-49cc-b0db-551f36cea458",
                "b02cae1f-7b3d-409d-8cab-ece6d8821410")));
        assertThat(spectrum.getPrecursor(), is(new Peak(733.01, 875.68, -1)));
        assertThat(spectrum.getId(), is(UUID.fromString("daa011ae-8a1a-43f6-bb7f-9776dc3f5a3f")));
        assertThat(spectrum.getMz(0), is(12.93234579876));
        assertThat(spectrum.getIntensity(0), is(14.258741256354125));
        assertThat(spectrum.getMz(1), is(58.92698543776));
        assertThat(spectrum.getIntensity(1), is(145.258741256354125));
        assertThat(spectrum.getMz(2), is(152.956783276));
        assertThat(spectrum.getIntensity(2), is(545.258741256354125));

        assertThat(spectrum.getMsLevel(), is(2));
    }

    private Collection<UUID> makeIds(String... ids) {

        Set<UUID> uuidList = new HashSet<>(ids.length);

        for (String id : ids) {

            uuidList.add(UUID.fromString(id));
        }

        return uuidList;
    }

    @Test
    public void testCreateSchema() throws Exception {

        Schema expected = new Schema.Parser().parse("{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"GlycoConsensusSpectrum\",\n" +
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
                "    \"name\" : \"msLevel\",\n" +
                "    \"type\" : \"int\"\n" +
                "  }, {\n" +
                "    \"name\" : \"name\",\n" +
                "    \"type\" : \"string\"\n" +
                "  }, {\n" +
                "    \"name\" : \"simScoreMean\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"simScoreStdev\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"precursorMzMean\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"precursorMzStdev\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"memberIds\",\n" +
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
                "}");

        Assert.assertEquals(expected.toString(true), new GlycoConsensusSpectrumReader(
                Optional.<PeakList.Precision>absent(),
                Collections.<PeakProcessor<LibPeakAnnotation, LibPeakAnnotation>>emptyList()).createSchema().toString(true)
        );
    }
}
