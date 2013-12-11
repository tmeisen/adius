package org.adiusframework.console;

import org.adiusframework.console.parsers.ADIUSCommandGrammarParser;
import org.adiusframework.processmanager.ProcessManager;
import org.adiusframework.query.Query;
import org.adiusframework.query.QueryFactory;
import org.adiusframework.util.jms.JmsCallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.remoting.RemoteAccessException;

public class BasicCommandParser implements CommandParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicCommandParser.class);

	private ProcessManager processManager;

	private QueryFactory queryFactory;

	private JmsCallbackFactory callbackFactory;

	@Required
	public ProcessManager getProcessManager() {
		return processManager;
	}

	public void setProcessManager(ProcessManager processManager) {
		this.processManager = processManager;
	}

	@Required
	public QueryFactory getQueryFactory() {
		return queryFactory;
	}

	public void setQueryFactory(QueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Required
	public JmsCallbackFactory getCallbackFactory() {
		return callbackFactory;
	}

	public void setCallbackFactory(JmsCallbackFactory callbackFactory) {
		this.callbackFactory = callbackFactory;
	}

	@Override
	public boolean parse(String command) {
		Query query = ADIUSCommandGrammarParser.parseExecute(getQueryFactory(), command);
		if (query != null) {
			try {
				getProcessManager().handleQuery(query, getCallbackFactory().create("testMessage", null));
			} catch (RemoteAccessException e) {
				LOGGER.error("Sending of message failed due to timeout");
				return false;
			}
			return true;
		}
		return false;
	}

}
