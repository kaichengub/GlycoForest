package org.expasy.glycoforest.solver;

import org.expasy.glycoforest.mol.IsomorphismType;
import org.expasy.glycoforest.mol.SugarStructure;

import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class StructureTransformation {

    protected final String name;
    private final double massShift;

    public StructureTransformation(String name, double massShift) {

        this.name = name;
        this.massShift = massShift;
    }

    public double massShift(){

        return massShift;
    }

    public abstract List<SugarStructure> generateCandidates(SugarStructure src, boolean allowDuplicates);

    public String getName(){

        return name;
    }

    protected void doAdd(List<SugarStructure> candidates, List<SugarStructure> allCandidates, boolean allowDuplicates) {

        if(allowDuplicates)
            allCandidates.addAll(candidates);
        else {

            for(SugarStructure candidate : candidates) {

                boolean duplicate = false;
                for(SugarStructure accepted : allCandidates) {

                    if(accepted.isIsomorphic(candidate, IsomorphismType.TOPOLOGY)) {

                        duplicate = true;
                        break;
                    }
                }
                if(!duplicate)
                    allCandidates.add(candidate);
            }
        }
    }

    @Override
    public String toString() {

        return name;
    }
}
