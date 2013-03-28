

package org.jfree.chart.title.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class TitlePackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.title");
        suite.addTestSuite(CompositeTitleTests.class);
        suite.addTestSuite(DateTitleTests.class);
        suite.addTestSuite(ImageTitleTests.class);
        suite.addTestSuite(LegendGraphicTests.class);
        suite.addTestSuite(LegendTitleTests.class);
        suite.addTestSuite(PaintScaleLegendTests.class);
        suite.addTestSuite(ShortTextTitleTests.class);
        suite.addTestSuite(TextTitleTests.class);
        suite.addTestSuite(TitleTests.class);
        return suite;
    }

    
    public TitlePackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
