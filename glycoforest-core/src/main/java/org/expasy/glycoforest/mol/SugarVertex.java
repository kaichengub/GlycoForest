package org.expasy.glycoforest.mol;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarVertex {

    private final int id;
    private final SugarUnit unit;

    public SugarVertex(SugarUnit unit, int id) {

        this.unit = unit;
        this.id = id;
    }

    public SugarUnit getUnit() {

        return unit;
    }

    public int getId() {

        return id;
    }

    @Override
    public String toString() {

        return unit.toString() + " " + id;
    }

    public boolean isUnit(SugarUnit sugarUnit) {

        return unit.equals(sugarUnit);
    }

    public boolean isExpendable() {

        return unit.isExtensible();
    }
}
