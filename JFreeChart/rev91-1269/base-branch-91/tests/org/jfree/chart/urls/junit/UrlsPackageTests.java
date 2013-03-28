

package org.jfree.chart.urls.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class UrlsPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.urls");
        suite.addTestSuite(CustomXYURLGeneratorTests.class);
        suite.addTestSuite(StandardCategoryURLGeneratorTests.class);
        suite.addTestSuite(StandardPieURLGeneratorTests.class);
        suite.addTestSuite(StandardXYURLGeneratorTests.class);
        suite.addTestSuite(TimeSeriesURLGeneratorTests.class);
        return suite;
    }

    
    public UrlsPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}
