package net.sourceforge.squirrel_sql.fw.dialects;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("SQL dialect tests");
        suite.addTestSuite(AxionDialectTest.class);
        suite.addTestSuite(DaffodilDialectTest.class);
        suite.addTestSuite(DB2DialectTest.class);
        suite.addTestSuite(DerbyDialectTest.class);
        suite.addTestSuite(FirebirdDialectTest.class);
        suite.addTestSuite(FrontBaseDialectTest.class);
        suite.addTestSuite(H2DialectTest.class);
        suite.addTestSuite(HADBDialectTest.class);
        suite.addTestSuite(HSQLDialectTest.class);
        suite.addTest(new JUnit4TestAdapter(IndexColInfoTest.class));
        suite.addTestSuite(InformixDialectTest.class);
        suite.addTestSuite(IngresDialectTest.class);
        suite.addTestSuite(InterbaseDialectTest.class);
        suite.addTestSuite(MAXDBDialectTest.class);
        suite.addTestSuite(McKoiDialectTest.class);
        suite.addTestSuite(MySQLDialectTest.class);
        suite.addTestSuite(Oracle9iDialectTest.class);
        suite.addTestSuite(PointbaseDialectTest.class);
        suite.addTestSuite(PostgreSQLDialectTest.class);
        suite.addTestSuite(SQLServerDialectTest.class);
        suite.addTestSuite(SybaseDialectTest.class);
        suite.addTestSuite(TimesTenDialectTest.class);
		return suite;
	}
}
