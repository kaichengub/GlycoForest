package org.expasy.glycoforest.app;

import org.expasy.glycoforest.app.gig.fish_mucin.FishClusterSource;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakCursor;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class ChargeStatePredictor {

    public static void main(String[] args) throws IOException {

        final ClusterSource<LibPeakAnnotation, BetweenRunConsensus> source = new FishClusterSource();

        final Map<String, BetweenRunConsensus> map = source.readLabelClusterMap(new PeakProcessorChain<>());
        final List<String> ids = new ArrayList<>(map.keySet());
        Collections.sort(ids);
        final List<Record> records = new ArrayList<>();
        for(String id : ids){

            final BetweenRunConsensus consensus = map.get(id);

            final Peak precursor = consensus.getPrecursor();

            double sum = 0;
            final PeakCursor<LibPeakAnnotation> cursor = consensus.cursor();
            cursor.movePast(precursor.getMz() + 3);
            while (cursor.next()) {

                sum += cursor.currIntensity();
            }

            final double fraction = sum / consensus.getTotalIonCurrent();
            records.add(new Record(fraction, id, precursor));
        }

        Collections.sort(records, (r1, r2) -> Double.compare(r2.fraction, r1.fraction));
        for(Record record : records) {

            System.out.println(record.id + "\t" + record.precursor + "\t" + (record.fraction > 0.15 ? "-2" : "-1") + "\t" + NumberFormat.getNumberInstance().format(record.fraction));                      //sout
        }
    }

    private static class Record {

        private final double fraction;
        private final String id;
        private final Peak precursor;

        public Record(double fraction, String id, Peak precursor) {

            this.fraction = fraction;
            this.id = id;
            this.precursor = precursor;
        }
    }
}