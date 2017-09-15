package org.expasy.glycoforest.mol;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SugarExtension extends AbstractSugarStructure {

    private SugarVertex out;

    private SugarExtension(String  label, SugarVertex root) {

        super(root);
    }

    public SugarVertex getOut() {

        return out;
    }

    public static class Builder extends AbstractSugarStructure.AbstractBuilder<Builder, SugarExtension> {

        private SugarVertex out = null;

        public Builder(String id, SugarUnit root) {

            super(root, (rootVertex) -> new SugarExtension(id, rootVertex));
        }

        @Override
        protected Builder thisReference() {

            return this;
        }

        public Builder setOut(){

            out = getHead();
            return this;
        }

        public SugarExtension build(){

            if(out == null)
                throw new IllegalStateException("A SugarExtension needs to have a sugar that is tagged as the out node. To do so use the setOut method while building");

            final SugarExtension sugarExtension = doBuild();
            sugarExtension.out = out;
            return sugarExtension;
        }
    }
}
