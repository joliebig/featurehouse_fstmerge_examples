package tests.net.sf.jabref.util;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for tests.net.sf.jabref.util");
		
		suite.addTestSuite(CaseChangerTest.class);
		suite.addTestSuite(XMPUtilTest.class);
		suite.addTestSuite(XMPSchemaBibtexTest.class);
		
		return suite;
	}

}
