// $ANTLR 3.5 D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g 2013-01-28 15:04:46

package org.adiusframework.console.parsers;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ADIUSCommandGrammarLexer extends Lexer {
	public static final int EOF = -1;
	public static final int T__14 = 14;
	public static final int T__15 = 15;
	public static final int T__16 = 16;
	public static final int T__17 = 17;
	public static final int T__18 = 18;
	public static final int T__19 = 19;
	public static final int T__20 = 20;
	public static final int T__21 = 21;
	public static final int ENDSYMBOL = 4;
	public static final int NUMBER = 5;
	public static final int PATH = 6;
	public static final int PATHDELIMITER = 7;
	public static final int PATHWITHWS = 8;
	public static final int PROTOCOL = 9;
	public static final int QUOTEDURI = 10;
	public static final int TEXT = 11;
	public static final int URI = 12;
	public static final int WS = 13;

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public ADIUSCommandGrammarLexer() {
	}

	public ADIUSCommandGrammarLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public ADIUSCommandGrammarLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override
	public String getGrammarFileName() {
		return "D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g";
	}

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:11:7:
			// ( '(' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:11:9:
			// '('
			{
				match('(');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__14"

	// $ANTLR start "T__15"
	public final void mT__15() throws RecognitionException {
		try {
			int _type = T__15;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:12:7:
			// ( ')' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:12:9:
			// ')'
			{
				match(')');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__15"

	// $ANTLR start "T__16"
	public final void mT__16() throws RecognitionException {
		try {
			int _type = T__16;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:13:7:
			// ( ',' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:13:9:
			// ','
			{
				match(',');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__16"

	// $ANTLR start "T__17"
	public final void mT__17() throws RecognitionException {
		try {
			int _type = T__17;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:14:7:
			// ( '=' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:14:9:
			// '='
			{
				match('=');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__17"

	// $ANTLR start "T__18"
	public final void mT__18() throws RecognitionException {
		try {
			int _type = T__18;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:15:7:
			// ( 'execute' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:15:9:
			// 'execute'
			{
				match("execute");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__18"

	// $ANTLR start "T__19"
	public final void mT__19() throws RecognitionException {
		try {
			int _type = T__19;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:16:7:
			// ( 'of' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:16:9:
			// 'of'
			{
				match("of");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__19"

	// $ANTLR start "T__20"
	public final void mT__20() throws RecognitionException {
		try {
			int _type = T__20;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:17:7:
			// ( 'properties' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:17:9:
			// 'properties'
			{
				match("properties");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__20"

	// $ANTLR start "T__21"
	public final void mT__21() throws RecognitionException {
		try {
			int _type = T__21;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:18:7:
			// ( 'using' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:18:9:
			// 'using'
			{
				match("using");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__21"

	// $ANTLR start "URI"
	public final void mURI() throws RecognitionException {
		try {
			int _type = URI;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:2:
			// ( ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT )? (
			// ':' NUMBER )? PATH ) | ( ( 'A' .. 'Z' ) ':' PATH ) )
			int alt4 = 2;
			int LA4_0 = input.LA(1);
			if ((LA4_0 == 'f')) {
				alt4 = 1;
			} else if (((LA4_0 >= 'A' && LA4_0 <= 'Z'))) {
				alt4 = 2;
			}

			else {
				NoViableAltException nvae = new NoViableAltException("", 4, 0, input);
				throw nvae;
			}

			switch (alt4) {
			case 1:
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:4:
			// ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT )? ( ':'
			// NUMBER )? PATH )
			{
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:4:
				// ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT )? (
				// ':' NUMBER )? PATH )
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:5:
				// PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT )? (
				// ':' NUMBER )? PATH
				{
					mPROTOCOL();

					match("://");

					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:20:
					// ( TEXT ':' TEXT '@' )?
					int alt1 = 2;
					alt1 = dfa1.predict(input);
					switch (alt1) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:21:
					// TEXT ':' TEXT '@'
					{
						mTEXT();

						match(':');
						mTEXT();

						match('@');
					}
						break;

					}

					mTEXT();

					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:46:
					// ( '.' TEXT )?
					int alt2 = 2;
					int LA2_0 = input.LA(1);
					if ((LA2_0 == '.')) {
						alt2 = 1;
					}
					switch (alt2) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:47:
					// '.' TEXT
					{
						match('.');
						mTEXT();

					}
						break;

					}

					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:58:
					// ( ':' NUMBER )?
					int alt3 = 2;
					int LA3_0 = input.LA(1);
					if ((LA3_0 == ':')) {
						alt3 = 1;
					}
					switch (alt3) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:59:
					// ':' NUMBER
					{
						match(':');
						mNUMBER();

					}
						break;

					}

					mPATH();

				}

			}
				break;
			case 2:
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:80:
			// ( ( 'A' .. 'Z' ) ':' PATH )
			{
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:80:
				// ( ( 'A' .. 'Z' ) ':' PATH )
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:107:81:
				// ( 'A' .. 'Z' ) ':' PATH
				{
					if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z')) {
						input.consume();
					} else {
						MismatchedSetException mse = new MismatchedSetException(null, input);
						recover(mse);
						throw mse;
					}
					match(':');
					mPATH();

				}

			}
				break;

			}
			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "URI"

	// $ANTLR start "QUOTEDURI"
	public final void mQUOTEDURI() throws RecognitionException {
		try {
			int _type = QUOTEDURI;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:2:
			// ( ( '\\'' | '\"' ) ( ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT
			// ( '.' TEXT )? ( ':' NUMBER )? PATHWITHWS ) | ( ( 'A' .. 'Z' ) ':'
			// PATHWITHWS ) ) ( '\\'' | '\"' ) )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:4:
			// ( '\\'' | '\"' ) ( ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT (
			// '.' TEXT )? ( ':' NUMBER )? PATHWITHWS ) | ( ( 'A' .. 'Z' ) ':'
			// PATHWITHWS ) ) ( '\\'' | '\"' )
			{
				if (input.LA(1) == '\"' || input.LA(1) == '\'') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:15:
				// ( ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT )?
				// ( ':' NUMBER )? PATHWITHWS ) | ( ( 'A' .. 'Z' ) ':'
				// PATHWITHWS ) )
				int alt8 = 2;
				int LA8_0 = input.LA(1);
				if ((LA8_0 == 'f')) {
					alt8 = 1;
				} else if (((LA8_0 >= 'A' && LA8_0 <= 'Z'))) {
					alt8 = 2;
				}

				else {
					NoViableAltException nvae = new NoViableAltException("", 8, 0, input);
					throw nvae;
				}

				switch (alt8) {
				case 1:
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:16:
				// ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT )? (
				// ':' NUMBER )? PATHWITHWS )
				{
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:16:
					// ( PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT
					// )? ( ':' NUMBER )? PATHWITHWS )
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:17:
					// PROTOCOL '://' ( TEXT ':' TEXT '@' )? TEXT ( '.' TEXT )?
					// ( ':' NUMBER )? PATHWITHWS
					{
						mPROTOCOL();

						match("://");

						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:32:
						// ( TEXT ':' TEXT '@' )?
						int alt5 = 2;
						alt5 = dfa5.predict(input);
						switch (alt5) {
						case 1:
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:33:
						// TEXT ':' TEXT '@'
						{
							mTEXT();

							match(':');
							mTEXT();

							match('@');
						}
							break;

						}

						mTEXT();

						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:58:
						// ( '.' TEXT )?
						int alt6 = 2;
						int LA6_0 = input.LA(1);
						if ((LA6_0 == '.')) {
							alt6 = 1;
						}
						switch (alt6) {
						case 1:
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:59:
						// '.' TEXT
						{
							match('.');
							mTEXT();

						}
							break;

						}

						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:70:
						// ( ':' NUMBER )?
						int alt7 = 2;
						int LA7_0 = input.LA(1);
						if ((LA7_0 == ':')) {
							alt7 = 1;
						}
						switch (alt7) {
						case 1:
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:71:
						// ':' NUMBER
						{
							match(':');
							mNUMBER();

						}
							break;

						}

						mPATHWITHWS();

					}

				}
					break;
				case 2:
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:99:
				// ( ( 'A' .. 'Z' ) ':' PATHWITHWS )
				{
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:99:
					// ( ( 'A' .. 'Z' ) ':' PATHWITHWS )
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:110:100:
					// ( 'A' .. 'Z' ) ':' PATHWITHWS
					{
						if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z')) {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}
						match(':');
						mPATHWITHWS();

					}

				}
					break;

				}

				if (input.LA(1) == '\"' || input.LA(1) == '\'') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "QUOTEDURI"

	// $ANTLR start "PATH"
	public final void mPATH() throws RecognitionException {
		try {
			int _type = PATH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:113:2:
			// ( ( PATHDELIMITER TEXT )* ( PATHDELIMITER TEXT '.' TEXT ) )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:113:4:
			// ( PATHDELIMITER TEXT )* ( PATHDELIMITER TEXT '.' TEXT )
			{
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:113:4:
				// ( PATHDELIMITER TEXT )*
				loop9: while (true) {
					int alt9 = 2;
					alt9 = dfa9.predict(input);
					switch (alt9) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:113:5:
					// PATHDELIMITER TEXT
					{
						mPATHDELIMITER();

						mTEXT();

					}
						break;

					default:
						break loop9;
					}
				}

				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:113:26:
				// ( PATHDELIMITER TEXT '.' TEXT )
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:113:27:
				// PATHDELIMITER TEXT '.' TEXT
				{
					mPATHDELIMITER();

					mTEXT();

					match('.');
					mTEXT();

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "PATH"

	// $ANTLR start "PATHWITHWS"
	public final void mPATHWITHWS() throws RecognitionException {
		try {
			int _type = PATHWITHWS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:2:
			// ( ( PATHDELIMITER ( '0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' | '_' )
			// ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )*
			// )* ( PATHDELIMITER ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z'
			// | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )* '.' TEXT ) )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:4:
			// ( PATHDELIMITER ( '0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' | '_' ) (
			// 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )*
			// )* ( PATHDELIMITER ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z'
			// | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )* '.' TEXT )
			{
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:4:
				// ( PATHDELIMITER ( '0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' | '_'
				// ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' |
				// ' ' )* )*
				loop11: while (true) {
					int alt11 = 2;
					alt11 = dfa11.predict(input);
					switch (alt11) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:5:
					// PATHDELIMITER ( '0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' |
					// '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_'
					// | '-' | ' ' )*
					{
						mPATHDELIMITER();

						if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z')
								|| input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:52:
						// ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' |
						// '-' | ' ' )*
						loop10: while (true) {
							int alt10 = 2;
							int LA10_0 = input.LA(1);
							if ((LA10_0 == ' ' || (LA10_0 >= '-' && LA10_0 <= '.') || (LA10_0 >= '0' && LA10_0 <= '9')
									|| (LA10_0 >= 'A' && LA10_0 <= 'Z') || LA10_0 == '_' || (LA10_0 >= 'a' && LA10_0 <= 'z'))) {
								alt10 = 1;
							}

							switch (alt10) {
							case 1:
							// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:
							{
								if (input.LA(1) == ' ' || (input.LA(1) >= '-' && input.LA(1) <= '.')
										|| (input.LA(1) >= '0' && input.LA(1) <= '9')
										|| (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
										|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
									input.consume();
								} else {
									MismatchedSetException mse = new MismatchedSetException(null, input);
									recover(mse);
									throw mse;
								}
							}
								break;

							default:
								break loop10;
							}
						}

					}
						break;

					default:
						break loop11;
					}
				}

				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:100:
				// ( PATHDELIMITER ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' ..
				// 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )* '.'
				// TEXT )
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:101:
				// PATHDELIMITER ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z'
				// | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )* '.' TEXT
				{
					mPATHDELIMITER();

					if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
							|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
						input.consume();
					} else {
						MismatchedSetException mse = new MismatchedSetException(null, input);
						recover(mse);
						throw mse;
					}
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:116:139:
					// ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-'
					// | ' ' )*
					loop12: while (true) {
						int alt12 = 2;
						alt12 = dfa12.predict(input);
						switch (alt12) {
						case 1:
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:
						{
							if (input.LA(1) == ' ' || (input.LA(1) >= '-' && input.LA(1) <= '.')
									|| (input.LA(1) >= '0' && input.LA(1) <= '9')
									|| (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
									|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
								input.consume();
							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}
						}
							break;

						default:
							break loop12;
						}
					}

					match('.');
					mTEXT();

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "PATHWITHWS"

	// $ANTLR start "PATHDELIMITER"
	public final void mPATHDELIMITER() throws RecognitionException {
		try {
			int _type = PATHDELIMITER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:119:2:
			// ( '/' | '\\\\' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:
			{
				if (input.LA(1) == '/' || input.LA(1) == '\\') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "PATHDELIMITER"

	// $ANTLR start "PROTOCOL"
	public final void mPROTOCOL() throws RecognitionException {
		try {
			int _type = PROTOCOL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:123:2:
			// ( 'ftp' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:123:4:
			// 'ftp'
			{
				match("ftp");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "PROTOCOL"

	// $ANTLR start "NUMBER"
	public final void mNUMBER() throws RecognitionException {
		try {
			int _type = NUMBER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:126:2:
			// ( '0' | '1' .. '9' ( '0' .. '9' )* )
			int alt14 = 2;
			int LA14_0 = input.LA(1);
			if ((LA14_0 == '0')) {
				alt14 = 1;
			} else if (((LA14_0 >= '1' && LA14_0 <= '9'))) {
				alt14 = 2;
			}

			else {
				NoViableAltException nvae = new NoViableAltException("", 14, 0, input);
				throw nvae;
			}

			switch (alt14) {
			case 1:
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:126:4:
			// '0'
			{
				match('0');
			}
				break;
			case 2:
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:126:10:
			// '1' .. '9' ( '0' .. '9' )*
			{
				matchRange('1', '9');
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:126:19:
				// ( '0' .. '9' )*
				loop13: while (true) {
					int alt13 = 2;
					int LA13_0 = input.LA(1);
					if (((LA13_0 >= '0' && LA13_0 <= '9'))) {
						alt13 = 1;
					}

					switch (alt13) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:
					{
						if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}
					}
						break;

					default:
						break loop13;
					}
				}

			}
				break;

			}
			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "NUMBER"

	// $ANTLR start "TEXT"
	public final void mTEXT() throws RecognitionException {
		try {
			int _type = TEXT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:129:2:
			// ( ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' ) ( 'a' .. 'z' |
			// 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:129:4:
			// ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' ) ( 'a' .. 'z' | 'A'
			// .. 'Z' | '0' .. '9' | '_' | '-' )*
			{
				if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z')
						|| input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:129:37:
				// ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
				loop15: while (true) {
					int alt15 = 2;
					int LA15_0 = input.LA(1);
					if ((LA15_0 == '-' || (LA15_0 >= '0' && LA15_0 <= '9') || (LA15_0 >= 'A' && LA15_0 <= 'Z')
							|| LA15_0 == '_' || (LA15_0 >= 'a' && LA15_0 <= 'z'))) {
						alt15 = 1;
					}

					switch (alt15) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:
					{
						if (input.LA(1) == '-' || (input.LA(1) >= '0' && input.LA(1) <= '9')
								|| (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
								|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}
					}
						break;

					default:
						break loop15;
					}
				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "TEXT"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:132:6:
			// ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:132:9:
			// ( ' ' | '\\t' | '\\r' | '\\n' )+
			{
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:132:9:
				// ( ' ' | '\\t' | '\\r' | '\\n' )+
				int cnt16 = 0;
				loop16: while (true) {
					int alt16 = 2;
					int LA16_0 = input.LA(1);
					if (((LA16_0 >= '\t' && LA16_0 <= '\n') || LA16_0 == '\r' || LA16_0 == ' ')) {
						alt16 = 1;
					}

					switch (alt16) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:
					{
						if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || input.LA(1) == '\r' || input.LA(1) == ' ') {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}
					}
						break;

					default:
						if (cnt16 >= 1)
							break loop16;
						EarlyExitException eee = new EarlyExitException(16, input);
						throw eee;
					}
					cnt16++;
				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "WS"

	// $ANTLR start "ENDSYMBOL"
	public final void mENDSYMBOL() throws RecognitionException {
		try {
			int _type = ENDSYMBOL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:135:2:
			// ( ';' )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:135:4:
			// ';'
			{
				match(';');
			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "ENDSYMBOL"

	@Override
	public void mTokens() throws RecognitionException {
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:8:
		// ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | URI
		// | QUOTEDURI | PATH | PATHWITHWS | PATHDELIMITER | PROTOCOL | NUMBER |
		// TEXT | WS | ENDSYMBOL )
		int alt17 = 18;
		alt17 = dfa17.predict(input);
		switch (alt17) {
		case 1:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:10:
		// T__14
		{
			mT__14();

		}
			break;
		case 2:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:16:
		// T__15
		{
			mT__15();

		}
			break;
		case 3:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:22:
		// T__16
		{
			mT__16();

		}
			break;
		case 4:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:28:
		// T__17
		{
			mT__17();

		}
			break;
		case 5:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:34:
		// T__18
		{
			mT__18();

		}
			break;
		case 6:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:40:
		// T__19
		{
			mT__19();

		}
			break;
		case 7:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:46:
		// T__20
		{
			mT__20();

		}
			break;
		case 8:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:52:
		// T__21
		{
			mT__21();

		}
			break;
		case 9:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:58:
		// URI
		{
			mURI();

		}
			break;
		case 10:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:62:
		// QUOTEDURI
		{
			mQUOTEDURI();

		}
			break;
		case 11:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:72:
		// PATH
		{
			mPATH();

		}
			break;
		case 12:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:77:
		// PATHWITHWS
		{
			mPATHWITHWS();

		}
			break;
		case 13:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:88:
		// PATHDELIMITER
		{
			mPATHDELIMITER();

		}
			break;
		case 14:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:102:
		// PROTOCOL
		{
			mPROTOCOL();

		}
			break;
		case 15:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:111:
		// NUMBER
		{
			mNUMBER();

		}
			break;
		case 16:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:118:
		// TEXT
		{
			mTEXT();

		}
			break;
		case 17:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:123:
		// WS
		{
			mWS();

		}
			break;
		case 18:
		// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:1:126:
		// ENDSYMBOL
		{
			mENDSYMBOL();

		}
			break;

		}
	}

	protected DFA1 dfa1 = new DFA1(this);
	protected DFA5 dfa5 = new DFA5(this);
	protected DFA9 dfa9 = new DFA9(this);
	protected DFA11 dfa11 = new DFA11(this);
	protected DFA12 dfa12 = new DFA12(this);
	protected DFA17 dfa17 = new DFA17(this);
	static final String DFA1_eotS = "\11\uffff";
	static final String DFA1_eofS = "\11\uffff";
	static final String DFA1_minS = "\1\60\2\55\1\60\1\uffff\2\55\1\uffff\1\55";
	static final String DFA1_maxS = "\4\172\1\uffff\2\172\1\uffff\1\172";
	static final String DFA1_acceptS = "\4\uffff\1\2\2\uffff\1\1\1\uffff";
	static final String DFA1_specialS = "\11\uffff}>";
	static final String[] DFA1_transitionS = { "\12\1\7\uffff\32\1\4\uffff\1\1\1\uffff\32\1",
			"\1\2\2\4\12\2\1\3\6\uffff\32\2\1\uffff\1\4\2\uffff\1\2\1\uffff\32\2",
			"\1\2\2\4\12\2\1\3\6\uffff\32\2\1\uffff\1\4\2\uffff\1\2\1\uffff\32\2",
			"\1\5\11\6\7\uffff\32\7\4\uffff\1\7\1\uffff\32\7", "",
			"\1\7\1\uffff\1\4\12\7\6\uffff\33\7\1\uffff\1\4\2\uffff\1\7\1\uffff\32" + "\7",
			"\1\7\1\uffff\1\4\12\10\6\uffff\33\7\1\uffff\1\4\2\uffff\1\7\1\uffff" + "\32\7", "",
			"\1\7\1\uffff\1\4\12\10\6\uffff\33\7\1\uffff\1\4\2\uffff\1\7\1\uffff" + "\32\7" };

	static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
	static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
	static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
	static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
	static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
	static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
	static final short[][] DFA1_transition;

	static {
		int numStates = DFA1_transitionS.length;
		DFA1_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
		}
	}

	protected class DFA1 extends DFA {

		public DFA1(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 1;
			this.eot = DFA1_eot;
			this.eof = DFA1_eof;
			this.min = DFA1_min;
			this.max = DFA1_max;
			this.accept = DFA1_accept;
			this.special = DFA1_special;
			this.transition = DFA1_transition;
		}

		@Override
		public String getDescription() {
			return "107:20: ( TEXT ':' TEXT '@' )?";
		}
	}

	static final String DFA5_eotS = "\11\uffff";
	static final String DFA5_eofS = "\11\uffff";
	static final String DFA5_minS = "\1\60\2\55\1\60\1\uffff\2\55\1\uffff\1\55";
	static final String DFA5_maxS = "\4\172\1\uffff\2\172\1\uffff\1\172";
	static final String DFA5_acceptS = "\4\uffff\1\2\2\uffff\1\1\1\uffff";
	static final String DFA5_specialS = "\11\uffff}>";
	static final String[] DFA5_transitionS = { "\12\1\7\uffff\32\1\4\uffff\1\1\1\uffff\32\1",
			"\1\2\2\4\12\2\1\3\6\uffff\32\2\1\uffff\1\4\2\uffff\1\2\1\uffff\32\2",
			"\1\2\2\4\12\2\1\3\6\uffff\32\2\1\uffff\1\4\2\uffff\1\2\1\uffff\32\2",
			"\1\5\11\6\7\uffff\32\7\4\uffff\1\7\1\uffff\32\7", "",
			"\1\7\1\uffff\1\4\12\7\6\uffff\33\7\1\uffff\1\4\2\uffff\1\7\1\uffff\32" + "\7",
			"\1\7\1\uffff\1\4\12\10\6\uffff\33\7\1\uffff\1\4\2\uffff\1\7\1\uffff" + "\32\7", "",
			"\1\7\1\uffff\1\4\12\10\6\uffff\33\7\1\uffff\1\4\2\uffff\1\7\1\uffff" + "\32\7" };

	static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
	static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
	static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
	static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
	static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
	static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
	static final short[][] DFA5_transition;

	static {
		int numStates = DFA5_transitionS.length;
		DFA5_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
		}
	}

	protected class DFA5 extends DFA {

		public DFA5(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 5;
			this.eot = DFA5_eot;
			this.eof = DFA5_eof;
			this.min = DFA5_min;
			this.max = DFA5_max;
			this.accept = DFA5_accept;
			this.special = DFA5_special;
			this.transition = DFA5_transition;
		}

		@Override
		public String getDescription() {
			return "110:32: ( TEXT ':' TEXT '@' )?";
		}
	}

	static final String DFA9_eotS = "\6\uffff";
	static final String DFA9_eofS = "\6\uffff";
	static final String DFA9_minS = "\1\57\1\60\2\55\2\uffff";
	static final String DFA9_maxS = "\1\134\3\172\2\uffff";
	static final String DFA9_acceptS = "\4\uffff\1\2\1\1";
	static final String DFA9_specialS = "\6\uffff}>";
	static final String[] DFA9_transitionS = { "\1\1\54\uffff\1\1", "\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
			"\1\3\1\4\1\5\12\3\7\uffff\32\3\1\uffff\1\5\2\uffff\1\3\1\uffff\32\3",
			"\1\3\1\4\1\5\12\3\7\uffff\32\3\1\uffff\1\5\2\uffff\1\3\1\uffff\32\3", "", "" };

	static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
	static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
	static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
	static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
	static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
	static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
	static final short[][] DFA9_transition;

	static {
		int numStates = DFA9_transitionS.length;
		DFA9_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
		}
	}

	protected class DFA9 extends DFA {

		public DFA9(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 9;
			this.eot = DFA9_eot;
			this.eof = DFA9_eof;
			this.min = DFA9_min;
			this.max = DFA9_max;
			this.accept = DFA9_accept;
			this.special = DFA9_special;
			this.transition = DFA9_transition;
		}

		@Override
		public String getDescription() {
			return "()* loopback of 113:4: ( PATHDELIMITER TEXT )*";
		}
	}

	static final String DFA11_eotS = "\6\uffff\2\10\1\uffff";
	static final String DFA11_eofS = "\11\uffff";
	static final String DFA11_minS = "\1\57\1\60\1\40\1\uffff\4\40\1\uffff";
	static final String DFA11_maxS = "\1\134\2\172\1\uffff\4\172\1\uffff";
	static final String DFA11_acceptS = "\3\uffff\1\1\4\uffff\1\2";
	static final String DFA11_specialS = "\11\uffff}>";
	static final String[] DFA11_transitionS = { "\1\1\54\uffff\1\1", "\12\3\7\uffff\32\2\4\uffff\1\2\1\uffff\32\2",
			"\1\5\14\uffff\1\5\1\4\1\3\12\5\7\uffff\32\5\1\uffff\1\3\2\uffff\1\5" + "\1\uffff\32\5", "",
			"\1\5\14\uffff\1\5\1\4\1\3\12\6\7\uffff\32\6\1\uffff\1\3\2\uffff\1\6" + "\1\uffff\32\6",
			"\1\5\14\uffff\1\5\1\4\1\3\12\5\7\uffff\32\5\1\uffff\1\3\2\uffff\1\5" + "\1\uffff\32\5",
			"\1\5\14\uffff\1\7\1\4\1\3\12\7\7\uffff\32\7\1\uffff\1\3\2\uffff\1\7" + "\1\uffff\32\7",
			"\1\5\14\uffff\1\7\1\4\1\3\12\7\7\uffff\32\7\1\uffff\1\3\2\uffff\1\7" + "\1\uffff\32\7", "" };

	static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
	static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
	static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
	static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
	static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
	static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
	static final short[][] DFA11_transition;

	static {
		int numStates = DFA11_transitionS.length;
		DFA11_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
		}
	}

	protected class DFA11 extends DFA {

		public DFA11(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 11;
			this.eot = DFA11_eot;
			this.eof = DFA11_eof;
			this.min = DFA11_min;
			this.max = DFA11_max;
			this.accept = DFA11_accept;
			this.special = DFA11_special;
			this.transition = DFA11_transition;
		}

		@Override
		public String getDescription() {
			return "()* loopback of 116:4: ( PATHDELIMITER ( '0' .. '9' | 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )* )*";
		}
	}

	static final String DFA12_eotS = "\3\uffff\2\5\1\uffff";
	static final String DFA12_eofS = "\6\uffff";
	static final String DFA12_minS = "\2\40\1\uffff\2\40\1\uffff";
	static final String DFA12_maxS = "\2\172\1\uffff\2\172\1\uffff";
	static final String DFA12_acceptS = "\2\uffff\1\1\2\uffff\1\2";
	static final String DFA12_specialS = "\6\uffff}>";
	static final String[] DFA12_transitionS = {
			"\1\2\14\uffff\1\2\1\1\1\uffff\12\2\7\uffff\32\2\4\uffff\1\2\1\uffff\32" + "\2",
			"\1\2\14\uffff\2\2\1\uffff\12\3\7\uffff\32\3\4\uffff\1\3\1\uffff\32\3", "",
			"\1\2\14\uffff\1\4\1\2\1\uffff\12\4\7\uffff\32\4\4\uffff\1\4\1\uffff" + "\32\4",
			"\1\2\14\uffff\1\4\1\2\1\uffff\12\4\7\uffff\32\4\4\uffff\1\4\1\uffff" + "\32\4", "" };

	static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
	static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
	static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
	static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
	static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
	static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
	static final short[][] DFA12_transition;

	static {
		int numStates = DFA12_transitionS.length;
		DFA12_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
		}
	}

	protected class DFA12 extends DFA {

		public DFA12(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 12;
			this.eot = DFA12_eot;
			this.eof = DFA12_eof;
			this.min = DFA12_min;
			this.max = DFA12_max;
			this.accept = DFA12_accept;
			this.special = DFA12_special;
			this.transition = DFA12_transition;
		}

		@Override
		public String getDescription() {
			return "()* loopback of 116:139: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '.' | '_' | '-' | ' ' )*";
		}
	}

	static final String DFA17_eotS = "\5\uffff\6\17\1\uffff\1\30\2\33\3\uffff\1\17\1\36\3\17\5\uffff\1\33\1"
			+ "\17\1\uffff\2\17\1\53\6\uffff\3\17\1\uffff\2\62\2\17\1\66\1\62\1\uffff"
			+ "\1\62\2\17\1\uffff\1\71\1\17\1\uffff\2\17\1\75\1\uffff";
	static final String DFA17_eofS = "\76\uffff";
	static final String DFA17_minS = "\1\11\4\uffff\1\170\1\146\1\162\1\163\1\164\1\72\1\uffff\1\60\2\55\3\uffff"
			+ "\1\145\1\55\1\157\1\151\1\160\2\uffff\2\40\1\uffff\1\55\1\143\1\uffff"
			+ "\1\160\1\156\1\55\1\40\1\60\1\40\1\uffff\2\40\1\165\1\145\1\147\1\uffff"
			+ "\2\40\1\164\1\162\1\55\1\40\1\uffff\1\40\1\145\1\164\1\uffff\1\55\1\151"
			+ "\1\uffff\1\145\1\163\1\55\1\uffff";
	static final String DFA17_maxS = "\1\172\4\uffff\1\170\1\146\1\162\1\163\1\164\1\72\1\uffff\3\172\3\uffff"
			+ "\1\145\1\172\1\157\1\151\1\160\2\uffff\2\172\1\uffff\1\172\1\143\1\uffff"
			+ "\1\160\1\156\4\172\1\uffff\2\172\1\165\1\145\1\147\1\uffff\2\172\1\164"
			+ "\1\162\2\172\1\uffff\1\172\1\145\1\164\1\uffff\1\172\1\151\1\uffff\1\145" + "\1\163\1\172\1\uffff";
	static final String DFA17_acceptS = "\1\uffff\1\1\1\2\1\3\1\4\6\uffff\1\12\3\uffff\1\20\1\21\1\22\5\uffff\1"
			+ "\11\1\15\2\uffff\1\17\2\uffff\1\6\6\uffff\1\14\5\uffff\1\16\6\uffff\1"
			+ "\13\3\uffff\1\10\2\uffff\1\5\3\uffff\1\7";
	static final String DFA17_specialS = "\76\uffff}>";
	static final String[] DFA17_transitionS = {
			"\2\20\2\uffff\1\20\22\uffff\1\20\1\uffff\1\13\4\uffff\1\13\1\1\1\2\2"
					+ "\uffff\1\3\2\uffff\1\14\1\15\11\16\1\uffff\1\21\1\uffff\1\4\3\uffff\32"
					+ "\12\1\uffff\1\14\2\uffff\1\17\1\uffff\4\17\1\5\1\11\10\17\1\6\1\7\4\17" + "\1\10\5\17", "", "",
			"", "", "\1\22", "\1\23", "\1\24", "\1\25", "\1\26", "\1\27", "",
			"\12\32\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
			"\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
			"\1\17\2\uffff\12\34\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17", "", "", "", "\1\35",
			"\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17", "\1\37", "\1\40", "\1\41", "", "",
			"\1\45\14\uffff\1\42\1\44\1\43\12\42\7\uffff\32\42\1\uffff\1\43\2\uffff" + "\1\42\1\uffff\32\42",
			"\1\45\14\uffff\1\46\1\47\1\43\12\46\7\uffff\32\46\1\uffff\1\43\2\uffff" + "\1\46\1\uffff\32\46", "",
			"\1\17\2\uffff\12\34\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17", "\1\50", "", "\1\51", "\1\52",
			"\1\17\2\uffff\12\17\1\27\6\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
			"\1\45\14\uffff\1\42\1\44\1\43\12\42\7\uffff\32\42\1\uffff\1\43\2\uffff" + "\1\42\1\uffff\32\42",
			"\12\32\7\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
			"\1\45\14\uffff\3\45\12\54\7\uffff\32\54\1\uffff\1\45\2\uffff\1\54\1" + "\uffff\32\54", "",
			"\1\45\14\uffff\1\46\1\47\1\43\12\46\7\uffff\32\46\1\uffff\1\43\2\uffff" + "\1\46\1\uffff\32\46",
			"\1\45\14\uffff\3\45\12\55\7\uffff\32\55\1\uffff\1\45\2\uffff\1\55\1" + "\uffff\32\55", "\1\56", "\1\57",
			"\1\60", "", "\1\45\14\uffff\1\61\2\45\12\61\7\uffff\32\61\1\uffff\1\45\2\uffff\1" + "\61\1\uffff\32\61",
			"\1\45\14\uffff\1\63\2\45\12\63\7\uffff\32\63\1\uffff\1\45\2\uffff\1" + "\63\1\uffff\32\63", "\1\64",
			"\1\65", "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
			"\1\45\14\uffff\1\61\2\45\12\61\7\uffff\32\61\1\uffff\1\45\2\uffff\1" + "\61\1\uffff\32\61", "",
			"\1\45\14\uffff\1\63\2\45\12\63\7\uffff\32\63\1\uffff\1\45\2\uffff\1" + "\63\1\uffff\32\63", "\1\67",
			"\1\70", "", "\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17", "\1\72", "", "\1\73", "\1\74",
			"\1\17\2\uffff\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17", "" };

	static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
	static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
	static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
	static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
	static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
	static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
	static final short[][] DFA17_transition;

	static {
		int numStates = DFA17_transitionS.length;
		DFA17_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
		}
	}

	protected class DFA17 extends DFA {

		public DFA17(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 17;
			this.eot = DFA17_eot;
			this.eof = DFA17_eof;
			this.min = DFA17_min;
			this.max = DFA17_max;
			this.accept = DFA17_accept;
			this.special = DFA17_special;
			this.transition = DFA17_transition;
		}

		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | URI | QUOTEDURI | PATH | PATHWITHWS | PATHDELIMITER | PROTOCOL | NUMBER | TEXT | WS | ENDSYMBOL );";
		}
	}

}
