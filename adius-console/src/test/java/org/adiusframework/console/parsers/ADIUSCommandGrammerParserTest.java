package org.adiusframework.console.parsers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.adiusframework.query.Query;
import org.adiusframework.query.QueryFactory;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ADIUSCommandGrammerParserTest {

	private static HashMap<String, String> COMMANDS;
	static {
		COMMANDS = new HashMap<String, String>();
		COMMANDS.put("FILE WITHOUT QUOTES",
				"execute integrate of testdomain using (input_file=D:/Upload/dbBase.mdb,process=process1,process_step=step1);");
		COMMANDS.put("FTP WITH SINGLE QUOTES",
				"execute integrate of testdomain using (input_file='ftp://default:default@localhost/upload/dbBase.mdb');");
		COMMANDS.put("FTP WITH DOUBLE QUOTES",
				"execute integrate of testdomain using (input_file=\"ftp://default:default@localhost/upload/dbBase.mdb\");");
	}
	private static QueryFactory FACTORY;
	private static Query QUERY;
	static {
		FACTORY = mock(QueryFactory.class);
		QUERY = mock(Query.class);
		when(FACTORY.create(anyString(), anyString(), any(Map.class), any(Map.class))).thenReturn(QUERY);
	}

	@Test
	public void testCommands() {
		for (Entry<String, String> entry : COMMANDS.entrySet()) {
			System.out.println("Testing " + entry.getKey());
			Query query = ADIUSCommandGrammarParser.parseExecute(FACTORY, entry.getValue());
			assertTrue(query != null);
		}
	}

}
