package org.expasy.glycoforest.writer;

import org.expasy.glycoforest.mol.*;

import java.util.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public abstract class AbstractIupacWriter {

    public String write(AbstractSugarStructure sugarStructure) {

        final ArrayList<String> tokens = new ArrayList<>();
        final SugarVertex root = sugarStructure.getRoot();
        final Set<SugarVertex> branchedVertex = new HashSet<>();

        tokens.add(0, formatRoot(root.getUnit()));

        sugarStructure.dfs(root, (parent, child, linkage) -> {

            if (!parent.isPresent())
                return;

            int numberOutgoingEdges = sugarStructure.outgoingEdgesOf(parent.get()).size();

            if (numberOutgoingEdges == 2 && !branchedVertex.contains(parent.get())) {

                branchedVertex.add(parent.get());
                tokens.add(getOpeningBracket());
                tokens.add(formatLinkage(linkage, sugarStructure));

            } else if (branchedVertex.contains(parent.get())) {

                tokens.add(getClosingBracket());
                tokens.add(formatLinkage(linkage, sugarStructure));
            } else {

                tokens.add(formatLinkage(linkage, sugarStructure));
            }

            tokens.add(formatSugarUnit(child.getUnit()));

        });


        Collections.reverse(tokens);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i <= tokens.size() - 1; i++) {

            String token = tokens.get(i);

            if (token.equals("-") && (tokens.get(i + 1).equals(getClosingBracket()) || tokens.get(i + 1).equals(getOpeningBracket()))) {
                token = "";
            }

            stringBuilder.append(token);
        }

        return stringBuilder.toString();
    }

    protected abstract String getOpeningBracket();

    protected abstract String getClosingBracket();

    protected abstract String formatLinkage(Optional<StructureLinkage> linkage, AbstractSugarStructure structure);

    protected abstract String formatSugarUnit(SugarUnit sugarUnit);

    protected abstract String formatRoot(SugarUnit sugarUnit);
}
