

package org.jfree.chart.plot.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class PlotPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.plot");
        suite.addTestSuite(CategoryMarkerTests.class);
        suite.addTestSuite(CategoryPlotTests.class);
        suite.addTestSuite(CombinedDomainCategoryPlotTests.class);
        suite.addTestSuite(CombinedDomainXYPlotTests.class);
        suite.addTestSuite(CombinedRangeCategoryPlotTests.class);
        suite.addTestSuite(CombinedRangeXYPlotTests.class);
        suite.addTestSuite(CompassPlotTests.class);
        suite.addTestSuite(DefaultDrawingSupplierTests.class);
        suite.addTestSuite(FastScatterPlotTests.class);
        suite.addTestSuite(IntervalMarkerTests.class);
        suite.addTestSuite(MarkerTests.class);
        suite.addTestSuite(MeterIntervalTests.class);
        suite.addTestSuite(MeterPlotTests.class);
        suite.addTestSuite(MultiplePiePlotTests.class);
        suite.addTestSuite(PiePlotTests.class);
        suite.addTestSuite(PiePlot3DTests.class);
        suite.addTestSuite(PlotOrientationTests.class);
        suite.addTestSuite(PlotRenderingInfoTests.class);
        suite.addTestSuite(PlotTests.class);
        suite.addTestSuite(PolarPlotTests.class);
        suite.addTestSuite(RingPlotTests.class);
        suite.addTestSuite(SpiderWebPlotTests.class);
        suite.addTestSuite(ThermometerPlotTests.class);
        suite.addTestSuite(ValueMarkerTests.class);
        suite.addTestSuite(XYPlotTests.class);
        return suite;
    }

    
    public PlotPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
