// Generated from C:/Users/Oliver/Documents/IdeaProjects/glycoforest-spark/parser/src/main/java/org/expasy/glycoforest/parser\GigCondensed.g4 by ANTLR 4.5.3
package org.expasy.glycoforest.parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GigCondensedParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, Id=3, Anomericity=4, Unit=5, Number=6, OpenBracket=7, 
		CloseBracket=8, LineBreak=9, Sepparator=10, WS=11;
	public static final int
		RULE_structureDb = 0, RULE_labeledStructure = 1, RULE_structure = 2, RULE_extension = 3, 
		RULE_subTree = 4, RULE_linkedUnit = 5, RULE_label = 6, RULE_link = 7;
	public static final String[] ruleNames = {
		"structureDb", "labeledStructure", "structure", "extension", "subTree", 
		"linkedUnit", "label", "link"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'+'", "'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "Id", "Anomericity", "Unit", "Number", "OpenBracket", 
		"CloseBracket", "LineBreak", "Sepparator", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "GigCondensed.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GigCondensedParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StructureDbContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(GigCondensedParser.EOF, 0); }
		public List<LabeledStructureContext> labeledStructure() {
			return getRuleContexts(LabeledStructureContext.class);
		}
		public LabeledStructureContext labeledStructure(int i) {
			return getRuleContext(LabeledStructureContext.class,i);
		}
		public StructureDbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structureDb; }
	}

	public final StructureDbContext structureDb() throws RecognitionException {
		StructureDbContext _localctx = new StructureDbContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_structureDb);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(17); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(16);
				labeledStructure();
				}
				}
				setState(19); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Id );
			setState(21);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabeledStructureContext extends ParserRuleContext {
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public StructureContext structure() {
			return getRuleContext(StructureContext.class,0);
		}
		public TerminalNode LineBreak() { return getToken(GigCondensedParser.LineBreak, 0); }
		public LabeledStructureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledStructure; }
	}

	public final LabeledStructureContext labeledStructure() throws RecognitionException {
		LabeledStructureContext _localctx = new LabeledStructureContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_labeledStructure);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23);
			label();
			setState(24);
			structure();
			setState(25);
			match(LineBreak);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructureContext extends ParserRuleContext {
		public SubTreeContext subTree() {
			return getRuleContext(SubTreeContext.class,0);
		}
		public List<ExtensionContext> extension() {
			return getRuleContexts(ExtensionContext.class);
		}
		public ExtensionContext extension(int i) {
			return getRuleContext(ExtensionContext.class,i);
		}
		public StructureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structure; }
	}

	public final StructureContext structure() throws RecognitionException {
		StructureContext _localctx = new StructureContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_structure);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(28); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(27);
						extension();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(30); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			}
			setState(34);
			subTree();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExtensionContext extends ParserRuleContext {
		public SubTreeContext subTree() {
			return getRuleContext(SubTreeContext.class,0);
		}
		public TerminalNode Number() { return getToken(GigCondensedParser.Number, 0); }
		public ExtensionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_extension; }
	}

	public final ExtensionContext extension() throws RecognitionException {
		ExtensionContext _localctx = new ExtensionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_extension);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37);
			_la = _input.LA(1);
			if (_la==Number) {
				{
				setState(36);
				match(Number);
				}
			}

			setState(39);
			subTree();
			setState(40);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubTreeContext extends ParserRuleContext {
		public List<LinkedUnitContext> linkedUnit() {
			return getRuleContexts(LinkedUnitContext.class);
		}
		public LinkedUnitContext linkedUnit(int i) {
			return getRuleContext(LinkedUnitContext.class,i);
		}
		public List<TerminalNode> OpenBracket() { return getTokens(GigCondensedParser.OpenBracket); }
		public TerminalNode OpenBracket(int i) {
			return getToken(GigCondensedParser.OpenBracket, i);
		}
		public List<SubTreeContext> subTree() {
			return getRuleContexts(SubTreeContext.class);
		}
		public SubTreeContext subTree(int i) {
			return getRuleContext(SubTreeContext.class,i);
		}
		public List<TerminalNode> CloseBracket() { return getTokens(GigCondensedParser.CloseBracket); }
		public TerminalNode CloseBracket(int i) {
			return getToken(GigCondensedParser.CloseBracket, i);
		}
		public SubTreeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subTree; }
	}

	public final SubTreeContext subTree() throws RecognitionException {
		SubTreeContext _localctx = new SubTreeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_subTree);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(47);
				switch (_input.LA(1)) {
				case Unit:
					{
					setState(42);
					linkedUnit();
					}
					break;
				case OpenBracket:
					{
					{
					setState(43);
					match(OpenBracket);
					setState(44);
					subTree();
					setState(45);
					match(CloseBracket);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==Unit || _la==OpenBracket );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LinkedUnitContext extends ParserRuleContext {
		public TerminalNode Unit() { return getToken(GigCondensedParser.Unit, 0); }
		public LinkContext link() {
			return getRuleContext(LinkContext.class,0);
		}
		public LinkedUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_linkedUnit; }
	}

	public final LinkedUnitContext linkedUnit() throws RecognitionException {
		LinkedUnitContext _localctx = new LinkedUnitContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_linkedUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(Unit);
			setState(53);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << Anomericity) | (1L << Number))) != 0)) {
				{
				setState(52);
				link();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode Id() { return getToken(GigCondensedParser.Id, 0); }
		public TerminalNode Sepparator() { return getToken(GigCondensedParser.Sepparator, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(Id);
			setState(56);
			match(Sepparator);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LinkContext extends ParserRuleContext {
		public TerminalNode Anomericity() { return getToken(GigCondensedParser.Anomericity, 0); }
		public List<TerminalNode> Number() { return getTokens(GigCondensedParser.Number); }
		public TerminalNode Number(int i) {
			return getToken(GigCondensedParser.Number, i);
		}
		public LinkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_link; }
	}

	public final LinkContext link() throws RecognitionException {
		LinkContext _localctx = new LinkContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_link);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_la = _input.LA(1);
			if (_la==Anomericity) {
				{
				setState(58);
				match(Anomericity);
				}
			}

			setState(62);
			_la = _input.LA(1);
			if (_la==Number) {
				{
				setState(61);
				match(Number);
				}
			}

			setState(64);
			match(T__1);
			setState(66);
			_la = _input.LA(1);
			if (_la==Number) {
				{
				setState(65);
				match(Number);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\rG\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\6\2\24\n\2\r\2"+
		"\16\2\25\3\2\3\2\3\3\3\3\3\3\3\3\3\4\6\4\37\n\4\r\4\16\4 \5\4#\n\4\3\4"+
		"\3\4\3\5\5\5(\n\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\6\6\62\n\6\r\6\16\6"+
		"\63\3\7\3\7\5\78\n\7\3\b\3\b\3\b\3\t\5\t>\n\t\3\t\5\tA\n\t\3\t\3\t\5\t"+
		"E\n\t\3\t\2\2\n\2\4\6\b\n\f\16\20\2\2H\2\23\3\2\2\2\4\31\3\2\2\2\6\"\3"+
		"\2\2\2\b\'\3\2\2\2\n\61\3\2\2\2\f\65\3\2\2\2\169\3\2\2\2\20=\3\2\2\2\22"+
		"\24\5\4\3\2\23\22\3\2\2\2\24\25\3\2\2\2\25\23\3\2\2\2\25\26\3\2\2\2\26"+
		"\27\3\2\2\2\27\30\7\2\2\3\30\3\3\2\2\2\31\32\5\16\b\2\32\33\5\6\4\2\33"+
		"\34\7\13\2\2\34\5\3\2\2\2\35\37\5\b\5\2\36\35\3\2\2\2\37 \3\2\2\2 \36"+
		"\3\2\2\2 !\3\2\2\2!#\3\2\2\2\"\36\3\2\2\2\"#\3\2\2\2#$\3\2\2\2$%\5\n\6"+
		"\2%\7\3\2\2\2&(\7\b\2\2\'&\3\2\2\2\'(\3\2\2\2()\3\2\2\2)*\5\n\6\2*+\7"+
		"\3\2\2+\t\3\2\2\2,\62\5\f\7\2-.\7\t\2\2./\5\n\6\2/\60\7\n\2\2\60\62\3"+
		"\2\2\2\61,\3\2\2\2\61-\3\2\2\2\62\63\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2"+
		"\2\64\13\3\2\2\2\65\67\7\7\2\2\668\5\20\t\2\67\66\3\2\2\2\678\3\2\2\2"+
		"8\r\3\2\2\29:\7\5\2\2:;\7\f\2\2;\17\3\2\2\2<>\7\6\2\2=<\3\2\2\2=>\3\2"+
		"\2\2>@\3\2\2\2?A\7\b\2\2@?\3\2\2\2@A\3\2\2\2AB\3\2\2\2BD\7\4\2\2CE\7\b"+
		"\2\2DC\3\2\2\2DE\3\2\2\2E\21\3\2\2\2\f\25 \"\'\61\63\67=@D";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}