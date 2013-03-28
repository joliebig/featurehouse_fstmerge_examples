

package org.jfree.experimental.chart.plot.dial.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DialPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite 
                = new TestSuite("org.jfree.experimental.chart.plot.dial");
        suite.addTestSuite(DialBackgroundTests.class);
        suite.addTestSuite(DialCapTests.class);
        suite.addTestSuite(DialPlotTests.class);
        suite.addTestSuite(DialPointerTests.class);
        suite.addTestSuite(DialTextAnnotationTests.class);
        suite.addTestSuite(DialValueIndicatorTests.class);
        suite.addTestSuite(SimpleDialFrameTests.class);
        suite.addTestSuite(StandardDialFrameTests.class);
        suite.addTestSuite(StandardDialRangeTests.class);
        suite.addTestSuite(StandardDialScaleTests.class);
        return suite;
    }

    
    public DialPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}

