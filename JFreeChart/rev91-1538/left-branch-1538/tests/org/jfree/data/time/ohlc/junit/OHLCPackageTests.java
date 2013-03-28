

package org.jfree.data.time.ohlc.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class OHLCPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.time.ohlc");
        suite.addTestSuite(OHLCItemTests.class);
        suite.addTestSuite(OHLCSeriesCollectionTests.class);
        suite.addTestSuite(OHLCSeriesTests.class);
        suite.addTestSuite(OHLCTests.class);
        return suite;
    }

    
    public OHLCPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
