package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakAnnotation;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.*;
import org.expasy.mzjava.hadoop.io.MockDataInput;
import org.expasy.mzjava.hadoop.io.MockDataOutput;
import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.ms.spectrum.PepFragAnnotation;
import org.expasy.mzjava.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

public class MsnSpectrumSerializerTest {

    private final String base64 = "AgICAGZmZmZm6oZAAAAAAADAVUAE1AMApkcYUGVyc2V2ZXJhbmNlAgDNzMzMzESLQAACAK5HADhz\n" +
            "b2Z0d2FyZTovL29yZy5leHBhc3kvdGVzdGVyIlRoaXMgaXMgYSBjb21tZW508+ed8YKx57Fll4SF\n" +
            "m+Dl1eaWAQICCAAAyEIAAIA/AABIQwAAAEAAAJZDAABAQAAAyEMAAIBAAA==";

    @Test
    public void testSerialize() throws Exception {

        MsnSpectrum spectrum = new MsnSpectrum(PeakList.Precision.FLOAT);

        spectrum.setId(UUID.fromString("cd4e313b-e8ec-4606-b499-5468fe4f5ef4"));
        spectrum.setPrecursor(new Peak(733.3, 87, -1));
        spectrum.setComment("This is a comment");
        spectrum.setFragMethod("Perseverance");
        spectrum.setParentScanNumber(new ScanNumberDiscrete(4563));
        spectrum.setSpectrumIndex(234);
        spectrum.setSpectrumSource(new URIBuilder("org.expasy", "tester").build());
        spectrum.setMsLevel(2);
        spectrum.addScanNumber(4567);
        spectrum.addRetentionTime(new RetentionTimeDiscrete(872.6, TimeUnit.SECOND));
        spectrum.add(100, 1, new PepFragAnnotation(IonType.b, 1, Peptide.parse("P")));
        spectrum.add(200, 2, new PepFragAnnotation(IonType.b, 1, Peptide.parse("PE")));
        spectrum.add(300, 3, new PepFragAnnotation(IonType.b, 1, Peptide.parse("PEP")));
        spectrum.add(400, 4, new PepFragAnnotation(IonType.b, 1, Peptide.parse("PEPT")));

        MsnSpectrumSerializer serializer = new MsnSpectrumSerializer();

        MockDataOutput out = new MockDataOutput(512);
        serializer.serialize(out, spectrum);

        Assert.assertEquals(base64, out.getBase64());
    }

    @Test
    public void testDeSerialize() throws Exception {

        MockDataInput in = new MockDataInput(base64);

        MsnSpectrumSerializer serializer = new MsnSpectrumSerializer();
        MsnSpectrum spectrum = serializer.deserialize(in, 0);

        Assert.assertEquals(UUID.fromString("cd4e313b-e8ec-4606-b499-5468fe4f5ef4"), spectrum.getId());
        Assert.assertEquals(new Peak(733.3, 87, -1), spectrum.getPrecursor());
        Assert.assertEquals("This is a comment", spectrum.getComment());
        Assert.assertEquals("Perseverance", spectrum.getFragMethod());
        Assert.assertEquals(new ScanNumberDiscrete(4563), spectrum.getParentScanNumber());
        Assert.assertEquals(234, spectrum.getSpectrumIndex());
        Assert.assertEquals(new URIBuilder("org.expasy", "tester").build(), spectrum.getSpectrumSource());
        Assert.assertEquals(2, spectrum.getMsLevel());

        Assert.assertEquals(new ScanNumberList(4567), spectrum.getScanNumbers());
        Assert.assertEquals(new RetentionTimeList(new RetentionTimeDiscrete(872.6, TimeUnit.SECOND)), spectrum.getRetentionTimes());

        Assert.assertEquals(100, spectrum.getMz(0), 0.00000001);
        Assert.assertEquals(1, spectrum.getIntensity(0), 0.00000001);
        Assert.assertEquals(Collections.<PeakAnnotation>emptyList(), spectrum.getAnnotations(0));
        Assert.assertEquals(200, spectrum.getMz(1), 0.00000001);
        Assert.assertEquals(2, spectrum.getIntensity(1), 0.00000001);
        Assert.assertEquals(Collections.<PeakAnnotation>emptyList(), spectrum.getAnnotations(1));
        Assert.assertEquals(300, spectrum.getMz(2), 0.00000001);
        Assert.assertEquals(3, spectrum.getIntensity(2), 0.00000001);
        Assert.assertEquals(Collections.<PeakAnnotation>emptyList(), spectrum.getAnnotations(2));
        Assert.assertEquals(400, spectrum.getMz(3), 0.00000001);
        Assert.assertEquals(4, spectrum.getIntensity(3), 0.00000001);
        Assert.assertEquals(Collections.<PeakAnnotation>emptyList(), spectrum.getAnnotations(3));
    }
}