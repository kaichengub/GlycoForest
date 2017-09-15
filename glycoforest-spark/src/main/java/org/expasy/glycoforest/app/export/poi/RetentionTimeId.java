package org.expasy.glycoforest.app.export.poi;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
class RetentionTimeId {

    private final String runName;
    private final String startRt;
    private final String endRt;

    public RetentionTimeId(final String runName, String startRt, String endRt) {

        this.runName = runName;

        this.startRt = startRt;
        this.endRt = endRt;
    }

    public String getRunName() {

        return runName;
    }

    @Override
    public int hashCode() {

        return com.google.common.base.Objects.hashCode(runName, startRt, endRt);
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final RetentionTimeId other = (RetentionTimeId) obj;
        return com.google.common.base.Objects.equal(this.runName, other.runName)
                && com.google.common.base.Objects.equal(this.startRt, other.startRt)
                && com.google.common.base.Objects.equal(this.endRt, other.endRt);
    }
}
