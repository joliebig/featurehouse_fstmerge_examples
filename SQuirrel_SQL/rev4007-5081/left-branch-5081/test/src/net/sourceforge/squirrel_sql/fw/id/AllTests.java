package net.sourceforge.squirrel_sql.fw.id;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("fw id tests");
		suite.addTestSuite(UidIdentifierTest.class);
        suite.addTestSuite(IntegerIdentifierTest.class);
		return suite;
	}
}
