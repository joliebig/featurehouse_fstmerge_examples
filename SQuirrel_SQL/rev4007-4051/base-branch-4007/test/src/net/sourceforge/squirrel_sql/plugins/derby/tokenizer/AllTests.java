package net.sourceforge.squirrel_sql.plugins.derby.tokenizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("derby tokenizer tests");
		suite.addTestSuite(DerbyQueryTokenizerTest.class);
		return suite;
	}
}
