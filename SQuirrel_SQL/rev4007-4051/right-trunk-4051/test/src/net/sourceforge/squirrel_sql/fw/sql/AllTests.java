package net.sourceforge.squirrel_sql.fw.sql;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("SQL framework tests");
        suite.addTest(new JUnit4TestAdapter(DatabaseObjectInfoTest.class));
        suite.addTestSuite(JDBCTypeMapperTest.class);
        suite.addTestSuite(QueryTokenizerTest.class);
        suite.addTest(new JUnit4TestAdapter(ResultSetReaderTest.class));
        suite.addTest(new JUnit4TestAdapter(ResultSetColumnReaderTest.class));
        suite.addTestSuite(SQLDatabaseMetaDataTest.class);
        suite.addTest(new JUnit4TestAdapter(SQLUtilitiesTest.class));
		return suite;
	}
}
