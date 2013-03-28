package net.sourceforge.squirrel_sql.plugins.dbcopy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("dbcopy tests");
		suite.addTestSuite(ColTypeMapperTest.class);
		return suite;
	}
}
