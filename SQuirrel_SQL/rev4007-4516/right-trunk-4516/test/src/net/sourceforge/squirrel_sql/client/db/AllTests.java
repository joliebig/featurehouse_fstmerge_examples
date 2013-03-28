package net.sourceforge.squirrel_sql.client.db;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("client db tests");
		suite.addTest(new JUnit4TestAdapter(AliasGroupTest.class));
		return suite;
	}
}
