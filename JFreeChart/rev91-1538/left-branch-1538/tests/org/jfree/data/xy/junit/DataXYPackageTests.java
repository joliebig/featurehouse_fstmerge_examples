

package org.jfree.data.xy.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataXYPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.xy");
        suite.addTestSuite(CategoryTableXYDatasetTests.class);
        suite.addTestSuite(DefaultHighLowDatasetTests.class);
        suite.addTestSuite(DefaultIntervalXYDatasetTests.class);
        suite.addTestSuite(DefaultOHLCDatasetTests.class);
        suite.addTestSuite(DefaultTableXYDatasetTests.class);
        suite.addTestSuite(DefaultWindDatasetTests.class);
        suite.addTestSuite(DefaultXYDatasetTests.class);
        suite.addTestSuite(DefaultXYZDatasetTests.class);
        suite.addTestSuite(IntervalXYDelegateTests.class);
        suite.addTestSuite(MatrixSeriesCollectionTests.class);
        suite.addTestSuite(MatrixSeriesTests.class);
        suite.addTestSuite(OHLCDataItemTests.class);
        suite.addTestSuite(TableXYDatasetTests.class);
        suite.addTestSuite(VectorDataItemTests.class);
        suite.addTestSuite(VectorSeriesCollectionTests.class);
        suite.addTestSuite(VectorSeriesTests.class);
        suite.addTestSuite(VectorTests.class);
        suite.addTestSuite(XIntervalDataItemTests.class);
        suite.addTestSuite(XIntervalSeriesCollectionTests.class);
        suite.addTestSuite(XIntervalSeriesTests.class);
        suite.addTestSuite(XYBarDatasetTests.class);
        suite.addTestSuite(XYCoordinateTests.class);
        suite.addTestSuite(XYDataItemTests.class);
        suite.addTestSuite(XYIntervalDataItemTests.class);
        suite.addTestSuite(XYIntervalSeriesCollectionTests.class);
        suite.addTestSuite(XYIntervalSeriesTests.class);
        suite.addTestSuite(XYIntervalTests.class);
        suite.addTestSuite(XYSeriesCollectionTests.class);
        suite.addTestSuite(XYSeriesTests.class);
        suite.addTestSuite(YIntervalDataItemTests.class);
        suite.addTestSuite(YIntervalSeriesCollectionTests.class);
        suite.addTestSuite(YIntervalSeriesTests.class);
        suite.addTestSuite(YIntervalTests.class);
        suite.addTestSuite(YWithXIntervalTests.class);
        return suite;
    }

    
    public DataXYPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
