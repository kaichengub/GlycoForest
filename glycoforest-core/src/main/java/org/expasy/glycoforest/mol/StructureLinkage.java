package org.expasy.glycoforest.mol;

import org.expasy.mzjava.glycomics.mol.Anomericity;

import java.util.Optional;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class StructureLinkage {

    private final Optional<Anomericity> anomericity;
    private final Optional<Integer> anomericCarbon;
    private final Optional<Integer> linkedCarbon;

    public StructureLinkage() {

        this(Optional.empty(), Optional.empty(), Optional.empty());
    }

    public StructureLinkage(Anomericity anomericity, Integer anomericCarbon, Integer linkedCarbon) {

        this(Optional.ofNullable(anomericity), Optional.ofNullable(anomericCarbon), Optional.ofNullable(linkedCarbon));
    }

    public StructureLinkage(Optional<Anomericity> anomericity, Optional<Integer> anomericCarbon, Optional<Integer> linkedCarbon) {

        this.anomericity = anomericity;
        this.anomericCarbon = anomericCarbon;
        this.linkedCarbon = linkedCarbon;
    }

    public Optional<Anomericity> getAnomericity() {

        return anomericity;
    }

    public Optional<Integer> getAnomericCarbon() {

        return anomericCarbon;
    }

    public Optional<Integer> getLinkedCarbon() {

        return linkedCarbon;
    }

    public boolean isDefined() {

        return anomericity.isPresent() || anomericCarbon.isPresent() || linkedCarbon.isPresent();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(anomericityToString());
        if(anomericCarbon.isPresent()){
            builder.append(anomericCarbon.get());
        }
        builder.append("-");
        if(linkedCarbon.isPresent()){
            builder.append(linkedCarbon.get());
        }

        return builder.toString();
    }

    private String anomericityToString() {

        if (anomericity.isPresent()) {

            switch (anomericity.get()) {

                case alpha:
                    return "a";
                case beta:
                    return "b";
                case open:
                    return "o";
                default:
                    return "?";
            }
        } else {

            return "";
        }
    }
}
