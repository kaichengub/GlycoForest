package org.expasy.glycoforest.app.evaluator;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.solver.SpectrumAnnotation;
import org.expasy.glycoforest.writer.GigCondensedWriter;
import org.expasy.mzjava.core.ms.Tolerance;
import org.expasy.mzjava.stats.FrequencyTable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SolvingEvaluator {

    private final IsomorphismType isomorphismType;
    private final Tolerance fragmentTolerance;
    private final Set<String> excludes;

    public SolvingEvaluator(IsomorphismType isomorphismType, Tolerance fragmentTolerance, final Set<String> excludes) {

        this.isomorphismType = isomorphismType;
        this.fragmentTolerance = fragmentTolerance;
        this.excludes = excludes;
    }

    private Stream<GsmResultList> makeAnnotatedResultListStream(final List<GsmResultList> results) {

        return results.stream()
                .sorted((r1, r2) -> Double.compare(r2.getMetaScore(), r1.getMetaScore()))
                .filter(resultList -> resultList.getSolveTask().getVertex().isAnnotated())
                .filter(resultList -> resultList.size() >= 1)
                .filter(resultList -> !containsExcluded(resultList));
    }

    private boolean containsExcluded(final GsmResultList gsmResultList) {

        final long count = gsmResultList.getSolveTask().getVertex().structureLabelStream()
                .filter(excludes::contains)
                .count();

        return count > 0;
    }

    public void reportHitPosition(List<GsmResultList> results, Predicate<GsmResultList> resultListPredicate) {

        final TIntIntMap counterMap = new TIntIntHashMap();
        final Map<Integer, FrequencyTable> frequencyTableMap = new HashMap<>();
        final double binSize = 0.05;
        makeAnnotatedResultListStream(results)
                .filter(resultListPredicate)
                .forEach(resultList -> {

                    final int correctIndex = resultList.getCorrectIndex(isomorphismType);
                    frequencyTableMap.computeIfAbsent(correctIndex == -1 ? 100 : correctIndex, missing -> new FrequencyTable(binSize, (missing + 1) + "th"))
                            .add(resultList.getMetaScore());
                    counterMap.adjustOrPutValue(correctIndex, 1, 1);
                });

        final int[] keys = counterMap.keys();
        Arrays.sort(keys);
        IntStream.of(keys).forEach(key -> {
            System.out.println(key + "\t" + counterMap.get(key));                      //sout
        });
        System.out.println();                      //sout

        reportFrequencies(frequencyTableMap);
    }

    public void reportMissingFragments(List<GsmResultList> results, Predicate<GsmResultList> resultListPredicate) {

        final TIntIntMap counterMap = new TIntIntHashMap();
        final Map<Integer, FrequencyTable> frequencyTableMap = new HashMap<>();
        final double binSize = 0.05;
        makeAnnotatedResultListStream(results)
                .filter(resultListPredicate)
                .forEach(resultList -> {

                    final int missingFragments = resultList.countMissingFragments(isomorphismType, fragmentTolerance);
                    if (missingFragments == 0) {
                        GigCondensedWriter writer = new GigCondensedWriter();
                        final String actual = writer.write(resultList.get(0).getHit().getKey());
                        final List<String> expected = resultList.getAnnotationStream().map(SpectrumAnnotation::getStructure).map(writer::write).collect(Collectors.toList());
                        int i = 1;
                    }
                    frequencyTableMap.computeIfAbsent(missingFragments, missing -> new FrequencyTable(binSize, "\u0394 " + missing))
                            .add(resultList.getMetaScore());
                    counterMap.adjustOrPutValue(missingFragments, 1, 1);
                });

        final int[] keys = counterMap.keys();
        Arrays.sort(keys);
        IntStream.of(keys).forEach(key -> {
            System.out.println(key + "\t" + counterMap.get(key));                      //sout
        });
        System.out.println();                      //sout

        reportFrequencies(frequencyTableMap);
    }

    private void reportFrequencies(final Map<Integer, FrequencyTable> frequencyTableMap) {

        final List<Integer> tableKeys = new ArrayList<>(frequencyTableMap.keySet());
        Collections.sort(tableKeys);

        final FrequencyTable[] tables = new FrequencyTable[tableKeys.size()];
        for (int i = 0; i < tableKeys.size(); i++) {
            final int key = tableKeys.get(i);

            tables[i] = frequencyTableMap.get(key);
        }
        System.out.println(FrequencyTable.toStringNotNormalize("score", tables));                      //sout
        System.out.println();                      //sout
    }

    public void reportComposition(List<GsmResultList> results, Predicate<GsmResultList> resultListPredicate) {

        final double binSize = 0.05;
        final FrequencyTable tableCorrect = new FrequencyTable(binSize, "correct");
        final FrequencyTable tableWrong = new FrequencyTable(binSize, "wrong");
        makeAnnotatedResultListStream(results)
                .filter(resultListPredicate)
                .forEach(resultList -> {

                    if (resultList.isBestCompositionCorrect())
                        tableCorrect.add(resultList.getMetaScore());
                    else
                        tableWrong.add(resultList.getMetaScore());
                });

        System.out.println(FrequencyTable.toStringNotNormalize("count", tableCorrect, tableWrong));                      //sout
        System.out.println();                      //sout
    }

}
