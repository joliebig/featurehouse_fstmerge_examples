

package org.jfree.data.statistics.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataStatisticsPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.statistics");
        suite.addTestSuite(BoxAndWhiskerCalculatorTests.class);
        suite.addTestSuite(BoxAndWhiskerItemTests.class);
        suite.addTestSuite(DefaultBoxAndWhiskerCategoryDatasetTests.class);
        suite.addTestSuite(DefaultBoxAndWhiskerXYDatasetTests.class);
        suite.addTestSuite(DefaultStatisticalCategoryDatasetTests.class);
        suite.addTestSuite(HistogramBinTests.class);
        suite.addTestSuite(HistogramDatasetTests.class);
        suite.addTestSuite(MeanAndStandardDeviationTests.class);
        suite.addTestSuite(RegressionTests.class);
        suite.addTestSuite(SimpleHistogramBinTests.class);
        suite.addTestSuite(SimpleHistogramDatasetTests.class);
        suite.addTestSuite(StatisticsTests.class);
        return suite;
    }

    
    public DataStatisticsPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
