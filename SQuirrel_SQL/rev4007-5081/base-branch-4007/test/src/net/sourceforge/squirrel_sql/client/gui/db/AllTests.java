package net.sourceforge.squirrel_sql.client.gui.db;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("Client GUI DB tests");
        suite.addTestSuite(SQLAliasTest.class);
		return suite;
	}
}
