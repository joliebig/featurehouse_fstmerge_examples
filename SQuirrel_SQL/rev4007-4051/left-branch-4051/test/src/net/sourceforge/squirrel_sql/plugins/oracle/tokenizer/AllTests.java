package net.sourceforge.squirrel_sql.plugins.oracle.tokenizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("oracle tokenizer tests");
		suite.addTestSuite(OracleQueryTokenizerTest.class);
		return suite;
	}
}
