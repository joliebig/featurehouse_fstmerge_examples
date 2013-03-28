

package org.jfree.data.category.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataCategoryPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.category");
        suite.addTestSuite(CategoryToPieDatasetTests.class);
        suite.addTestSuite(DefaultCategoryDatasetTests.class);
        suite.addTestSuite(DefaultIntervalCategoryDatasetTests.class);
        return suite;
    }

    
    public DataCategoryPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
