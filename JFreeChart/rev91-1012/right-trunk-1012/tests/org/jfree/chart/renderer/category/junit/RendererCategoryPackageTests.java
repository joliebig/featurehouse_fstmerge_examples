

package org.jfree.chart.renderer.category.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class RendererCategoryPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.renderer.category");
        suite.addTestSuite(AbstractCategoryItemRendererTests.class);
        suite.addTestSuite(AreaRendererTests.class);
        suite.addTestSuite(BarRendererTests.class);
        suite.addTestSuite(BarRenderer3DTests.class);
        suite.addTestSuite(BoxAndWhiskerRendererTests.class);
        suite.addTestSuite(CategoryStepRendererTests.class);
        suite.addTestSuite(DefaultCategoryItemRendererTests.class);
        suite.addTestSuite(GanttRendererTests.class);
        suite.addTestSuite(GroupedStackedBarRendererTests.class);
        suite.addTestSuite(IntervalBarRendererTests.class);
        suite.addTestSuite(LayeredBarRendererTests.class);
        suite.addTestSuite(LevelRendererTests.class);
        suite.addTestSuite(LineAndShapeRendererTests.class);
        suite.addTestSuite(LineRenderer3DTests.class);
        suite.addTestSuite(MinMaxCategoryRendererTests.class);
        suite.addTestSuite(ScatterRendererTests.class);
        suite.addTestSuite(StackedAreaRendererTests.class);
        suite.addTestSuite(StackedBarRendererTests.class);
        suite.addTestSuite(StackedBarRenderer3DTests.class);
        suite.addTestSuite(StatisticalBarRendererTests.class);
        suite.addTestSuite(StatisticalLineAndShapeRendererTests.class);
        suite.addTestSuite(WaterfallBarRendererTests.class);
        return suite;
    }

    
    public RendererCategoryPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
