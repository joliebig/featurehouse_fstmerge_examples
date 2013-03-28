

package org.jfree.chart.labels.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class LabelsPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.labels");
        suite.addTestSuite(BoxAndWhiskerToolTipGeneratorTests.class);
        suite.addTestSuite(BoxAndWhiskerXYToolTipGeneratorTests.class);
        suite.addTestSuite(BubbleXYItemLabelGeneratorTests.class);
        suite.addTestSuite(CustomXYItemLabelGeneratorTests.class);
        suite.addTestSuite(HighLowItemLabelGeneratorTests.class);
        suite.addTestSuite(IntervalCategoryLabelGeneratorTests.class);
        suite.addTestSuite(ItemLabelAnchorTests.class);
        suite.addTestSuite(ItemLabelPositionTests.class);
        suite.addTestSuite(MultipleXYSeriesLabelGeneratorTests.class);
        suite.addTestSuite(StandardCategoryItemLabelGeneratorTests.class);
        suite.addTestSuite(StandardCategorySeriesLabelGeneratorTests.class);
        suite.addTestSuite(StandardCategoryToolTipGeneratorTests.class);
        suite.addTestSuite(StandardContourToolTipGeneratorTests.class);
        suite.addTestSuite(StandardPieSectionLabelGeneratorTests.class);
        suite.addTestSuite(StandardPieToolTipGeneratorTests.class);
        suite.addTestSuite(StandardXYItemLabelGeneratorTests.class);
        suite.addTestSuite(StandardXYSeriesLabelGeneratorTests.class);
        suite.addTestSuite(StandardXYToolTipGeneratorTests.class);
        suite.addTestSuite(StandardXYZToolTipGeneratorTests.class);
        suite.addTestSuite(SymbolicXYItemLabelGeneratorTests.class);
        return suite;
    }

    
    public LabelsPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
