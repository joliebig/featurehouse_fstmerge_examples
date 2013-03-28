

package org.jfree.chart.renderer.xy.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class RendererXYPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.renderer.xy");
        suite.addTestSuite(AbstractXYItemRendererTests.class);
        suite.addTestSuite(CandlestickRendererTests.class);
        suite.addTestSuite(ClusteredXYBarRendererTests.class);
        suite.addTestSuite(DeviationRendererTests.class);
        suite.addTestSuite(HighLowRendererTests.class);
        suite.addTestSuite(StackedXYAreaRendererTests.class);
        suite.addTestSuite(StackedXYAreaRenderer2Tests.class);
        suite.addTestSuite(StackedXYBarRendererTests.class);
        suite.addTestSuite(StandardXYItemRendererTests.class);
        suite.addTestSuite(VectorRendererTests.class);
        suite.addTestSuite(WindItemRendererTests.class);
        suite.addTestSuite(XYAreaRendererTests.class);
        suite.addTestSuite(XYAreaRenderer2Tests.class);
        suite.addTestSuite(XYBarRendererTests.class);
        suite.addTestSuite(XYBlockRendererTests.class);
        suite.addTestSuite(XYBoxAndWhiskerRendererTests.class);
        suite.addTestSuite(XYBubbleRendererTests.class);
        suite.addTestSuite(XYDifferenceRendererTests.class);
        suite.addTestSuite(XYDotRendererTests.class);
        suite.addTestSuite(XYErrorRendererTests.class);
        suite.addTestSuite(XYLineAndShapeRendererTests.class);
        suite.addTestSuite(XYLine3DRendererTests.class);
        suite.addTestSuite(XYStepRendererTests.class);
        suite.addTestSuite(XYStepAreaRendererTests.class);
        suite.addTestSuite(YIntervalRendererTests.class);
        return suite;
    }

    
    public RendererXYPackageTests(String name) {
        super(name);
    }
    
    
    public static XYSeriesCollection createTestXYSeriesCollection() {
        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Series 1", false, false);
        series1.add(1.0, 2.0);
        series1.add(2.0, 5.0);
        XYSeries series2 = new XYSeries("Series 2", false, false);
        series2.add(1.0, 4.0);
        series2.add(2.0, 3.0);
        result.addSeries(series1);
        result.addSeries(series2);
        return result;
    }
    
    
    public static TableXYDataset createTestTableXYDataset() {
        DefaultTableXYDataset result = new DefaultTableXYDataset();
        XYSeries series1 = new XYSeries("Series 1", false, false);
        series1.add(1.0, 2.0);
        series1.add(2.0, 5.0);
        XYSeries series2 = new XYSeries("Series 2", false, false);
        series2.add(1.0, 4.0);
        series2.add(2.0, 3.0);
        result.addSeries(series1);
        result.addSeries(series2);
        return result;
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
