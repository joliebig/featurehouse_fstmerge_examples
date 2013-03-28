

package org.jfree.chart.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class ChartPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart");
        suite.addTestSuite(AreaChartTests.class);
        suite.addTestSuite(BarChartTests.class);
        suite.addTestSuite(BarChart3DTests.class);
        suite.addTestSuite(ChartPanelTests.class);
        suite.addTestSuite(ChartRenderingInfoTests.class);
        suite.addTestSuite(GanttChartTests.class);
        suite.addTestSuite(HashUtilitiesTests.class);
        suite.addTestSuite(JFreeChartTests.class);
        suite.addTestSuite(LegendItemTests.class);
        suite.addTestSuite(LegendItemCollectionTests.class);
        suite.addTestSuite(LineChartTests.class);
        suite.addTestSuite(LineChart3DTests.class);
        suite.addTestSuite(MeterChartTests.class);
        suite.addTestSuite(PaintMapTests.class);
        suite.addTestSuite(PieChartTests.class);
        suite.addTestSuite(PieChart3DTests.class);
        suite.addTestSuite(ScatterPlotTests.class);
        suite.addTestSuite(StackedAreaChartTests.class);
        suite.addTestSuite(StackedBarChartTests.class);
        suite.addTestSuite(StackedBarChart3DTests.class);
        suite.addTestSuite(StandardChartThemeTests.class);
        suite.addTestSuite(StrokeMapTests.class);
        suite.addTestSuite(TimeSeriesChartTests.class);
        suite.addTestSuite(WaterfallChartTests.class);
        suite.addTestSuite(XYAreaChartTests.class);
        suite.addTestSuite(XYBarChartTests.class);
        suite.addTestSuite(XYLineChartTests.class);
        suite.addTestSuite(XYStepAreaChartTests.class);
        suite.addTestSuite(XYStepChartTests.class);
        return suite;
    }

}
