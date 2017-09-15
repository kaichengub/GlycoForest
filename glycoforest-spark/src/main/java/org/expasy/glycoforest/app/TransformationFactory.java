package org.expasy.glycoforest.app;

import com.google.common.collect.Lists;
import org.expasy.glycoforest.mol.*;
import org.expasy.glycoforest.solver.*;

import java.util.List;
import java.util.stream.Stream;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class TransformationFactory {

    public static List<StructureTransformation> newTransformations() {

        return Lists.newArrayList(

                // 1 Fuc
                StructureAddition.noInsertNoTerminal(Fuc),
                // 1 Hex
                StructureAddition.noTerminal(Hex),
                // 1 HexNAc
                StructureAddition.noTerminal(HexNAc),
                // 1 Neu5Ac
                StructureAddition.noInsertExtendSameTerminal(Neu5Ac),
                // 1 Neu5Gc
                StructureAddition.noInsertExtendSameTerminal(Neu5Gc),
                // 1 Kdn
                StructureAddition.noInsertNoTerminal(Kdn),
                // 1 S
                StructureAddition.noInsertNoTerminal(S),

                // -Fuc
                newRemove(Fuc),
                // - Hex
                newRemove(Hex),
                // - HexNAc
                newRemove(HexNAc),
                // - Neu5Ac
                newRemove(Neu5Ac),
                // - Neu5Gc
                newRemove(Neu5Gc),
                // - Kdn
                newRemove(Kdn),
                // - S
                newRemove(S),

                new CompoundStructureTransformation("Fuc > Hex, Neu5Ac > Neu5Gc", Lists.newArrayList(
                        // Fuc > Hex
                        newRemoveAddAndSubstitution(Fuc, Hex),
                        // Neu5Ac > Neu5Gc
                        newRemoveAddAndSubstitution(Neu5Ac, Neu5Gc))
                ),
                new CompoundStructureTransformation("Hex > Fuc, Neu5Ac > Neu5Gc", Lists.newArrayList(
                        // Hex > Fuc
                        newLeafSubstitution(Hex, Fuc),
                        // Neu5Ac > Neu5Gc
                        newRemoveAddAndSubstitution(Neu5Gc, Neu5Ac))
                ),
                new CompoundStructureTransformation("Hex > HexNAc, Kdn > Neu5Ac", Lists.newArrayList(
                        // Hex > HexNAc
                        newRemoveAddAndSubstitution(Hex, HexNAc),
                        // - 1Kdn 1 Neu5Ac
                        newRemoveAddAndSubstitution(Kdn, Neu5Ac))
                ),
                new CompoundStructureTransformation("HexNAc > Hex, Neu5Ac > Kdn", Lists.newArrayList(
                        // Hex > HexNAc
                        newRemoveAddAndSubstitution(HexNAc, Hex),
                        // Kdn > Neu5Ac
                        newRemoveAddAndSubstitution(Neu5Ac, Kdn))
                ),

                new CompoundStructureTransformation("Fuc > HexNAc, Kdn > Neu5Gc", Lists.newArrayList(
                        // Fuc > HexNAc
                        newRemoveAddAndSubstitution(Fuc, HexNAc),
                        // Kdn > Neu5Gc
                        newRemoveAddAndSubstitution(Kdn, Neu5Gc))
                ),
                new CompoundStructureTransformation("HexNAc > Fuc, Neu5Gc > Kdn", Lists.newArrayList(
                        // HexNAc > Fuc
                        newLeafSubstitution(HexNAc, Fuc),
                        // Neu5Gc > Kdn
                        newRemoveAddAndSubstitution(Neu5Gc, Kdn))
                ),

                new CompoundStructureTransformation("Hex > Kdn, HexNAc > Neu5Ac", Lists.newArrayList(
                        // Hex > Kdn
                        newLeafSubstitution(Hex, Kdn),
                        // HexNAc > Neu5Ac
                        newLeafSubstitution(HexNAc, Neu5Ac))
                ),
                new CompoundStructureTransformation("Kdn > Hex, Neu5Ac > HexNAc", Lists.newArrayList(
                        // Kdn > Hex
                        newRemoveAddAndSubstitution(Kdn, Hex),
                        // Neu5Ac > HexNAc
                        newLeafSubstitution(Neu5Ac, HexNAc))
                ),

                new CompoundStructureTransformation("Fuc > Kdn, HexNAc > Neu5Ac", Lists.newArrayList(
                        // Fuc > Kdn
                        newRemoveAddAndSubstitution(Fuc, Kdn),
                        // HexNAc > Neu5Ac
                        newLeafSubstitution(HexNAc, Neu5Gc))
                ),
                new CompoundStructureTransformation("Kdn > Fuc, Neu5Ac > HexNAc", Lists.newArrayList(
                        // Kdn > Fuc
                        newRemoveAddAndSubstitution(Kdn, Fuc),
                        // Neu5Ac > HexNAc
                        newRemoveAddAndSubstitution(Neu5Gc, HexNAc))
                ),
                new CompoundStructureTransformation("Hex > Neu5Gc, Fuc > Neu5Ac", Lists.newArrayList(
                        // Hex > Neu5Gc
                        newLeafSubstitution(Hex, Neu5Gc),
                        // Fuc > Neu5Ac
                        newRemoveAddAndSubstitution(Fuc, Neu5Ac))
                ),
                new CompoundStructureTransformation("Neu5Gc > Hex, Neu5Ac > Fuc", Lists.newArrayList(
                        // Neu5Gc > Hex
                        newRemoveAddAndSubstitution(Neu5Gc, Hex),
                        // Neu5Ac > Fuc
                        newRemoveAddAndSubstitution(Neu5Ac, Fuc))
                ),

                // HexNAc > Kdn
                newLeafSubstitution(HexNAc, Kdn),
                // Kdn > hexNAc
                newRemoveAddAndSubstitution(Kdn, HexNAc),

                // Hex > Neu5Ac
                newLeafSubstitution(Hex, Neu5Ac),
                // Neu5Ac > Hex
                newRemoveAddAndSubstitution(Neu5Ac, Hex),

                // Fuc > Neu5Gc
                newRemoveAddAndSubstitution(Fuc, Neu5Gc),
                // Neu5Gc > Fuc
                newRemoveAddAndSubstitution(Neu5Gc, Fuc)
        );
    }

    protected static StructureSubstitution newLeafSubstitution(SugarUnit src, SugarUnit dest) {

        return new StructureSubstitution(src + " > " + dest,
                new SugarExtension.Builder(src + " > " + dest, dest).setOut().build(),
                dest.getUnitMass() - src.getUnitMass(),
                (v, g) -> v.getUnit().equals(src) && g.outDegreeOf(v) == 0);
    }

    protected static StructureRemoval newRemove(SugarUnit unit) {

        return new StructureRemoval(unit);
    }

    protected static StructureRemoval newRemoveOnlyTerminal(SugarUnit unit) {

        return new StructureRemoval(unit, (s, g) -> g.outDegreeOf(s) == 0);
    }

    protected static StructureTransformation newRemoveAddAndSubstitution(SugarUnit src, SugarUnit dest) {

        final String label = src.toString() + " > " + dest.toString();
        return new CompoundStructureTransformation(label, Lists.newArrayList(

                new StructureSubstitution(src + " > " + dest, new SugarExtension.Builder(src + " > " + dest, dest).setOut().build(), dest.getUnitMass() - src.getUnitMass(),
                        (v, g) -> v.getUnit().equals(src)  && !v.equals(g.getRoot())),
                new ChainedStructureTransformation(
                        new StructureRemoval(src, (v, g) -> g.outDegreeOf(v) == 0 && !v.equals(g.getRoot())),
                        StructureAddition.noInsertExtendSameTerminal(dest)
                )
        ));
    }

    private static StructureTransformation newRemoveAdd(SugarUnit minus, SugarUnit... additions) {

        StructureTransformation[] chain = new StructureTransformation[additions.length + 1];
        chain[0] = newRemove(minus);
        System.arraycopy(getStructureAdditions(additions), 0, chain, 1, additions.length);

        return new ChainedStructureTransformation(chain);
    }

    private static StructureTransformation newAdd(SugarUnit... units) {

        final StructureAddition[] additions = getStructureAdditions(units);

        return new ChainedStructureTransformation(additions);
    }

    private static StructureAddition[] getStructureAdditions(SugarUnit... units) {

        final StructureAddition[] additions = new StructureAddition[units.length];
        for (int i = 0; i < additions.length; i++) {

            final SugarUnit unit = units[i];
            if (unit.isTerminating()) {
                additions[i] = StructureAddition.noInsertExtendSameTerminal(unit);
            } else {
                additions[i] = StructureAddition.noTerminal(unit);
            }
        }
        return additions;
    }

    public static void main(String[] args) {

//        TransformationFactory.newTransformations().
//                forEach(System.out::println);

//        final List<SugarUnit> values = new ArrayList<>(EnumSet.range(SugarUnit.Hex, SugarUnit.Kdn));
//        for(SugarUnit src : values) {
//
//            for(SugarUnit dest : values){
//
//                if(!src.equals(dest)) {
//
//                    System.out.println(dest.getUnitMass() - src.getUnitMass() + "\t" + src + " > " + dest);                      //sout
//                }
//            }
//        }

        Stream.of(SugarUnit.values())
                .sorted((u1, u2) -> Double.compare(u1.getUnitMass(), u2.getUnitMass()))
                .forEach(sugar -> {

                    System.out.println("'+ " + sugar + "\t" + sugar.getUnitMass());                      //sout
                    System.out.println("'- " + sugar + "\t" + -sugar.getUnitMass());                      //sout
                });
    }
}
