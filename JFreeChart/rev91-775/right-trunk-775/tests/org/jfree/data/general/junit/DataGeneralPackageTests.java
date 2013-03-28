

package org.jfree.data.general.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataGeneralPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.general");
        suite.addTestSuite(DatasetGroupTests.class);
        suite.addTestSuite(DatasetUtilitiesTests.class);
        suite.addTestSuite(DefaultKeyedValueDatasetTests.class);
        suite.addTestSuite(DefaultKeyedValuesDatasetTests.class);
        suite.addTestSuite(DefaultKeyedValues2DDatasetTests.class);
        suite.addTestSuite(DefaultPieDatasetTests.class);
        return suite;
    }

    
    public DataGeneralPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
