// $ANTLR 3.5 D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g 2013-01-28 15:04:46

package org.adiusframework.console.parsers;

import org.adiusframework.query.QueryFactory;
import org.adiusframework.query.Query;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ADIUSCommandGrammarParser extends Parser {
	public static final String[] tokenNames = new String[] { "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ENDSYMBOL",
			"NUMBER", "PATH", "PATHDELIMITER", "PATHWITHWS", "PROTOCOL", "QUOTEDURI", "TEXT", "URI", "WS", "'('",
			"')'", "','", "'='", "'execute'", "'of'", "'properties'", "'using'" };
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
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

	// delegators

	public ADIUSCommandGrammarParser(TokenStream input) {
		this(input, new RecognizerSharedState());
	}

	public ADIUSCommandGrammarParser(TokenStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override
	public String[] getTokenNames() {
		return ADIUSCommandGrammarParser.tokenNames;
	}

	@Override
	public String getGrammarFileName() {
		return "D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g";
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ADIUSCommandGrammarParser.class);

	public static Query parseExecute(QueryFactory factory, String expression) {
		ANTLRStringStream in = new ANTLRStringStream(expression);
		ADIUSCommandGrammarLexer lexer = new ADIUSCommandGrammarLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ADIUSCommandGrammarParser parser = new ADIUSCommandGrammarParser(tokens);
		Query query = parser.parseExecute(factory);
		LOGGER.debug("Generated query " + query);
		return query;
	}

	// $ANTLR start "parseExecute"
	// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:46:1:
	// parseExecute[QueryFactory factory] returns [Query query] : 'execute' WS
	// type= TEXT WS 'of' WS domain= TEXT ( WS 'using' WS '(' ( WS )? paramMap=
	// paramLiterals ')' )? ( WS 'properties' WS '(' ( WS )? propertyMap=
	// paramLiterals ')' )? ( WS )? ENDSYMBOL ;
	public final Query parseExecute(QueryFactory factory) {
		Query query = null;

		Token type = null;
		Token domain = null;
		Map<String, String> paramMap = null;
		Map<String, String> propertyMap = null;

		try {
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:47:2:
			// ( 'execute' WS type= TEXT WS 'of' WS domain= TEXT ( WS 'using' WS
			// '(' ( WS )? paramMap= paramLiterals ')' )? ( WS 'properties' WS
			// '(' ( WS )? propertyMap= paramLiterals ')' )? ( WS )? ENDSYMBOL )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:47:4:
			// 'execute' WS type= TEXT WS 'of' WS domain= TEXT ( WS 'using' WS
			// '(' ( WS )? paramMap= paramLiterals ')' )? ( WS 'properties' WS
			// '(' ( WS )? propertyMap= paramLiterals ')' )? ( WS )? ENDSYMBOL
			{
				match(input, 18, FOLLOW_18_in_parseExecute71);
				match(input, WS, FOLLOW_WS_in_parseExecute73);
				type = (Token) match(input, TEXT, FOLLOW_TEXT_in_parseExecute82);
				match(input, WS, FOLLOW_WS_in_parseExecute84);
				match(input, 19, FOLLOW_19_in_parseExecute86);
				match(input, WS, FOLLOW_WS_in_parseExecute88);
				domain = (Token) match(input, TEXT, FOLLOW_TEXT_in_parseExecute97);
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:50:3:
				// ( WS 'using' WS '(' ( WS )? paramMap= paramLiterals ')' )?
				int alt2 = 2;
				int LA2_0 = input.LA(1);
				if ((LA2_0 == WS)) {
					int LA2_1 = input.LA(2);
					if ((LA2_1 == 21)) {
						alt2 = 1;
					}
				}
				switch (alt2) {
				case 1:
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:50:4:
				// WS 'using' WS '(' ( WS )? paramMap= paramLiterals ')'
				{
					match(input, WS, FOLLOW_WS_in_parseExecute102);
					match(input, 21, FOLLOW_21_in_parseExecute104);
					match(input, WS, FOLLOW_WS_in_parseExecute106);
					match(input, 14, FOLLOW_14_in_parseExecute108);
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:50:22:
					// ( WS )?
					int alt1 = 2;
					int LA1_0 = input.LA(1);
					if ((LA1_0 == WS)) {
						alt1 = 1;
					}
					switch (alt1) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:50:22:
					// WS
					{
						match(input, WS, FOLLOW_WS_in_parseExecute110);
					}
						break;

					}

					pushFollow(FOLLOW_paramLiterals_in_parseExecute117);
					paramMap = paramLiterals();
					state._fsp--;

					match(input, 15, FOLLOW_15_in_parseExecute118);
				}
					break;

				}

				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:51:3:
				// ( WS 'properties' WS '(' ( WS )? propertyMap= paramLiterals
				// ')' )?
				int alt4 = 2;
				int LA4_0 = input.LA(1);
				if ((LA4_0 == WS)) {
					int LA4_1 = input.LA(2);
					if ((LA4_1 == 20)) {
						alt4 = 1;
					}
				}
				switch (alt4) {
				case 1:
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:51:4:
				// WS 'properties' WS '(' ( WS )? propertyMap= paramLiterals ')'
				{
					match(input, WS, FOLLOW_WS_in_parseExecute125);
					match(input, 20, FOLLOW_20_in_parseExecute127);
					match(input, WS, FOLLOW_WS_in_parseExecute129);
					match(input, 14, FOLLOW_14_in_parseExecute131);
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:51:27:
					// ( WS )?
					int alt3 = 2;
					int LA3_0 = input.LA(1);
					if ((LA3_0 == WS)) {
						alt3 = 1;
					}
					switch (alt3) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:51:27:
					// WS
					{
						match(input, WS, FOLLOW_WS_in_parseExecute133);
					}
						break;

					}

					pushFollow(FOLLOW_paramLiterals_in_parseExecute140);
					propertyMap = paramLiterals();
					state._fsp--;

					match(input, 15, FOLLOW_15_in_parseExecute141);
				}
					break;

				}

				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:52:3:
				// ( WS )?
				int alt5 = 2;
				int LA5_0 = input.LA(1);
				if ((LA5_0 == WS)) {
					alt5 = 1;
				}
				switch (alt5) {
				case 1:
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:52:3:
				// WS
				{
					match(input, WS, FOLLOW_WS_in_parseExecute147);
				}
					break;

				}

				match(input, ENDSYMBOL, FOLLOW_ENDSYMBOL_in_parseExecute150);

				LOGGER.debug("Type: " + (type != null ? type.getText() : null));
				LOGGER.debug("Domain: " + (domain != null ? domain.getText() : null));
				LOGGER.debug("ParamMap: " + paramMap);
				LOGGER.debug("ParamMap: " + propertyMap);
				query = factory.create((type != null ? type.getText() : null), (domain != null ? domain.getText()
						: null), paramMap, propertyMap);

			}

		}

		catch (RecognitionException e) {
			System.err.println(e.getMessage());
			return null;
		}

		finally {
			// do for sure before leaving
		}
		return query;
	}

	// $ANTLR end "parseExecute"

	// $ANTLR start "paramLiterals"
	// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:62:1:
	// paramLiterals returns [Map<String, String> params] : first_key= TEXT '='
	// first_value= paramValueLiteral ( ( WS )? ',' ( WS )? next_key= TEXT '='
	// next_value= paramValueLiteral )* ;
	public final Map<String, String> paramLiterals() {
		Map<String, String> params = null;

		Token first_key = null;
		Token next_key = null;
		String first_value = null;
		String next_value = null;

		params = new HashMap<String, String>();
		try {
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:64:2:
			// (first_key= TEXT '=' first_value= paramValueLiteral ( ( WS )? ','
			// ( WS )? next_key= TEXT '=' next_value= paramValueLiteral )* )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:64:4:
			// first_key= TEXT '=' first_value= paramValueLiteral ( ( WS )? ','
			// ( WS )? next_key= TEXT '=' next_value= paramValueLiteral )*
			{
				first_key = (Token) match(input, TEXT, FOLLOW_TEXT_in_paramLiterals179);
				match(input, 17, FOLLOW_17_in_paramLiterals181);
				pushFollow(FOLLOW_paramValueLiteral_in_paramLiterals186);
				first_value = paramValueLiteral();
				state._fsp--;

				params.put((first_key != null ? first_key.getText() : null), first_value);

				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:68:3:
				// ( ( WS )? ',' ( WS )? next_key= TEXT '=' next_value=
				// paramValueLiteral )*
				loop8: while (true) {
					int alt8 = 2;
					int LA8_0 = input.LA(1);
					if ((LA8_0 == WS || LA8_0 == 16)) {
						alt8 = 1;
					}

					switch (alt8) {
					case 1:
					// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:68:4:
					// ( WS )? ',' ( WS )? next_key= TEXT '=' next_value=
					// paramValueLiteral
					{
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:68:4:
						// ( WS )?
						int alt6 = 2;
						int LA6_0 = input.LA(1);
						if ((LA6_0 == WS)) {
							alt6 = 1;
						}
						switch (alt6) {
						case 1:
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:68:4:
						// WS
						{
							match(input, WS, FOLLOW_WS_in_paramLiterals196);
						}
							break;

						}

						match(input, 16, FOLLOW_16_in_paramLiterals199);
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:68:12:
						// ( WS )?
						int alt7 = 2;
						int LA7_0 = input.LA(1);
						if ((LA7_0 == WS)) {
							alt7 = 1;
						}
						switch (alt7) {
						case 1:
						// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:68:12:
						// WS
						{
							match(input, WS, FOLLOW_WS_in_paramLiterals201);
						}
							break;

						}

						next_key = (Token) match(input, TEXT, FOLLOW_TEXT_in_paramLiterals210);
						match(input, 17, FOLLOW_17_in_paramLiterals212);
						pushFollow(FOLLOW_paramValueLiteral_in_paramLiterals217);
						next_value = paramValueLiteral();
						state._fsp--;

						params.put((next_key != null ? next_key.getText() : null), next_value);

					}
						break;

					default:
						break loop8;
					}
				}

			}

		}

		catch (RecognitionException e) {
			System.err.println(e.getMessage());
			return null;
		}

		finally {
			// do for sure before leaving
		}
		return params;
	}

	// $ANTLR end "paramLiterals"

	// $ANTLR start "paramValueLiteral"
	// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:75:1:
	// paramValueLiteral returns [String value] : (uri_value= uriLiteral
	// |text_value= textLiteral ) ;
	public final String paramValueLiteral() {
		String value = null;

		String uri_value = null;
		String text_value = null;

		try {
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:76:2:
			// ( (uri_value= uriLiteral |text_value= textLiteral ) )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:76:5:
			// (uri_value= uriLiteral |text_value= textLiteral )
			{
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:76:5:
				// (uri_value= uriLiteral |text_value= textLiteral )
				int alt9 = 2;
				int LA9_0 = input.LA(1);
				if ((LA9_0 == QUOTEDURI || LA9_0 == URI)) {
					alt9 = 1;
				} else if ((LA9_0 == TEXT)) {
					alt9 = 2;
				}

				else {
					NoViableAltException nvae = new NoViableAltException("", 9, 0, input);
					throw nvae;
				}

				switch (alt9) {
				case 1:
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:76:6:
				// uri_value= uriLiteral
				{
					pushFollow(FOLLOW_uriLiteral_in_paramValueLiteral243);
					uri_value = uriLiteral();
					state._fsp--;

				}
					break;
				case 2:
				// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:76:28:
				// text_value= textLiteral
				{
					pushFollow(FOLLOW_textLiteral_in_paramValueLiteral248);
					text_value = textLiteral();
					state._fsp--;

				}
					break;

				}

				if (uri_value != null) {
					value = uri_value;
				} else {
					value = text_value;
				}

			}

		}

		catch (RecognitionException e) {
			System.err.println(e.getMessage());
			return null;
		}

		finally {
			// do for sure before leaving
		}
		return value;
	}

	// $ANTLR end "paramValueLiteral"

	// $ANTLR start "textLiteral"
	// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:86:1:
	// textLiteral returns [String value] : textvalue= TEXT ;
	public final String textLiteral() {
		String value = null;

		Token textvalue = null;

		try {
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:87:2:
			// (textvalue= TEXT )
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:87:4:
			// textvalue= TEXT
			{
				textvalue = (Token) match(input, TEXT, FOLLOW_TEXT_in_textLiteral273);

				value = (textvalue != null ? textvalue.getText() : null);

			}

		}

		catch (RecognitionException e) {
			System.err.println(e.getMessage());
			return null;
		}

		finally {
			// do for sure before leaving
		}
		return value;
	}

	// $ANTLR end "textLiteral"

	// $ANTLR start "uriLiteral"
	// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:93:1:
	// uriLiteral returns [String value] : (urivalue= URI |quotedurivalue=
	// QUOTEDURI );
	public final String uriLiteral() {
		String value = null;

		Token urivalue = null;
		Token quotedurivalue = null;

		try {
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:94:2:
			// (urivalue= URI |quotedurivalue= QUOTEDURI )
			int alt10 = 2;
			int LA10_0 = input.LA(1);
			if ((LA10_0 == URI)) {
				alt10 = 1;
			} else if ((LA10_0 == QUOTEDURI)) {
				alt10 = 2;
			}

			else {
				NoViableAltException nvae = new NoViableAltException("", 10, 0, input);
				throw nvae;
			}

			switch (alt10) {
			case 1:
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:94:4:
			// urivalue= URI
			{
				urivalue = (Token) match(input, URI, FOLLOW_URI_in_uriLiteral296);

				value = (urivalue != null ? urivalue.getText() : null);

			}
				break;
			case 2:
			// D:\\Repositories\\vpi\\02_dev\\03_workspace\\adius-console\\src\\main\\java\\org\\adiusframework\\console\\parsers\\ADIUSCommandGrammar.g:98:4:
			// quotedurivalue= QUOTEDURI
			{
				quotedurivalue = (Token) match(input, QUOTEDURI, FOLLOW_QUOTEDURI_in_uriLiteral310);

				value = (quotedurivalue != null ? quotedurivalue.getText() : null);
				value = value.substring(1, value.length() - 1); // remove
																// leading- and
																// trailing
																// quotes
				value = value.replace("\"\"", "\""); // replace all `""` with
														// `"`

			}
				break;

			}
		}

		catch (RecognitionException e) {
			System.err.println(e.getMessage());
			return null;
		}

		finally {
			// do for sure before leaving
		}
		return value;
	}

	// $ANTLR end "uriLiteral"

	// Delegated rules

	public static final BitSet FOLLOW_18_in_parseExecute71 = new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet FOLLOW_WS_in_parseExecute73 = new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet FOLLOW_TEXT_in_parseExecute82 = new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet FOLLOW_WS_in_parseExecute84 = new BitSet(new long[] { 0x0000000000080000L });
	public static final BitSet FOLLOW_19_in_parseExecute86 = new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet FOLLOW_WS_in_parseExecute88 = new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet FOLLOW_TEXT_in_parseExecute97 = new BitSet(new long[] { 0x0000000000002010L });
	public static final BitSet FOLLOW_WS_in_parseExecute102 = new BitSet(new long[] { 0x0000000000200000L });
	public static final BitSet FOLLOW_21_in_parseExecute104 = new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet FOLLOW_WS_in_parseExecute106 = new BitSet(new long[] { 0x0000000000004000L });
	public static final BitSet FOLLOW_14_in_parseExecute108 = new BitSet(new long[] { 0x0000000000002800L });
	public static final BitSet FOLLOW_WS_in_parseExecute110 = new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet FOLLOW_paramLiterals_in_parseExecute117 = new BitSet(new long[] { 0x0000000000008000L });
	public static final BitSet FOLLOW_15_in_parseExecute118 = new BitSet(new long[] { 0x0000000000002010L });
	public static final BitSet FOLLOW_WS_in_parseExecute125 = new BitSet(new long[] { 0x0000000000100000L });
	public static final BitSet FOLLOW_20_in_parseExecute127 = new BitSet(new long[] { 0x0000000000002000L });
	public static final BitSet FOLLOW_WS_in_parseExecute129 = new BitSet(new long[] { 0x0000000000004000L });
	public static final BitSet FOLLOW_14_in_parseExecute131 = new BitSet(new long[] { 0x0000000000002800L });
	public static final BitSet FOLLOW_WS_in_parseExecute133 = new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet FOLLOW_paramLiterals_in_parseExecute140 = new BitSet(new long[] { 0x0000000000008000L });
	public static final BitSet FOLLOW_15_in_parseExecute141 = new BitSet(new long[] { 0x0000000000002010L });
	public static final BitSet FOLLOW_WS_in_parseExecute147 = new BitSet(new long[] { 0x0000000000000010L });
	public static final BitSet FOLLOW_ENDSYMBOL_in_parseExecute150 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_TEXT_in_paramLiterals179 = new BitSet(new long[] { 0x0000000000020000L });
	public static final BitSet FOLLOW_17_in_paramLiterals181 = new BitSet(new long[] { 0x0000000000001C00L });
	public static final BitSet FOLLOW_paramValueLiteral_in_paramLiterals186 = new BitSet(
			new long[] { 0x0000000000012002L });
	public static final BitSet FOLLOW_WS_in_paramLiterals196 = new BitSet(new long[] { 0x0000000000010000L });
	public static final BitSet FOLLOW_16_in_paramLiterals199 = new BitSet(new long[] { 0x0000000000002800L });
	public static final BitSet FOLLOW_WS_in_paramLiterals201 = new BitSet(new long[] { 0x0000000000000800L });
	public static final BitSet FOLLOW_TEXT_in_paramLiterals210 = new BitSet(new long[] { 0x0000000000020000L });
	public static final BitSet FOLLOW_17_in_paramLiterals212 = new BitSet(new long[] { 0x0000000000001C00L });
	public static final BitSet FOLLOW_paramValueLiteral_in_paramLiterals217 = new BitSet(
			new long[] { 0x0000000000012002L });
	public static final BitSet FOLLOW_uriLiteral_in_paramValueLiteral243 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_textLiteral_in_paramValueLiteral248 = new BitSet(
			new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_TEXT_in_textLiteral273 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_URI_in_uriLiteral296 = new BitSet(new long[] { 0x0000000000000002L });
	public static final BitSet FOLLOW_QUOTEDURI_in_uriLiteral310 = new BitSet(new long[] { 0x0000000000000002L });
}
