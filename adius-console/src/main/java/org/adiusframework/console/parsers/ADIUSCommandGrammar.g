grammar ADIUSCommandGrammar;

options {
    // antlr will generate java lexer and parser
    language = Java;
}

@parser::header {
	package org.adiusframework.console.parsers;
	
	import org.adiusframework.query.QueryFactory;
	import org.adiusframework.query.Query;
	import java.util.UUID;
	import java.util.Map;
	import java.util.HashMap;
	import org.apache.log4j.Logger;
}

@lexer::header {
	package org.adiusframework.console.parsers;
}

@parser::members {
	
	private static final Logger LOGGER = Logger.getLogger(ADIUSCommandGrammarParser.class);

	public static Query parseExecute(QueryFactory factory, String expression) {
		ANTLRStringStream in = new ANTLRStringStream(expression);
		ADIUSCommandGrammarLexer lexer = new ADIUSCommandGrammarLexer(in);
        	CommonTokenStream tokens = new CommonTokenStream(lexer);
        	ADIUSCommandGrammarParser parser = new ADIUSCommandGrammarParser(tokens);
        	Query query = parser.parseExecute(factory);
        	LOGGER.debug("Generated query " + query);
        	return query;
	}
	
}

@rulecatch {
   	catch (RecognitionException e) {
   		System.err.println(e.getMessage());
    		return null;
   	}
}

parseExecute [QueryFactory factory] returns [Query query]
	:	'execute' WS 
		type = TEXT WS 'of' WS 
		domain = TEXT
		(WS 'using' WS '(' WS? paramMap = paramLiterals')')?
		(WS 'properties' WS '(' WS? propertyMap = paramLiterals')')?
		WS? ENDSYMBOL
		{
			LOGGER.debug("Type: " + $type.text);
			LOGGER.debug("Domain: " + $domain.text);
			LOGGER.debug("ParamMap: " + $paramMap.params);
			LOGGER.debug("ParamMap: " + $propertyMap.params);
			query = factory.create($type.text, $domain.text, $paramMap.params, $propertyMap.params);
		}
	;

paramLiterals returns [Map<String, String> params]
	@init 	{params = new HashMap<String,String>();}
	:	first_key= TEXT '=' first_value= paramValueLiteral
		{
			params.put($first_key.text, $first_value.value);
		} 
		(WS? ',' WS? 
		next_key= TEXT '=' next_value= paramValueLiteral
		{
			params.put($next_key.text, $next_value.value);
		})*
	;

paramValueLiteral returns [String value]
	: 	(uri_value= uriLiteral|text_value= textLiteral)
	 	{
			if ($uri_value.value != null) {
				$value = $uri_value.value;
			} else {
				$value = $text_value.value;
			}
		}
	;

textLiteral returns [String value]
	:	textvalue = TEXT
		{
			$value = $textvalue.text;
		}
	;

uriLiteral returns [String value]
	:	urivalue = URI	
		{
			$value = $urivalue.text;
		}
	|	quotedurivalue = QUOTEDURI
		{
			$value = $quotedurivalue.text;
      			$value = $value.substring(1, $value.length()-1); // remove leading- and trailing quotes
		        $value = $value.replace("\"\"", "\""); // replace all `""` with `"`
		}
	;

URI
	:	(PROTOCOL '://' (TEXT ':' TEXT '@')? TEXT ('.' TEXT)? (':' NUMBER)? PATH) | (('A'..'Z') ':' PATH);

QUOTEDURI 
	:	('\''|'"') ((PROTOCOL '://' (TEXT ':' TEXT '@')? TEXT ('.' TEXT)? (':' NUMBER)? PATHWITHWS ) | (('A'..'Z') ':' PATHWITHWS)) ('\''|'"');
	
PATH
	:	(PATHDELIMITER TEXT)* (PATHDELIMITER TEXT '.' TEXT);
	
PATHWITHWS
	:	(PATHDELIMITER ('0'..'9'|'a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_'|'-'|' ')*)* (PATHDELIMITER ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'_'|'-'|' ')* '.' TEXT);

PATHDELIMITER
	:	'/'
	|	'\\';

PROTOCOL
	:	'ftp';
	
NUMBER
	:	'0' | '1'..'9' ('0'..'9')*;

TEXT
	:	('a'..'z'|'A'..'Z'|'0'..'9'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*;
		
WS
    	: 	(' ' | '\t' | '\r'| '\n')+;
    	
ENDSYMBOL
	:	';';