package org.expasy.glycoforest.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.expasy.glycoforest.mol.SugarStructure;
import org.expasy.glycoforest.mol.SugarUnit;
import org.expasy.mzjava.glycomics.mol.Anomericity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.expasy.glycoforest.mol.SugarUnit.*;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GigCondensedReader extends GigCondensedBaseVisitor<Void> {

    final Map<String, SugarUnit> conversionMap = new HashMap<>();

    protected SugarStructure.Builder builder;
    protected String id;

    public GigCondensedReader() {

        conversionMap.put("Fuc", Fuc);
        conversionMap.put("Gal", Hex);
        conversionMap.put("Glc", Hex);
        conversionMap.put("Hex", Hex);
        conversionMap.put("HexNAc", HexNAc);
        conversionMap.put("GlcNAc", HexNAc);
        conversionMap.put("GalNAc", HexNAc);
        conversionMap.put("GalNAcol", HexNAc);
        conversionMap.put("GlcNAcol", HexNAc);
        conversionMap.put("HexNAcol", HexNAc);
        conversionMap.put("Man", Hex);
        conversionMap.put("Kdn", Kdn);
        conversionMap.put("NeuAc", Neu5Ac);
        conversionMap.put("NeuGc", Neu5Gc);
        conversionMap.put("Neu5Ac", Neu5Ac);
        conversionMap.put("Neu5Gc", Neu5Gc);
        conversionMap.put("S", S);
        conversionMap.put("Xyl", Xyl);
    }

    public SugarStructure readStructure(String id, String structure) {

        this.id = id;

        GigCondensedLexer lexer = new GigCondensedLexer(new ANTLRInputStream(structure));
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {

                throw new IllegalStateException("could not read " + structure + " line " + line + ":" + charPositionInLine + " " + msg);
            }
        });
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GigCondensedParser parser = new GigCondensedParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        final GigCondensedParser.StructureContext context = parser.structure();

        try {

            visitStructure(context);
        } catch (Exception e) {

            throw new IllegalStateException("Error while parsing " + structure, e);
        }
        return builder.build();
    }

    @Override
    public Void visitStructure(GigCondensedParser.StructureContext structureCtx) {

        checkException(structureCtx);

        List<ParserRuleContext> row = structureCtx.subTree().getRuleContexts(ParserRuleContext.class);
        int index = row.size() - 1;
        builder = new SugarStructure.Builder(id, extractSugar((GigCondensedParser.LinkedUnitContext) row.get(index)));
        index--;
        for (; index >= 0; index--) {

            ParserRuleContext ctx = row.get(index);
            checkException(ctx);
            process(ctx);
        }
        return null;
    }

    @Override
    public Void visitSubTree(GigCondensedParser.SubTreeContext subTree) {

        final List<ParserRuleContext> row = subTree.getRuleContexts(ParserRuleContext.class);
        for (int index = row.size() - 1; index >= 0; index--) {

            ParserRuleContext ctx = row.get(index);
            checkException(ctx);
            process(ctx);
        }
        return null;
    }

    private void process(ParserRuleContext ctx) {

        if (ctx instanceof GigCondensedParser.LinkedUnitContext) {

            final GigCondensedParser.LinkedUnitContext linkedUnitContext = (GigCondensedParser.LinkedUnitContext) ctx;
            final GigCondensedParser.LinkContext link = linkedUnitContext.link();
            if (link == null) {

                builder.add(extractSugar(linkedUnitContext));
            } else {

                builder.add(extractSugar(linkedUnitContext), extractAnomericity(link), extractInt(link, 0), extractInt(link, 1));
            }
        } else if (ctx instanceof GigCondensedParser.SubTreeContext) {

            builder.branch();
            visitSubTree((GigCondensedParser.SubTreeContext) ctx);
            builder.pop();
        } else {

            throw new IllegalStateException("Cannot parse context " + ctx.getClass());
        }
    }

    private Optional<Anomericity> extractAnomericity(GigCondensedParser.LinkContext link) {

        TerminalNode anomericityNode = link.Anomericity();

        if(anomericityNode == null)
            return Optional.empty();

        final String anomericityString = anomericityNode.getText();
        final Anomericity anomericity;
        if("a".equals(anomericityString) || "\u03B1".equals(anomericityString))
            anomericity = Anomericity.alpha;
        else if("b".equals(anomericityString) || "\u03B2".equals(anomericityString))
            anomericity = Anomericity.beta;
        else
            throw new IllegalStateException("Cannot convert " + anomericityString + " to anomericity");

        return Optional.of(anomericity);
    }

    private Optional<Integer> extractInt(GigCondensedParser.LinkContext link, int index) {

        TerminalNode numberNode = link.Number(index);

        return numberNode == null ? Optional.empty() : Optional.of(Integer.parseInt(numberNode.getText()));
    }

    private void checkException(ParserRuleContext context){

        final RecognitionException exception = context.exception;
        if(exception != null) {

            throw exception;
        }
    }

    private SugarUnit extractSugar(GigCondensedParser.LinkedUnitContext context) {

        final String text = context.Unit().getText();
        final SugarUnit sugarUnit = conversionMap.get(text);
        if(sugarUnit == null)
            throw new IllegalStateException("Could not convert " + text);
        return sugarUnit;
    }
}
