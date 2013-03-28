

package org.jfree.data.gantt.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataGanttPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.gantt");
        suite.addTestSuite(SlidingGanttCategoryDatasetTests.class);
        suite.addTestSuite(TaskTests.class);
        suite.addTestSuite(TaskSeriesTests.class);
        suite.addTestSuite(TaskSeriesCollectionTests.class);
        suite.addTestSuite(XYTaskDatasetTests.class);
        return suite;
    }

    
    public DataGanttPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
