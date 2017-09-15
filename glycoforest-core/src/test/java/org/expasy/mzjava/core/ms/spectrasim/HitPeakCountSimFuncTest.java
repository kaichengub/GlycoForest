package org.expasy.mzjava.core.ms.spectrasim;

import org.expasy.mzjava.core.ms.AbsoluteTolerance;
import org.expasy.mzjava.core.ms.peaklist.FloatPeakList;
import org.expasy.mzjava.core.ms.peaklist.PeakList;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.glycomics.ms.spectrum.GlycanFragAnnotation;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class HitPeakCountSimFuncTest {

    @Test
    public void testCalcSimilarity() throws Exception {

        HitPeakCountSimFunc simFunc = new HitPeakCountSimFunc(new AbsoluteTolerance(0.3));

        PeakList<GlycanFragAnnotation> plx = new FloatPeakList<>();
        plx.add(161.045547, 1);
        plx.add(179.0561117, 1);
        plx.add(290.0881401, 1);
        plx.add(308.0987048, 1);
        plx.add(366.1405696, 1);
        plx.add(384.1511343, 1);
        plx.add(495.1831627, 1);
        plx.add(513.1937274, 1);

        PeakList<LibPeakAnnotation> ply = new FloatPeakList<>();
        ply.add(161.045547, 1);
        ply.add(179.0561117, 1);
        ply.add(290.0881401, 1);
        ply.add(308.0987048, 1);
        ply.add(366.1405696, 1);
        ply.add(384.1511343, 1);
        ply.add(495.1831627, 1);
        ply.add(513.1937274, 1);

        Assert.assertEquals(1.0, simFunc.calcSimilarity(plx, ply), 0);
    }

    @Test
    public void testCalcSimilarity2() throws Exception {

        HitPeakCountSimFunc simFunc = new HitPeakCountSimFunc(new AbsoluteTolerance(0.3));

        PeakList<GlycanFragAnnotation> plx = new FloatPeakList<>();
        plx.add(161.045547, 1);
        plx.add(179.0561117, 1);
        plx.add(290.0881401, 1);
        plx.add(308.0987048, 1);
        plx.add(366.1405696, 1);
        plx.add(384.1511343, 1);
        plx.add(495.1831627, 1);
        plx.add(513.1937274, 1);

        PeakList<LibPeakAnnotation> ply = new FloatPeakList<>();
        ply.add(161.045547, 1);
        ply.add(290.0881401, 1);
        ply.add(310.0881401, 1);  //noise
        ply.add(366.1405696, 1);
        ply.add(384.1511343, 1);
        ply.add(398.1405696, 1); //noise

        Assert.assertEquals(0.5, simFunc.calcSimilarity(plx, ply), 0);
    }

    @Test
    public void testCalcSimilarity3() throws Exception {

        HitPeakCountSimFunc simFunc = new HitPeakCountSimFunc(new AbsoluteTolerance(0.3));

        PeakList<GlycanFragAnnotation> plx = new FloatPeakList<>();
        plx.add(161.045547, 1);
        plx.add(179.0561117, 1);
        plx.add(290.0881401, 1);
        plx.add(308.0987048, 1);
        plx.add(366.1405696, 1);
        plx.add(384.1511343, 1);
        plx.add(495.1831627, 1);
        plx.add(513.1937274, 1);

        PeakList<LibPeakAnnotation> ply = new FloatPeakList<>();
        ply.add(1161.045547, 1);
        ply.add(1290.0881401, 1);
        ply.add(1366.1405696, 1);
        ply.add(1384.1511343, 1);

        Assert.assertEquals(0.0, simFunc.calcSimilarity(plx, ply), 0);
    }
}