package org.adiusframework.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SystemUtilTest {
	@Test
	public void testGetSystemDescribtion() {
		assertTrue(SystemUtil.getSystemDescription().matches(
				"Java Version\\:\\s+.+\\s+at\\s+.+\\s+OS\\:\\s+.*\\s+\\(.*\\/.*\\)"));
	}
}
