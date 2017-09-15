// Generated from C:/Users/Oliver/Documents/IdeaProjects/glycoforest-spark/parser/src/main/java/org/expasy/glycoforest/parser\GigCondensed.g4 by ANTLR 4.5.3
package org.expasy.glycoforest.parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GigCondensedLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, Id=3, Anomericity=4, Unit=5, Number=6, OpenBracket=7, 
		CloseBracket=8, LineBreak=9, Sepparator=10, WS=11;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "Id", "Anomericity", "Unit", "Number", "OpenBracket", 
		"CloseBracket", "LineBreak", "Sepparator", "WS"
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


	public GigCondensedLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "GigCondensed.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\r\u00a8\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\3\2\3\2\3\3\3\3\3\4\3\4\6\4 \n\4\r\4\16\4!\3\4\3\4\3\4\3"+
		"\4\6\4(\n\4\r\4\16\4)\3\4\5\4-\n\4\5\4/\n\4\3\5\3\5\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6"+
		"\u008e\n\6\3\7\6\7\u0091\n\7\r\7\16\7\u0092\3\b\3\b\3\t\3\t\3\n\5\n\u009a"+
		"\n\n\3\n\3\n\5\n\u009e\n\n\3\13\3\13\3\f\6\f\u00a3\n\f\r\f\16\f\u00a4"+
		"\3\f\3\f\2\2\r\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\3\2"+
		"\t\3\2\62;\3\2c|\4\2cd\u03b3\u03b4\5\2**]]}}\5\2++__\177\177\5\2\13\13"+
		"..<=\3\2\"\"\u00c1\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\3\31\3\2\2\2\5\33\3\2\2\2\7.\3\2\2\2\t\60\3\2\2\2\13\u008d"+
		"\3\2\2\2\r\u0090\3\2\2\2\17\u0094\3\2\2\2\21\u0096\3\2\2\2\23\u009d\3"+
		"\2\2\2\25\u009f\3\2\2\2\27\u00a2\3\2\2\2\31\32\7-\2\2\32\4\3\2\2\2\33"+
		"\34\7/\2\2\34\6\3\2\2\2\35\37\t\2\2\2\36 \t\2\2\2\37\36\3\2\2\2 !\3\2"+
		"\2\2!\37\3\2\2\2!\"\3\2\2\2\"#\3\2\2\2#$\7/\2\2$/\t\2\2\2%\'\t\2\2\2&"+
		"(\t\2\2\2\'&\3\2\2\2()\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*,\3\2\2\2+-\t\3\2"+
		"\2,+\3\2\2\2,-\3\2\2\2-/\3\2\2\2.\35\3\2\2\2.%\3\2\2\2/\b\3\2\2\2\60\61"+
		"\t\4\2\2\61\n\3\2\2\2\62\63\7I\2\2\63\64\7c\2\2\64\u008e\7n\2\2\65\66"+
		"\7J\2\2\66\67\7g\2\2\67\u008e\7z\2\289\7I\2\29:\7n\2\2:\u008e\7e\2\2;"+
		"<\7I\2\2<=\7c\2\2=>\7n\2\2>?\7P\2\2?@\7C\2\2@\u008e\7e\2\2AB\7I\2\2BC"+
		"\7n\2\2CD\7e\2\2DE\7C\2\2E\u008e\7e\2\2FG\7I\2\2GH\7c\2\2HI\7n\2\2IJ\7"+
		"P\2\2JK\7C\2\2KL\7e\2\2LM\7q\2\2M\u008e\7n\2\2NO\7I\2\2OP\7n\2\2PQ\7e"+
		"\2\2QR\7P\2\2RS\7C\2\2S\u008e\7e\2\2TU\7I\2\2UV\7n\2\2VW\7e\2\2WX\7P\2"+
		"\2XY\7C\2\2YZ\7e\2\2Z[\7q\2\2[\u008e\7n\2\2\\]\7J\2\2]^\7g\2\2^_\7z\2"+
		"\2_`\7P\2\2`a\7C\2\2ab\7e\2\2bc\7q\2\2c\u008e\7n\2\2de\7H\2\2ef\7w\2\2"+
		"f\u008e\7e\2\2gh\7J\2\2hi\7g\2\2ij\7z\2\2jk\7P\2\2kl\7C\2\2l\u008e\7e"+
		"\2\2mn\7P\2\2no\7g\2\2op\7w\2\2pq\7I\2\2q\u008e\7e\2\2rs\7P\2\2st\7g\2"+
		"\2tu\7w\2\2uv\7\67\2\2vw\7I\2\2w\u008e\7e\2\2xy\7P\2\2yz\7g\2\2z{\7w\2"+
		"\2{|\7C\2\2|\u008e\7e\2\2}~\7P\2\2~\177\7g\2\2\177\u0080\7w\2\2\u0080"+
		"\u0081\7\67\2\2\u0081\u0082\7C\2\2\u0082\u008e\7e\2\2\u0083\u0084\7M\2"+
		"\2\u0084\u0085\7f\2\2\u0085\u008e\7p\2\2\u0086\u008e\7U\2\2\u0087\u0088"+
		"\7O\2\2\u0088\u0089\7c\2\2\u0089\u008e\7p\2\2\u008a\u008b\7Z\2\2\u008b"+
		"\u008c\7{\2\2\u008c\u008e\7n\2\2\u008d\62\3\2\2\2\u008d\65\3\2\2\2\u008d"+
		"8\3\2\2\2\u008d;\3\2\2\2\u008dA\3\2\2\2\u008dF\3\2\2\2\u008dN\3\2\2\2"+
		"\u008dT\3\2\2\2\u008d\\\3\2\2\2\u008dd\3\2\2\2\u008dg\3\2\2\2\u008dm\3"+
		"\2\2\2\u008dr\3\2\2\2\u008dx\3\2\2\2\u008d}\3\2\2\2\u008d\u0083\3\2\2"+
		"\2\u008d\u0086\3\2\2\2\u008d\u0087\3\2\2\2\u008d\u008a\3\2\2\2\u008e\f"+
		"\3\2\2\2\u008f\u0091\t\2\2\2\u0090\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092"+
		"\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\16\3\2\2\2\u0094\u0095\t\5\2"+
		"\2\u0095\20\3\2\2\2\u0096\u0097\t\6\2\2\u0097\22\3\2\2\2\u0098\u009a\7"+
		"\17\2\2\u0099\u0098\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009b\3\2\2\2\u009b"+
		"\u009e\7\f\2\2\u009c\u009e\7\17\2\2\u009d\u0099\3\2\2\2\u009d\u009c\3"+
		"\2\2\2\u009e\24\3\2\2\2\u009f\u00a0\t\7\2\2\u00a0\26\3\2\2\2\u00a1\u00a3"+
		"\t\b\2\2\u00a2\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\b\f\2\2\u00a7\30\3\2\2"+
		"\2\f\2!),.\u008d\u0092\u0099\u009d\u00a4\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}