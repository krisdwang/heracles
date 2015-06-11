package heracles.jdbc.parser;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import antlr.ANTLRHashString;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;

@SuppressWarnings({"unchecked", "unused"})
public class SQLLexer extends antlr.CharScanner implements SQLTokenTypes, TokenStream {
	public SQLLexer(InputStream in) {
		this(new ByteBuffer(in));
	}

	public SQLLexer(Reader in) {
		this(new CharBuffer(in));
	}

	public SQLLexer(InputBuffer ib) {
		this(new LexerSharedInputState(ib));
	}

	
	public SQLLexer(LexerSharedInputState state) {
		super(state);
		caseSensitiveLiterals = false;
		setCaseSensitive(false);
		literals = new Hashtable<ANTLRHashString, Integer>();
		literals.put(new ANTLRHashString("hint", this), new Integer(35));
		literals.put(new ANTLRHashString("count", this), new Integer(17));
		literals.put(new ANTLRHashString("sum", this), new Integer(16));
		literals.put(new ANTLRHashString("min", this), new Integer(14));
		literals.put(new ANTLRHashString("and", this), new Integer(4));
		literals.put(new ANTLRHashString("asc", this), new Integer(33));
		literals.put(new ANTLRHashString("desc", this), new Integer(34));
		literals.put(new ANTLRHashString("select", this), new Integer(19));
		literals.put(new ANTLRHashString("force_read", this), new Integer(36));
		literals.put(new ANTLRHashString("cross", this), new Integer(26));
		literals.put(new ANTLRHashString("right", this), new Integer(24));
		literals.put(new ANTLRHashString("on", this), new Integer(29));
		literals.put(new ANTLRHashString("outer", this), new Integer(27));
		literals.put(new ANTLRHashString("where", this), new Integer(22));
		literals.put(new ANTLRHashString("left", this), new Integer(23));
		literals.put(new ANTLRHashString("set", this), new Integer(20));
		literals.put(new ANTLRHashString("avg", this), new Integer(15));
		literals.put(new ANTLRHashString("order", this), new Integer(31));
		literals.put(new ANTLRHashString("limit", this), new Integer(30));
		literals.put(new ANTLRHashString("in", this), new Integer(8));
		literals.put(new ANTLRHashString("into", this), new Integer(10));
		literals.put(new ANTLRHashString("inner", this), new Integer(25));
		literals.put(new ANTLRHashString("value", this), new Integer(12));
		literals.put(new ANTLRHashString("values", this), new Integer(11));
		literals.put(new ANTLRHashString("insert", this), new Integer(9));
		literals.put(new ANTLRHashString("or", this), new Integer(18));
		literals.put(new ANTLRHashString("max", this), new Integer(13));
		literals.put(new ANTLRHashString("from", this), new Integer(7));
		literals.put(new ANTLRHashString("delete", this), new Integer(6));
		literals.put(new ANTLRHashString("update", this), new Integer(21));
		literals.put(new ANTLRHashString("join", this), new Integer(28));
		literals.put(new ANTLRHashString("by", this), new Integer(32));
		literals.put(new ANTLRHashString("as", this), new Integer(5));
	}

	public Token nextToken() throws TokenStreamException {
		Token theRetToken = null;
		tryAgain: for (;;) {
			Token _token = null;
			int _ttype = Token.INVALID_TYPE;
			resetText();
			try { // for char stream error handling
				try { // for lexical error handling
					switch (LA(1)) {
					case '=': {
						mEQ(true);
						theRetToken = _returnToken;
						break;
					}
					case '!':
					case '^': {
						mNE(true);
						theRetToken = _returnToken;
						break;
					}
					case ',': {
						mCOMMA(true);
						theRetToken = _returnToken;
						break;
					}
					case '(': {
						mOPEN(true);
						theRetToken = _returnToken;
						break;
					}
					case ')': {
						mCLOSE(true);
						theRetToken = _returnToken;
						break;
					}
					case '[': {
						mOPEN_BRACKET(true);
						theRetToken = _returnToken;
						break;
					}
					case ']': {
						mCLOSE_BRACKET(true);
						theRetToken = _returnToken;
						break;
					}
					case '|': {
						mCONCAT(true);
						theRetToken = _returnToken;
						break;
					}
					case '+': {
						mPLUS(true);
						theRetToken = _returnToken;
						break;
					}
					case '-': {
						mMINUS(true);
						theRetToken = _returnToken;
						break;
					}
					case '%': {
						mMOD(true);
						theRetToken = _returnToken;
						break;
					}
					case ':': {
						mCOLON(true);
						theRetToken = _returnToken;
						break;
					}
					case '?': {
						mPARAM(true);
						theRetToken = _returnToken;
						break;
					}
					case '.': {
						mDOT(true);
						theRetToken = _returnToken;
						break;
					}
					case '\'': {
						mQUOTED_STRING(true);
						theRetToken = _returnToken;
						break;
					}
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9': {
						mNUMERICAL(true);
						theRetToken = _returnToken;
						break;
					}
					case '\t':
					case '\n':
					case '\r':
					case ' ': {
						mWS(true);
						theRetToken = _returnToken;
						break;
					}
					default:
						if ((LA(1) == '<') && (LA(2) == '>')) {
							mSQL_NE(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '<') && (LA(2) == '=')) {
							mLE(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '>') && (LA(2) == '=')) {
							mGE(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '/') && (LA(2) == '*')) {
							mOPEN_COMMENT(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '*') && (LA(2) == '/')) {
							mCLOSE_COMMENT(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '<') && (true)) {
							mLT(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '>') && (true)) {
							mGT(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '*') && (true)) {
							mSTAR(true);
							theRetToken = _returnToken;
						} else if ((LA(1) == '/') && (true)) {
							mDIV(true);
							theRetToken = _returnToken;
						} else if ((_tokenSet_0.member(LA(1)))) {
							mIDENT(true);
							theRetToken = _returnToken;
						} else {
							if (LA(1) == EOF_CHAR) {
								uponEOF();
								_returnToken = makeToken(Token.EOF_TYPE);
							} else {
								throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine(),
										getColumn());
							}
						}
					}
					if (_returnToken == null)
						continue tryAgain; // found SKIP token
					_ttype = _returnToken.getType();
					_returnToken.setType(_ttype);
					return _returnToken;
				} catch (RecognitionException e) {
					throw new TokenStreamRecognitionException(e);
				}
			} catch (CharStreamException cse) {
				if (cse instanceof CharStreamIOException) {
					throw new TokenStreamIOException(((CharStreamIOException) cse).io);
				} else {
					throw new TokenStreamException(cse.getMessage());
				}
			}
		}
	}

	public final void mEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = EQ;
		int _saveIndex;

		match('=');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LT;
		int _saveIndex;

		match('<');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GT;
		int _saveIndex;

		match('>');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSQL_NE(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SQL_NE;
		int _saveIndex;

		match("<>");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mNE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NE;
		int _saveIndex;

		switch (LA(1)) {
		case '!': {
			match("!=");
			break;
		}
		case '^': {
			match("^=");
			break;
		}
		default: {
			throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LE;
		int _saveIndex;

		match("<=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mGE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GE;
		int _saveIndex;

		match(">=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = COMMA;
		int _saveIndex;

		match(',');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mOPEN(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = OPEN;
		int _saveIndex;

		match('(');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCLOSE(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CLOSE;
		int _saveIndex;

		match(')');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mOPEN_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = OPEN_BRACKET;
		int _saveIndex;

		match('[');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCLOSE_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CLOSE_BRACKET;
		int _saveIndex;

		match(']');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCONCAT(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CONCAT;
		int _saveIndex;

		match("||");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = PLUS;
		int _saveIndex;

		match('+');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = MINUS;
		int _saveIndex;

		match('-');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = STAR;
		int _saveIndex;

		match('*');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = DIV;
		int _saveIndex;

		match('/');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mMOD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = MOD;
		int _saveIndex;

		match('%');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = COLON;
		int _saveIndex;

		match(':');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mPARAM(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = PARAM;
		int _saveIndex;

		match('?');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mDOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = DOT;
		int _saveIndex;

		match('.');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mOPEN_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = OPEN_COMMENT;
		int _saveIndex;

		match("/*");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCLOSE_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CLOSE_COMMENT;
		int _saveIndex;

		match("*/");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = IDENT;
		int _saveIndex;

		mID_START_LETTER(false);
		{
			_loop110: do {
				if ((_tokenSet_1.member(LA(1)))) {
					mID_LETTER(false);
				} else {
					break _loop110;
				}

			} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mID_START_LETTER(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ID_START_LETTER;
		int _saveIndex;

		switch (LA(1)) {
		case '_': {
			match('_');
			break;
		}
		case '$': {
			match('$');
			break;
		}
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z': {
			matchRange('a', 'z');
			break;
		}
		default:
			if (((LA(1) >= '\u0080' && LA(1) <= '\ufffe'))) {
				matchRange('\u0080', '\ufffe');
			} else {
				throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine(), getColumn());
			}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mID_LETTER(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ID_LETTER;
		int _saveIndex;

		switch (LA(1)) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9': {
			matchRange('0', '9');
			break;
		}
		case '.': {
			mDOT(false);
			break;
		}
		default:
			if ((_tokenSet_0.member(LA(1)))) {
				mID_START_LETTER(false);
			} else {
				throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine(), getColumn());
			}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mQUOTED_STRING(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = QUOTED_STRING;
		int _saveIndex;

		match('\'');
		{
			_loop117: do {
				boolean synPredMatched116 = false;
				if (((LA(1) == '\'') && (LA(2) == '\''))) {
					int _m116 = mark();
					synPredMatched116 = true;
					inputState.guessing++;
					try {
						{
							mESCqs(false);
						}
					} catch (RecognitionException pe) {
						synPredMatched116 = false;
					}
					rewind(_m116);
					inputState.guessing--;
				}
				if (synPredMatched116) {
					mESCqs(false);
				} else if ((_tokenSet_2.member(LA(1)))) {
					matchNot('\'');
				} else {
					break _loop117;
				}

			} while (true);
		}
		match('\'');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mESCqs(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ESCqs;
		int _saveIndex;

		match('\'');
		match('\'');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mNUMERICAL(boolean _createToken) throws RecognitionException, CharStreamException,
			TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NUMERICAL;
		int _saveIndex;

		{
			matchRange('1', '9');
		}
		{
			_loop121: do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					matchRange('0', '9');
				} else {
					break _loop121;
				}

			} while (true);
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = WS;
		int _saveIndex;

		{
			switch (LA(1)) {
			case ' ': {
				match(' ');
				break;
			}
			case '\t': {
				match('\t');
				break;
			}
			case '\n': {
				match('\n');
				if (inputState.guessing == 0) {
					newline();
				}
				break;
			}
			default:
				if ((LA(1) == '\r') && (LA(2) == '\n')) {
					match('\r');
					match('\n');
					if (inputState.guessing == 0) {
						newline();
					}
				} else if ((LA(1) == '\r') && (true)) {
					match('\r');
					if (inputState.guessing == 0) {
						newline();
					}
				} else {
					throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine(), getColumn());
				}
			}
		}
		if (inputState.guessing == 0) {
			_ttype = Token.SKIP;
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	private static final long[] mk_tokenSet_0() {
		long[] data = new long[3072];
		data[0] = 68719476736L;
		data[1] = 576460745860972544L;
		for (int i = 2; i <= 1022; i++) {
			data[i] = -1L;
		}
		data[1023] = 9223372036854775807L;
		return data;
	}

	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

	private static final long[] mk_tokenSet_1() {
		long[] data = new long[3072];
		data[0] = 288019338638655488L;
		data[1] = 576460745860972544L;
		for (int i = 2; i <= 1022; i++) {
			data[i] = -1L;
		}
		data[1023] = 9223372036854775807L;
		return data;
	}

	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());

	private static final long[] mk_tokenSet_2() {
		long[] data = new long[2048];
		data[0] = -549755813889L;
		for (int i = 1; i <= 1022; i++) {
			data[i] = -1L;
		}
		data[1023] = 9223372036854775807L;
		return data;
	}

	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());

}
