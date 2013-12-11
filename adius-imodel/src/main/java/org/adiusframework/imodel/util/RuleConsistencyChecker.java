package org.adiusframework.imodel.util;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class RuleConsistencyChecker implements ModelConsistencyChecker {

	private static final String GLOBAL_PROTOCOL_IDENTIFIER = "protocol";

	private StatefulKnowledgeSession ksession;

	private String rulePath;

	private String globalProtocolIdentifier;

	public RuleConsistencyChecker() {
		setGlobalProtocolIdentifier(GLOBAL_PROTOCOL_IDENTIFIER);
	}

	public String getGlobalProtocolIdentifier() {
		return globalProtocolIdentifier;
	}

	public void setGlobalProtocolIdentifier(String globalProtocolIdentifier) {
		if (globalProtocolIdentifier == null || globalProtocolIdentifier.isEmpty()) {
			throw new IllegalArgumentException("Global protocol identifier cannot be null or empty");
		}
		this.globalProtocolIdentifier = globalProtocolIdentifier;
	}

	public String getRulePath() {
		return rulePath;
	}

	public void setRulePath(String rulePath) {
		this.rulePath = rulePath;
	}

	public void init() {

		// check the configuration
		if (getRulePath() == null || getRulePath().isEmpty()) {
			throw new IllegalStateException("Path to ruleset has not been set");
		}

		// lets initialize the knowledge builder and check for errors
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(getRulePath(), RuleConsistencyChecker.class),
				ResourceType.DRL);
		if (kbuilder.hasErrors()) {
			throw new IllegalStateException("Failed to build knowledge base: " + kbuilder.getErrors().toString());
		}

		// and finally lets initialize the session
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		ksession = kbase.newStatefulKnowledgeSession();
	}

	public void close() {
		if (ksession != null) {
			ksession.dispose();
		}
		ksession = null;
	}

	@Override
	public ConsistencyCheckerProtocol check() {
		if (ksession == null) {
			init();
		}
		BasicCheckerProtocol protocol = new BasicCheckerProtocol();
		ksession.setGlobal(getGlobalProtocolIdentifier(), protocol);
		ksession.fireAllRules();
		return protocol;
	}

	@Override
	public <T> void addModelFact(T fact) {
		if (ksession == null) {
			init();
		}
		ksession.insert(fact);
	}

	@Override
	public void addModelFacts(Object... facts) {
		for (Object fact : facts) {
			addModelFact(fact);
		}
	}

}
