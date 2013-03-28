package tests.net.sf.jabref.labelPattern;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(
            "Test for tests.net.sf.jabref.labelPattern");
        
        suite.addTestSuite(LabelPatternUtilTest.class);
        
        return suite;
    }

}
