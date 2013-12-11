package org.adiusframework.imodel.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DroolsBasedConsistencyCheckTest {

	private static final String TEST_RULES = "/rules_checker_test.drl";
	private static final String TEST_GLOBAL_NAME = "test_protocol";

	private RuleConsistencyChecker checker;

	@Before
	public void init() {
		checker = new RuleConsistencyChecker();
		checker.setRulePath(TEST_RULES);
		checker.setGlobalProtocolIdentifier(TEST_GLOBAL_NAME);
	}

	@After
	public void close() {
		checker.close();
	}

	@Test
	public void testCheckerPass() {
		checker.addModelFact(new TestFact(true));
		checker.addModelFact(new TestFact(false));
		ConsistencyCheckerProtocol protocol = checker.check();
		assertTrue(protocol.passed());
	}

	@Test
	public void testCheckerFail() {
		checker.addModelFact(new TestFact(false));
		checker.addModelFact(new TestFact(false));
		ConsistencyCheckerProtocol protocol = checker.check();
		assertFalse(protocol.passed());
		assertTrue(protocol.getEntries().size() > 0);
	}

}
