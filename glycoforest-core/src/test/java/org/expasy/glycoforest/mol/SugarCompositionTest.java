package org.expasy.glycoforest.mol;

import com.google.common.collect.Lists;
import org.expasy.mzjava.core.mol.Composition;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarCompositionTest {

    @Test
    public void add() throws Exception {

        final SugarComposition result = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, Fuc)
                .add(Neu5Ac);

        Assert.assertEquals(3, result.getCount(HexNAc));
        Assert.assertEquals(1, result.getCount(Hex));
        Assert.assertEquals(1, result.getCount(Fuc));
        Assert.assertEquals(1, result.getCount(Neu5Ac));
        Assert.assertEquals(1208.4442663559998, result.getMass(), 0.0);
    }

    @Test
    public void add2() throws Exception {

        final SugarComposition result = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, Fuc)
                .add(Fuc);

        Assert.assertEquals(3, result.getCount(HexNAc));
        Assert.assertEquals(1, result.getCount(Hex));
        Assert.assertEquals(2, result.getCount(Fuc));
        Assert.assertEquals(0, result.getCount(Neu5Ac));
        Assert.assertEquals(1063.406758639, result.getMass(), 0.0);
    }

    @Test
    public void testMinus() throws Exception {

        SugarComposition composition1 = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, Fuc);
        SugarComposition composition2 = new SugarComposition(HexNAc, HexNAc, HexNAc, HexNAc, Hex, Hex);

        SugarComposition result = composition2.minus(composition1);
        Assert.assertEquals(1, result.getCount(HexNAc));
        Assert.assertEquals(1, result.getCount(Hex));
        Assert.assertEquals(-1, result.getCount(Fuc));
        Assert.assertEquals(0, result.getCount(Neu5Ac));
    }

    @Test
    public void testMinus2() throws Exception {

        SugarComposition composition1 = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, Fuc);
        SugarComposition composition2 = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, HexNAc);

        SugarComposition result = composition2.minus(composition1);
        Assert.assertEquals(1, result.getCount(HexNAc));
        Assert.assertEquals(0, result.getCount(Hex));
        Assert.assertEquals(-1, result.getCount(Fuc));
        Assert.assertEquals(0, result.getCount(Neu5Ac));

        Assert.assertEquals("{-1 Fuc, 1 HexNAc}", result.toString());
        Assert.assertEquals(-Fuc.getUnitMass() + HexNAc.getUnitMass(), result.getMass(), 0.0);
    }

    @Test
    public void testToString() throws Exception {

        SugarComposition composition1 = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, Fuc);
        String string = composition1.toString();
        Assert.assertEquals("{1 Hex, 1 Fuc, 3 HexNAc}", string);
    }

    @Test
    public void testNegate() throws Exception {

        SugarComposition composition = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, Fuc).negate();
        Assert.assertEquals(-3, composition.getCount(HexNAc));
        Assert.assertEquals(-1, composition.getCount(Hex));
        Assert.assertEquals(-1, composition.getCount(Fuc));
    }

    @Test
    public void testMinusNegative() throws Exception {

        SugarComposition minusHexNac = new SugarComposition(HexNAc).negate();

        SugarComposition result = new SugarComposition(Fuc).minus(minusHexNac);
        Assert.assertEquals(1, result.getCount(Fuc));
        Assert.assertEquals(1, result.getCount(HexNAc));
    }

    @Test
    public void testMonomerStream() throws Exception {

        final SugarComposition composition = new SugarComposition(HexNAc, HexNAc, HexNAc, Hex, Fuc);

        final List<SugarUnit> monomers = composition.monomerStream().sorted().collect(Collectors.toList());

        Assert.assertEquals(Lists.newArrayList(Hex, Fuc, HexNAc, HexNAc, HexNAc), monomers);
    }

    @Test
    public void testToAtomComposition() throws Exception {

        final SugarComposition composition = new SugarComposition(Hex, Fuc);

        final Composition expected = new Composition.Builder()
                .addAll(Hex.getComposition())
                .addAll(Fuc.getComposition())
                .build();

        Assert.assertEquals(expected, composition.toAtomComposition());
    }
}