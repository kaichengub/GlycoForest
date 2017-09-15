package org.expasy.glycoforest.avro.io;

import org.expasy.mzjava.avro.AvroAssert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class WithinRunSimGraphAvroTest {

    @Test
    public void testCreateRecordFields() throws Exception {

        String expected = "{\n" +
                "  \"type\" : \"record\",\n" +
                "  \"name\" : \"WithinRunSimGraph\",\n" +
                "  \"namespace\" : \"org.expasy.glycoforest.data\",\n" +
                "  \"fields\" : [ {\n" +
                "    \"name\" : \"members\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : {\n" +
                "        \"type\" : \"record\",\n" +
                "        \"name\" : \"WithinRunConsensus\",\n" +
                "        \"namespace\" : \"org.expasy.mzjava_avro.tools.glycoforest.ms.spectrum\",\n" +
                "        \"fields\" : [ {\n" +
                "          \"name\" : \"precursor\",\n" +
                "          \"type\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"Peak\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"polarity\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"enum\",\n" +
                "                \"name\" : \"Polarity\",\n" +
                "                \"namespace\" : \"org.expasy.mzjava.core.ms.peaklist\",\n" +
                "                \"symbols\" : [ \"POSITIVE\", \"NEGATIVE\", \"UNKNOWN\" ]\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"name\" : \"charge\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : \"int\"\n" +
                "              }\n" +
                "            }, {\n" +
                "              \"name\" : \"mz\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"intensity\",\n" +
                "              \"type\" : \"double\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"name\" : \"sourceId\",\n" +
                "          \"type\" : {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"UUID\",\n" +
                "            \"namespace\" : \"java.util\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"mostSignificantBits\",\n" +
                "              \"type\" : \"long\"\n" +
                "            }, {\n" +
                "              \"name\" : \"leastSignificantBits\",\n" +
                "              \"type\" : \"long\"\n" +
                "            } ]\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"name\" : \"memberCount\",\n" +
                "          \"type\" : \"int\"\n" +
                "        }, {\n" +
                "          \"name\" : \"simScoreMean\",\n" +
                "          \"type\" : \"double\"\n" +
                "        }, {\n" +
                "          \"name\" : \"simScoreStdev\",\n" +
                "          \"type\" : \"double\"\n" +
                "        }, {\n" +
                "          \"name\" : \"minScanNumber\",\n" +
                "          \"type\" : \"int\"\n" +
                "        }, {\n" +
                "          \"name\" : \"maxScanNumber\",\n" +
                "          \"type\" : \"int\"\n" +
                "        }, {\n" +
                "          \"name\" : \"minRetentionTime\",\n" +
                "          \"type\" : \"double\"\n" +
                "        }, {\n" +
                "          \"name\" : \"maxRetentionTime\",\n" +
                "          \"type\" : \"double\"\n" +
                "        }, {\n" +
                "          \"name\" : \"minMemberMz\",\n" +
                "          \"type\" : \"double\"\n" +
                "        }, {\n" +
                "          \"name\" : \"maxMemberMz\",\n" +
                "          \"type\" : \"double\"\n" +
                "        }, {\n" +
                "          \"name\" : \"id\",\n" +
                "          \"type\" : \"java.util.UUID\"\n" +
                "        }, {\n" +
                "          \"name\" : \"precision\",\n" +
                "          \"type\" : {\n" +
                "            \"type\" : \"enum\",\n" +
                "            \"name\" : \"Precision\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava.core.ms.peaklist\",\n" +
                "            \"symbols\" : [ \"DOUBLE\", \"FLOAT\", \"DOUBLE_FLOAT\", \"DOUBLE_CONSTANT\", \"FLOAT_CONSTANT\" ]\n" +
                "          }\n" +
                "        }, {\n" +
                "          \"name\" : \"peaks\",\n" +
                "          \"type\" : [ {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"DoublePeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"DoublePeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"i\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"FloatPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"FloatPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"i\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"DoubleFloatPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"DoubleFloatPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"i\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"DoubleConstantPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"intensity\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"DoubleConstantPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"FloatConstantPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"intensity\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"FloatConstantPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedDoublePeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"AnnotatedDoublePeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"i\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"annotations\",\n" +
                "                    \"type\" : {\n" +
                "                      \"type\" : \"array\",\n" +
                "                      \"items\" : [ {\n" +
                "                        \"type\" : \"record\",\n" +
                "                        \"name\" : \"LibPeakAnnotation\",\n" +
                "                        \"namespace\" : \"org.expasy.mzjava_avro.core.ms.spectrum\",\n" +
                "                        \"fields\" : [ {\n" +
                "                          \"name\" : \"mergedPeakCount\",\n" +
                "                          \"type\" : \"int\"\n" +
                "                        }, {\n" +
                "                          \"name\" : \"mzStd\",\n" +
                "                          \"type\" : \"double\"\n" +
                "                        }, {\n" +
                "                          \"name\" : \"intensityStd\",\n" +
                "                          \"type\" : \"double\"\n" +
                "                        } ]\n" +
                "                      } ]\n" +
                "                    }\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedFloatPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"AnnotatedFloatPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"i\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"annotations\",\n" +
                "                    \"type\" : {\n" +
                "                      \"type\" : \"array\",\n" +
                "                      \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "                    }\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedDoubleFloatPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"AnnotatedDoubleFloatPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"i\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"annotations\",\n" +
                "                    \"type\" : {\n" +
                "                      \"type\" : \"array\",\n" +
                "                      \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "                    }\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedDoubleConstantPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"intensity\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"AnnotatedDoubleConstantPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"double\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"annotations\",\n" +
                "                    \"type\" : {\n" +
                "                      \"type\" : \"array\",\n" +
                "                      \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "                    }\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          }, {\n" +
                "            \"type\" : \"record\",\n" +
                "            \"name\" : \"AnnotatedFloatConstantPeakList\",\n" +
                "            \"namespace\" : \"org.expasy.mzjava_avro.core.ms.peaklist\",\n" +
                "            \"fields\" : [ {\n" +
                "              \"name\" : \"intensity\",\n" +
                "              \"type\" : \"double\"\n" +
                "            }, {\n" +
                "              \"name\" : \"peaks\",\n" +
                "              \"type\" : {\n" +
                "                \"type\" : \"array\",\n" +
                "                \"items\" : {\n" +
                "                  \"type\" : \"record\",\n" +
                "                  \"name\" : \"AnnotatedFloatConstantPeak\",\n" +
                "                  \"fields\" : [ {\n" +
                "                    \"name\" : \"mz\",\n" +
                "                    \"type\" : \"float\"\n" +
                "                  }, {\n" +
                "                    \"name\" : \"annotations\",\n" +
                "                    \"type\" : {\n" +
                "                      \"type\" : \"array\",\n" +
                "                      \"items\" : [ \"org.expasy.mzjava_avro.core.ms.spectrum.LibPeakAnnotation\" ]\n" +
                "                    }\n" +
                "                  } ]\n" +
                "                }\n" +
                "              }\n" +
                "            } ]\n" +
                "          } ]\n" +
                "        } ]\n" +
                "      }\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"name\" : \"scores\",\n" +
                "    \"type\" : {\n" +
                "      \"type\" : \"array\",\n" +
                "      \"items\" : \"float\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}";

        AvroAssert.assertSchema(expected, new WithinRunSimGraphReader());
        AvroAssert.assertSchema(expected, new WithinRunSimGraphWriter());
    }
}