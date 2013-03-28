package tests.net.sf.jabref.search;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for tests.net.sf.jabref.search");
		
		suite.addTestSuite(BasicSearchTest.class);
		
		return suite;
	}

}
