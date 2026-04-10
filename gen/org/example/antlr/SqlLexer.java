// Generated from C:/Users/Nick/IdeaProjects/AntlrSQL/src/main/java/org/example/antlr/Sql.g4 by ANTLR 4.13.2
package org.example.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SqlLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, SELECT=3, FROM=4, WHERE=5, AS=6, AND=7, OR=8, STAR=9, 
		COMMA=10, EQ=11, NEQ=12, GT=13, LT=14, GE=15, LE=16, NUMBER=17, STRING=18, 
		IDENTIFIER=19, WS=20;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "SELECT", "FROM", "WHERE", "AS", "AND", "OR", "STAR", 
			"COMMA", "EQ", "NEQ", "GT", "LT", "GE", "LE", "NUMBER", "STRING", "IDENTIFIER", 
			"WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", null, null, null, null, null, null, "'*'", "','", 
			"'='", null, "'>'", "'<'", "'>='", "'<='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "SELECT", "FROM", "WHERE", "AS", "AND", "OR", "STAR", 
			"COMMA", "EQ", "NEQ", "GT", "LT", "GE", "LE", "NUMBER", "STRING", "IDENTIFIER", 
			"WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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


	public SqlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Sql.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0014\u009f\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002:\b\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0003\u0003D\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004P\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0003\u0005V\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0003\u0006^\b\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0003\u0007d\b\u0007\u0001\b\u0001\b\u0001\t"+
		"\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0003\u000bp\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0004"+
		"\u0010}\b\u0010\u000b\u0010\f\u0010~\u0001\u0010\u0001\u0010\u0004\u0010"+
		"\u0083\b\u0010\u000b\u0010\f\u0010\u0084\u0003\u0010\u0087\b\u0010\u0001"+
		"\u0011\u0001\u0011\u0005\u0011\u008b\b\u0011\n\u0011\f\u0011\u008e\t\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0005\u0012\u0094\b\u0012"+
		"\n\u0012\f\u0012\u0097\t\u0012\u0001\u0013\u0004\u0013\u009a\b\u0013\u000b"+
		"\u0013\f\u0013\u009b\u0001\u0013\u0001\u0013\u0001\u008c\u0000\u0014\u0001"+
		"\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007"+
		"\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d"+
		"\u000f\u001f\u0010!\u0011#\u0012%\u0013\'\u0014\u0001\u0000\u0004\u0001"+
		"\u000009\u0003\u0000AZ__az\u0004\u000009AZ__az\u0003\u0000\t\n\r\r  \u00ab"+
		"\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000"+
		"\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000"+
		"\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000"+
		"\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000"+
		"\u0000\u0000\'\u0001\u0000\u0000\u0000\u0001)\u0001\u0000\u0000\u0000"+
		"\u0003+\u0001\u0000\u0000\u0000\u00059\u0001\u0000\u0000\u0000\u0007C"+
		"\u0001\u0000\u0000\u0000\tO\u0001\u0000\u0000\u0000\u000bU\u0001\u0000"+
		"\u0000\u0000\r]\u0001\u0000\u0000\u0000\u000fc\u0001\u0000\u0000\u0000"+
		"\u0011e\u0001\u0000\u0000\u0000\u0013g\u0001\u0000\u0000\u0000\u0015i"+
		"\u0001\u0000\u0000\u0000\u0017o\u0001\u0000\u0000\u0000\u0019q\u0001\u0000"+
		"\u0000\u0000\u001bs\u0001\u0000\u0000\u0000\u001du\u0001\u0000\u0000\u0000"+
		"\u001fx\u0001\u0000\u0000\u0000!|\u0001\u0000\u0000\u0000#\u0088\u0001"+
		"\u0000\u0000\u0000%\u0091\u0001\u0000\u0000\u0000\'\u0099\u0001\u0000"+
		"\u0000\u0000)*\u0005(\u0000\u0000*\u0002\u0001\u0000\u0000\u0000+,\u0005"+
		")\u0000\u0000,\u0004\u0001\u0000\u0000\u0000-.\u0005S\u0000\u0000./\u0005"+
		"E\u0000\u0000/0\u0005L\u0000\u000001\u0005E\u0000\u000012\u0005C\u0000"+
		"\u00002:\u0005T\u0000\u000034\u0005s\u0000\u000045\u0005e\u0000\u0000"+
		"56\u0005l\u0000\u000067\u0005e\u0000\u000078\u0005c\u0000\u00008:\u0005"+
		"t\u0000\u00009-\u0001\u0000\u0000\u000093\u0001\u0000\u0000\u0000:\u0006"+
		"\u0001\u0000\u0000\u0000;<\u0005F\u0000\u0000<=\u0005R\u0000\u0000=>\u0005"+
		"O\u0000\u0000>D\u0005M\u0000\u0000?@\u0005f\u0000\u0000@A\u0005r\u0000"+
		"\u0000AB\u0005o\u0000\u0000BD\u0005m\u0000\u0000C;\u0001\u0000\u0000\u0000"+
		"C?\u0001\u0000\u0000\u0000D\b\u0001\u0000\u0000\u0000EF\u0005W\u0000\u0000"+
		"FG\u0005H\u0000\u0000GH\u0005E\u0000\u0000HI\u0005R\u0000\u0000IP\u0005"+
		"E\u0000\u0000JK\u0005w\u0000\u0000KL\u0005h\u0000\u0000LM\u0005e\u0000"+
		"\u0000MN\u0005r\u0000\u0000NP\u0005e\u0000\u0000OE\u0001\u0000\u0000\u0000"+
		"OJ\u0001\u0000\u0000\u0000P\n\u0001\u0000\u0000\u0000QR\u0005A\u0000\u0000"+
		"RV\u0005S\u0000\u0000ST\u0005a\u0000\u0000TV\u0005s\u0000\u0000UQ\u0001"+
		"\u0000\u0000\u0000US\u0001\u0000\u0000\u0000V\f\u0001\u0000\u0000\u0000"+
		"WX\u0005A\u0000\u0000XY\u0005N\u0000\u0000Y^\u0005D\u0000\u0000Z[\u0005"+
		"a\u0000\u0000[\\\u0005n\u0000\u0000\\^\u0005d\u0000\u0000]W\u0001\u0000"+
		"\u0000\u0000]Z\u0001\u0000\u0000\u0000^\u000e\u0001\u0000\u0000\u0000"+
		"_`\u0005O\u0000\u0000`d\u0005R\u0000\u0000ab\u0005o\u0000\u0000bd\u0005"+
		"r\u0000\u0000c_\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000d\u0010"+
		"\u0001\u0000\u0000\u0000ef\u0005*\u0000\u0000f\u0012\u0001\u0000\u0000"+
		"\u0000gh\u0005,\u0000\u0000h\u0014\u0001\u0000\u0000\u0000ij\u0005=\u0000"+
		"\u0000j\u0016\u0001\u0000\u0000\u0000kl\u0005<\u0000\u0000lp\u0005>\u0000"+
		"\u0000mn\u0005!\u0000\u0000np\u0005=\u0000\u0000ok\u0001\u0000\u0000\u0000"+
		"om\u0001\u0000\u0000\u0000p\u0018\u0001\u0000\u0000\u0000qr\u0005>\u0000"+
		"\u0000r\u001a\u0001\u0000\u0000\u0000st\u0005<\u0000\u0000t\u001c\u0001"+
		"\u0000\u0000\u0000uv\u0005>\u0000\u0000vw\u0005=\u0000\u0000w\u001e\u0001"+
		"\u0000\u0000\u0000xy\u0005<\u0000\u0000yz\u0005=\u0000\u0000z \u0001\u0000"+
		"\u0000\u0000{}\u0007\u0000\u0000\u0000|{\u0001\u0000\u0000\u0000}~\u0001"+
		"\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000"+
		"\u0000\u007f\u0086\u0001\u0000\u0000\u0000\u0080\u0082\u0005.\u0000\u0000"+
		"\u0081\u0083\u0007\u0000\u0000\u0000\u0082\u0081\u0001\u0000\u0000\u0000"+
		"\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0082\u0001\u0000\u0000\u0000"+
		"\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0087\u0001\u0000\u0000\u0000"+
		"\u0086\u0080\u0001\u0000\u0000\u0000\u0086\u0087\u0001\u0000\u0000\u0000"+
		"\u0087\"\u0001\u0000\u0000\u0000\u0088\u008c\u0005\'\u0000\u0000\u0089"+
		"\u008b\t\u0000\u0000\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008b\u008e"+
		"\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008c\u008a"+
		"\u0001\u0000\u0000\u0000\u008d\u008f\u0001\u0000\u0000\u0000\u008e\u008c"+
		"\u0001\u0000\u0000\u0000\u008f\u0090\u0005\'\u0000\u0000\u0090$\u0001"+
		"\u0000\u0000\u0000\u0091\u0095\u0007\u0001\u0000\u0000\u0092\u0094\u0007"+
		"\u0002\u0000\u0000\u0093\u0092\u0001\u0000\u0000\u0000\u0094\u0097\u0001"+
		"\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000\u0095\u0096\u0001"+
		"\u0000\u0000\u0000\u0096&\u0001\u0000\u0000\u0000\u0097\u0095\u0001\u0000"+
		"\u0000\u0000\u0098\u009a\u0007\u0003\u0000\u0000\u0099\u0098\u0001\u0000"+
		"\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\u0099\u0001\u0000"+
		"\u0000\u0000\u009b\u009c\u0001\u0000\u0000\u0000\u009c\u009d\u0001\u0000"+
		"\u0000\u0000\u009d\u009e\u0006\u0013\u0000\u0000\u009e(\u0001\u0000\u0000"+
		"\u0000\u000e\u00009COU]co~\u0084\u0086\u008c\u0095\u009b\u0001\u0006\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}