package org.expasy.mzjava.tools.glycoforest.graphdb.hstore;

import org.expasy.mzjava.tools.glycoforest.graphdb.RunNode;

import java.util.Objects;
import java.util.UUID;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class HRunNode implements RunNode {

    private final UUID id;
    private final String name;

    public HRunNode(UUID id, String name) {

        this.id = id;
        this.name = name;
    }

    @Override
    public UUID getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HRunNode that = (HRunNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {

        return "HRunNode{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
