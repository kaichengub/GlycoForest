package org.expasy.glycoforest.app.gig.fish_mucin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.parser.GigCondensedReader;
import org.expasy.glycoforest.app.ClusterSource;
import org.expasy.glycoforest.app.chargestate.ChargeStateEstimator;
import org.expasy.glycoforest.app.chargestate.IonCurrentChargeStateEstimator;
import org.expasy.glycoforest.app.data.StructureQuantEntry;
import org.expasy.mzjava.core.ms.peaklist.Peak;
import org.expasy.mzjava.core.ms.peaklist.PeakProcessorChain;
import org.expasy.mzjava.core.ms.spectrum.LibPeakAnnotation;
import org.expasy.mzjava.tools.glycoforest.ms.spectrum.BetweenRunConsensus;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class FishClusterSource extends ClusterSource<LibPeakAnnotation, BetweenRunConsensus> {

    private final Map<UUID, String> idMap;
    private final Map<UUID, BetweenRunConsensus> betweenRunConsensusMap;

    public FishClusterSource() {

        try {

            final FileReader fileReader = new FileReader("C:\\Users\\ohorlach\\Documents\\IdeaProjects\\glycoforest-spark\\glycoforest-scratch\\src\\main\\resources\\org\\expasy\\glycoforest\\scratch\\fish_mucin\\structure_id_cluster_id_map.json");
            idMap = new ObjectMapper().<Map<String, String>>readValue(fileReader, new TypeReference<Map<String, String>>() {
            }).entrySet().stream()
                    .collect(Collectors.toMap(entry -> UUID.fromString(entry.getValue()), Map.Entry::getKey));
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }

        File dbRoot = new File("C:\\Users\\ohorlach\\Documents\\tmp\\fish_mucin\\clusters");
        DB mapDb = DBMaker.newFileDB(new File(dbRoot, "mapDb/spectra"))
                .closeOnJvmShutdown()
                .mmapFileEnable()
                .commitFileSyncDisable()
                .transactionDisable()
                .make();

        ChargeStateEstimator chargeStateEstimator = new IonCurrentChargeStateEstimator(0.15);
        betweenRunConsensusMap = new HashMap<>();
        for(Map.Entry<UUID, BetweenRunConsensus> entry : mapDb.<UUID, BetweenRunConsensus>getHashMap("between_run_consensus").entrySet()){

            BetweenRunConsensus consensus = entry.getValue();
            final Peak precursor = consensus.getPrecursor();
            precursor.setMzAndCharge(precursor.getMz(), chargeStateEstimator.estimateChargeState(consensus));
            betweenRunConsensusMap.put(entry.getKey(), consensus);
        }
    }

    @Override
    public Map<BetweenRunConsensus, String> readClusterLabelMap(PeakProcessorChain<LibPeakAnnotation> processorChain) throws IOException {

        return idMap.entrySet().stream().collect(Collectors.toMap(entry -> betweenRunConsensusMap.get(entry.getKey()), Map.Entry::getValue));
    }

    @Override
    public Map<String, BetweenRunConsensus> readLabelClusterMap(PeakProcessorChain<LibPeakAnnotation> processorChain) throws IOException {

        return idMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, entry -> betweenRunConsensusMap.get(entry.getKey())));
    }

    @Override
    public Map<String, SugarStructure> readCorrectStructures() {

        try {
            final GigCondensedReader reader = new GigCondensedReader();
            final FileReader fileReader = new FileReader("C:\\Users\\ohorlach\\Documents\\IdeaProjects\\glycoforest-spark\\glycoforest-scratch\\src\\main\\resources\\org\\expasy\\glycoforest\\scratch\\fish_mucin\\fish_mucin_structures.json");
            final List<StructureQuantEntry> entries = new ObjectMapper().readValue(fileReader, new TypeReference<List<StructureQuantEntry>>() {
            });
            return entries.stream()
                    .filter(entry -> entry.getStructure().indexOf('+') == -1)
                    .collect(Collectors.toMap(StructureQuantEntry::getName, entry -> reader.readStructure(entry.getName(), entry.getStructure())));
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }
    }
}
