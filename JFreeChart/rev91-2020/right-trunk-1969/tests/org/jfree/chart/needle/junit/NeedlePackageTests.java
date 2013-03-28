

package org.jfree.chart.needle.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class NeedlePackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.needle");
        suite.addTestSuite(ArrowNeedleTests.class);
        suite.addTestSuite(LineNeedleTests.class);
        suite.addTestSuite(LongNeedleTests.class);
        suite.addTestSuite(MeterNeedleTests.class);
        suite.addTestSuite(MiddlePinNeedleTests.class);
        suite.addTestSuite(PinNeedleTests.class);
        suite.addTestSuite(PlumNeedleTests.class);
        suite.addTestSuite(PointerNeedleTests.class);
        suite.addTestSuite(ShipNeedleTests.class);
        suite.addTestSuite(WindNeedleTests.class);
        return suite;
    }

    
    public NeedlePackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
