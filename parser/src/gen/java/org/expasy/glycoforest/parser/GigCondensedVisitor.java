// Generated from C:/Users/ohorlach/Documents/IdeaProjects/glycoforest-spark/parser/src/main/java/org/expasy/glycoforest/parser\GigCondensed.g4 by ANTLR 4.5.1
package org.expasy.glycoforest.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GigCondensedParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GigCondensedVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GigCondensedParser#structureDb}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructureDb(GigCondensedParser.StructureDbContext ctx);
	/**
	 * Visit a parse tree produced by {@link GigCondensedParser#labeledStructure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabeledStructure(GigCondensedParser.LabeledStructureContext ctx);
	/**
	 * Visit a parse tree produced by {@link GigCondensedParser#structure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructure(GigCondensedParser.StructureContext ctx);
	/**
	 * Visit a parse tree produced by {@link GigCondensedParser#extension}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtension(GigCondensedParser.ExtensionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GigCondensedParser#subTree}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubTree(GigCondensedParser.SubTreeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GigCondensedParser#linkedUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLinkedUnit(GigCondensedParser.LinkedUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link GigCondensedParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(GigCondensedParser.LabelContext ctx);
}