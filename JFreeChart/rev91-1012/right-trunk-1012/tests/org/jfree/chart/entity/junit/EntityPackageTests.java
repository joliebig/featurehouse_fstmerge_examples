

package org.jfree.chart.entity.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class EntityPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.entity");
        suite.addTestSuite(CategoryItemEntityTests.class);
        suite.addTestSuite(LegendItemEntityTests.class);
        suite.addTestSuite(PieSectionEntityTests.class);
        suite.addTestSuite(StandardEntityCollectionTests.class);
        suite.addTestSuite(TickLabelEntityTests.class);
        suite.addTestSuite(XYItemEntityTests.class);
        return suite;
    }

    
    public EntityPackageTests(String name) {
        super(name);
    }
    
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}

