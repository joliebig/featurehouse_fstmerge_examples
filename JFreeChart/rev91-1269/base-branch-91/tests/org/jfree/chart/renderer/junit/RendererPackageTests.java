

package org.jfree.chart.renderer.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class RendererPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.renderer");
        suite.addTestSuite(AbstractRendererTests.class);
        suite.addTestSuite(AreaRendererEndTypeTests.class);
        suite.addTestSuite(DefaultPolarItemRendererTests.class);
        suite.addTestSuite(GrayPaintScaleTests.class);
        suite.addTestSuite(LookupPaintScaleTests.class);
        return suite;
    }

    
    public RendererPackageTests(String name) {
        super(name);
    }

}
