

package org.jfree.data.time.junit;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DataTimePackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.time");
        suite.addTestSuite(DateRangeTests.class);
        suite.addTestSuite(DayTests.class);
        suite.addTestSuite(FixedMillisecondTests.class);
        suite.addTestSuite(HourTests.class);
        suite.addTestSuite(MinuteTests.class);
        suite.addTestSuite(MillisecondTests.class);
        suite.addTestSuite(MonthTests.class);
        suite.addTestSuite(MovingAverageTests.class);
        suite.addTestSuite(QuarterTests.class);
        suite.addTestSuite(SecondTests.class);
        suite.addTestSuite(SimpleTimePeriodTests.class);
        suite.addTestSuite(TimePeriodAnchorTests.class);
        suite.addTestSuite(TimePeriodValueTests.class);
        suite.addTestSuite(TimePeriodValuesTests.class);
        suite.addTestSuite(TimePeriodValuesCollectionTests.class);
        suite.addTestSuite(TimeSeriesCollectionTests.class);
        suite.addTestSuite(TimeSeriesTests.class);
        suite.addTestSuite(TimeSeriesDataItemTests.class);
        suite.addTestSuite(TimeTableXYDatasetTests.class);
        suite.addTestSuite(WeekTests.class);
        suite.addTestSuite(YearTests.class);
        return suite;
    }

    
    public DataTimePackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
