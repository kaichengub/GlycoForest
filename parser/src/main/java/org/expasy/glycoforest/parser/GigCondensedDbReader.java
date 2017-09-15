package org.expasy.glycoforest.parser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.expasy.glycoforest.solver.SugarStructureDB;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class GigCondensedDbReader extends GigCondensedReader {

    private SugarStructureDB.Builder dbBuilder;

    public GigCondensedDbReader() {
    }

    public SugarStructureDB readDb(Reader reader) throws IOException {

        dbBuilder = new SugarStructureDB.Builder();

        GigCondensedLexer lexer = new GigCondensedLexer(new ANTLRInputStream(reader));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GigCondensedParser parser = new GigCondensedParser(tokens);
        visitStructureDb(parser.structureDb());

        return dbBuilder.build();
    }

    @Override
    public Void visitStructureDb(GigCondensedParser.StructureDbContext ctx) {

        for(GigCondensedParser.LabeledStructureContext labeledStructure: ctx.labeledStructure()){

            visitLabeledStructure(labeledStructure);
        }

        return null;
    }

    @Override
    public Void visitLabeledStructure(GigCondensedParser.LabeledStructureContext ctx) {

        visitLabel(ctx.label());
        visitStructure(ctx.structure());

        return null;
    }

    @Override
    public Void visitLabel(GigCondensedParser.LabelContext ctx) {

        id = ctx.Id().getText();

        return null;
    }

    @Override
    public Void visitStructure(GigCondensedParser.StructureContext structureCtx) {

        super.visitStructure(structureCtx);
        dbBuilder.add(builder.build());
        builder = null;
        return null;
    }
}
