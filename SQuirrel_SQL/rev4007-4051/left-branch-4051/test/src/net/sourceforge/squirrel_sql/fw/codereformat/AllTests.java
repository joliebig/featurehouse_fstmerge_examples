package net.sourceforge.squirrel_sql.fw.codereformat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("SQL codereformat tests");
        suite.addTestSuite(CodeReformatorTest.class);
		return suite;
	}
}
