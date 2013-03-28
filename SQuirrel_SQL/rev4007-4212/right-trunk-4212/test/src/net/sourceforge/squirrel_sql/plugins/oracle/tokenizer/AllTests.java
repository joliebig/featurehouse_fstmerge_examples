package net.sourceforge.squirrel_sql.plugins.oracle.tokenizer;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("oracle tokenizer tests");
		suite.addTest(new JUnit4TestAdapter(OracleQueryTokenizerTest.class));
		return suite;
	}
}
