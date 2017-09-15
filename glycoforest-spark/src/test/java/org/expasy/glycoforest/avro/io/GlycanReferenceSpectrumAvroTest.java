package org.expasy.glycoforest.avro.io;

import com.google.common.base.Optional;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.glycoforest.ms.spectrum.GlycanReferenceSpectrum;
import org.expasy.mzjava.avro.AvroAssert;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessor;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GlycanReferenceSpectrumAvroTest {

    @Test
    public void testRead() throws Exception {

        GlycanReferenceSpectrumReader reader = new GlycanReferenceSpectrumReader(Optional.absent(), Collections.emptyList());
        Decoder in = DecoderFactory.get().jsonDecoder(reader.createSchema(), "{\n" +
                "  \"precursor\" : {\n" +
                "    \"polarity\" : \"POSITIVE\",\n" +
                "    \"charge\" : [ 1 ],\n" +
                "    \"mz\" : 733.3,\n" +
                "    \"intensity\" : 257.36\n" +
                "  },\n" +
                "  \"sugarStructure\" : {\n" +
                "    \"label\" : \"test structure\",\n" +
                "    \"structure\" : \"Fuc(Hex)HexNAcol\"\n" +
                "  },\n" +
                "  \"memberCount\" : 12,\n" +
                "  \"simScoreMean\" : 0.9115,\n" +
                "  \"simScoreStdev\" : 0.0021213203435596446,\n" +
                "  \"coverage\" : 0.8,\n" +
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

        GlycanReferenceSpectrum consensus = reader.read(in);

        Assert.assertEquals(UUID.fromString("da60bd8e-53e6-4ef0-9fcc-06eaa9733568"), consensus.getId());
        Assert.assertEquals(PeakList.Precision.FLOAT, consensus.getPrecision());

        final double delta = 0.000000001;

        Assert.assertEquals(2, consensus.size());
        Assert.assertEquals(384.29998779296875, consensus.getMz(0), delta);
        Assert.assertEquals(125.0, consensus.getIntensity(0), delta);
        Assert.assertEquals(733.2999877929688, consensus.getMz(1), delta);
        Assert.assertEquals(234.0, consensus.getIntensity(1), delta);
        Assert.assertEquals(0.9115, consensus.getSimScoreMean(), delta);
        Assert.assertEquals(0.0021213203435596446, consensus.getSimScoreStdev(), delta);

        Assert.assertEquals(12, consensus.getMemberCount());
    }

    @Test
    public void testWrite() throws Exception {

        GlycanReferenceSpectrumWriter writer = new GlycanReferenceSpectrumWriter(Optional.absent());

        StringWriter out = new StringWriter();
        JsonGenerator gen = new JsonFactory().createJsonGenerator(out);
        gen.setPrettyPrinter(new DefaultPrettyPrinter());
        Encoder encoder = EncoderFactory.get().jsonEncoder(writer.createSchema(), gen);

        final SugarStructure structure = new SugarStructure.Builder("test structure", SugarUnit.HexNAc)
                .branch().add(SugarUnit.Hex)
                .pop().add(SugarUnit.Fuc)
                .build();

        GlycanReferenceSpectrum consensus = new GlycanReferenceSpectrum(
                UUID.fromString("da60bd8e-53e6-4ef0-9fcc-06eaa9733568"),
                structure,
                0.9115,
                0.0021213203435596446,
                0.8,
                12, 733.3, 257.36,
                new int[]{1},
                PeakList.Precision.FLOAT);
        consensus.add(733.3, 234);
        consensus.add(384.3, 125);

        writer.write(consensus, encoder);
        encoder.flush();

        Assert.assertEquals("{\n" +
                        "  \"precursor\" : {\n" +
                        "    \"polarity\" : \"POSITIVE\",\n" +
                        "    \"charge\" : [ 1 ],\n" +
                        "    \"mz\" : 733.3,\n" +
                        "    \"intensity\" : 257.36\n" +
                        "  },\n" +
                        "  \"sugarStructure\" : {\n" +
                        "    \"label\" : \"test structure\",\n" +
                        "    \"structure\" : \"Fuc(Hex)HexNAcol\"\n" +
                        "  },\n" +
                        "  \"memberCount\" : 12,\n" +
                        "  \"simScoreMean\" : 0.9115,\n" +
                        "  \"simScoreStdev\" : 0.0021213203435596446,\n" +
                        "  \"coverage\" : 0.8,\n" +
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
                "    \"name\" : \"sugarStructure\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"record\",\n" +
                "      \"name\" : \"SugarStructure\",\n" +
                "      \"namespace\" : \"org.expasy.glycoforest.mol\",\n" +
                "      \"fields\" : [ {\n" +
                "        \"name\" : \"label\",\n" +
                "        \"type\" : \"string\"\n" +
                "      }, {\n" +
                "        \"name\" : \"structure\",\n" +
                "        \"type\" : \"string\"\n" +
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
                "    \"name\" : \"coverage\",\n" +
                "    \"type\" : \"double\"\n" +
                "  }, {\n" +
                "    \"name\" : \"id\",\n" +
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

        AvroAssert.assertSchema(expected, new GlycanReferenceSpectrumReader(Optional.<PeakList.Precision>absent(), Collections.<PeakProcessor<LibPeakAnnotation,LibPeakAnnotation>>emptyList()));
        AvroAssert.assertSchema(expected, new GlycanReferenceSpectrumWriter(Optional.<PeakList.Precision>absent()));
    }
}