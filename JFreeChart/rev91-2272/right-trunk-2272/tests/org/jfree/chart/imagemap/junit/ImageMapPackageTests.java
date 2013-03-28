

package org.jfree.chart.imagemap.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class ImageMapPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.imagemap");
        suite.addTestSuite(DynamicDriveToolTipTagFragmentGeneratorTests.class);
        suite.addTestSuite(ImageMapUtilitiesTests.class);
        suite.addTestSuite(OverLIBToolTipTagFragmentGeneratorTests.class);
        suite.addTestSuite(StandardToolTipTagFragmentGeneratorTests.class);
        suite.addTestSuite(StandardURLTagFragmentGeneratorTests.class);
        return suite;
    }

    
    public ImageMapPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}