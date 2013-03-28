

package org.jfree.data.function.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataFunctionPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.function");
        suite.addTestSuite(LineFunction2DTests.class);
        suite.addTestSuite(NormalDistributionFunction2DTests.class);
        suite.addTestSuite(PolynomialFunction2DTests.class);
        suite.addTestSuite(PowerFunction2DTests.class);
        return suite;
    }

    
    public DataFunctionPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}

