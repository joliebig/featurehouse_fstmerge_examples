

package org.jfree.data.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data");
        suite.addTestSuite(ComparableObjectItemTests.class);
        suite.addTestSuite(ComparableObjectSeriesTests.class);
        suite.addTestSuite(DataUtilitiesTests.class);
        suite.addTestSuite(DefaultKeyedValueTests.class);
        suite.addTestSuite(DefaultKeyedValuesTests.class);
        suite.addTestSuite(DefaultKeyedValues2DTests.class);
        suite.addTestSuite(DomainOrderTests.class);
        suite.addTestSuite(KeyedObjectTests.class);
        suite.addTestSuite(KeyedObjectsTests.class);
        suite.addTestSuite(KeyedObjects2DTests.class);
        suite.addTestSuite(KeyToGroupMapTests.class);
        suite.addTestSuite(RangeTests.class);
        suite.addTestSuite(RangeTypeTests.class);
        return suite;
    }

    
    public DataPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
