package org.expasy.glycoforest.app.evaluator;

import java.util.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class RunMap {

    private RunMap() {}

    public static Map<UUID, String> getGastricRunMap(){

        final Map<UUID, String> gastricRuns = new HashMap<>();
        gastricRuns.put(UUID.fromString("37715776-80a9-4081-9ef1-93819019ae0e"), "10062011_ES5");
        gastricRuns.put(UUID.fromString("b1ae30c4-9108-4b10-a869-7550f94a86e1"), "100929_es1");
        gastricRuns.put(UUID.fromString("08584f03-caf6-4978-baab-62ad777efe48"), "100929_es10");
        gastricRuns.put(UUID.fromString("61d7d746-ba5a-431a-a664-fef92fbc226a"), "100929_es2");
        gastricRuns.put(UUID.fromString("c23028ad-b349-4cf9-ad81-4ca3556fefdb"), "100929_es3");
        gastricRuns.put(UUID.fromString("538b7170-52ac-4f50-8368-128197e3b527"), "100929_es4");
        gastricRuns.put(UUID.fromString("f46165b5-9089-436a-b8f1-d2815a71a7fe"), "100929_es7");
        gastricRuns.put(UUID.fromString("e7b56400-e153-43b9-b39f-50d799185f34"), "100929_es8");
        gastricRuns.put(UUID.fromString("950ae626-0ab1-44f9-a5ab-7231de8646d4"), "100929_es9");
        gastricRuns.put(UUID.fromString("d6086b8e-fa41-4c08-8002-c54aaf9a87f7"), "101215_es11");
        gastricRuns.put(UUID.fromString("932d7ba5-ce3a-4ad4-aa44-18f7b4b82ecc"), "101215_es12");
        gastricRuns.put(UUID.fromString("d1f341b4-3090-478a-a787-9704b97ab605"), "101215_es17");
        gastricRuns.put(UUID.fromString("09d4de67-d996-4dcc-bf49-4b6f46ef9aad"), "101216_es_13");
        gastricRuns.put(UUID.fromString("f0a05a1b-af39-49ce-bafc-573b8e99148e"), "111116es_14");
        gastricRuns.put(UUID.fromString("2467c139-2bb0-4bd8-bbe9-430e96b3372b"), "111116es_15");
        gastricRuns.put(UUID.fromString("0663044c-8893-4ccc-ad38-6dc6e165623f"), "111116es_6");
        return gastricRuns;
    }



    public static Map<UUID, String> getFishRunMap() {

        final Map<UUID, String> fishRuns = new HashMap<>();
        fishRuns.put(UUID.fromString("728f4b78-0521-4b51-b6a3-c60df82dca97"), "JC_131209FMDc1");
        fishRuns.put(UUID.fromString("4bb1bdb5-1e77-4f79-95a9-2fe9c9959c42"), "JC_131209FMDc2");
        fishRuns.put(UUID.fromString("953615d3-b7ee-4294-b33b-61b33cb8f997"), "JC_131209FMDc3");
        fishRuns.put(UUID.fromString("70b08d71-3f2d-465b-beeb-d7fca6f5f19c"), "JC_131209FMS1");
        fishRuns.put(UUID.fromString("c920ea15-c88f-4acb-b3c4-dd3a739ba371"), "JC_131209FMS2");
        fishRuns.put(UUID.fromString("6137d5f6-797d-4e55-8908-3a8020832155"), "JC_131209FMS3");
        fishRuns.put(UUID.fromString("08bf7f1f-06ef-426a-b8a9-719908335eea"), "JC_131209FMS4");
        fishRuns.put(UUID.fromString("8dc0c77d-0754-4092-8c50-66d36c86afc7"), "JC_131209FMS5");
        fishRuns.put(UUID.fromString("9011c058-6dcc-4c9b-8364-60baac29daf0"), "JC_131210FMDc4");
        fishRuns.put(UUID.fromString("6544dd5c-22ea-436d-a8a5-18a4dec3e629"), "JC_131210FMDc5");
        fishRuns.put(UUID.fromString("e469cd48-cc8a-4874-b84b-ee54c43f1dc9"), "JC_131210FMpx1");
        fishRuns.put(UUID.fromString("f103792a-5405-4e47-a79c-a288e93ad291"), "JC_131210FMpx2");
        fishRuns.put(UUID.fromString("9037034f-3c8e-4616-b2a6-b59255d7b305"), "JC_131210PMpc1");
        fishRuns.put(UUID.fromString("2f629563-5de2-4395-885a-28ee0a4c9b4f"), "JC_131210PMpc2");
        fishRuns.put(UUID.fromString("6074e664-d6c8-4074-93d8-11b599ba0d6e"), "JC_131210PMpc3");
        fishRuns.put(UUID.fromString("ace756ea-879d-41f7-a20d-c90b82abd237"), "JC_131210PMpc4");
        fishRuns.put(UUID.fromString("0de86d47-b1a5-40cf-a8bb-b0971bb51733"), "JC_131210PMpc5");
        fishRuns.put(UUID.fromString("f87bb257-67ae-482f-918d-a8310de80d7e"), "JC_131210PMpx3");
        fishRuns.put(UUID.fromString("5bedd92f-55bb-4fe5-9b41-f22f6e6d99bc"), "JC_131210PMpx4");
        fishRuns.put(UUID.fromString("500941d0-d2ff-41c3-9c73-ca0a9583091b"), "JC_131210PMpx5");
        return fishRuns;
    }

    public static Set<UUID> getGastricRunIds() {

        return getGastricRunMap().keySet();
    }

    public static Set<UUID> getFishRunIds() {

        return getFishRunMap().keySet();
    }

    public static Set<UUID> getAllRunIds() {

        final Set<UUID> set = new HashSet<>(getGastricRunIds());
        set.addAll(getFishRunIds());
        return set;
    }
}
