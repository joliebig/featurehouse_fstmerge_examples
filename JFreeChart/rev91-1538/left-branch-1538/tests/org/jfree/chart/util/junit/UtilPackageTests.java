

package org.jfree.chart.util.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class UtilPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.util");
        suite.addTestSuite(LogFormatTests.class);
        suite.addTestSuite(RelativeDateFormatTests.class);
        return suite;
    }

    
    public UtilPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
