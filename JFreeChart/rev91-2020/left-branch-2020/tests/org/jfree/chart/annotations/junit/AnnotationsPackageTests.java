

package org.jfree.chart.annotations.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class AnnotationsPackageTests extends TestCase {

    
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.chart.annotations");
        suite.addTestSuite(CategoryLineAnnotationTests.class);
        suite.addTestSuite(CategoryPointerAnnotationTests.class);
        suite.addTestSuite(CategoryTextAnnotationTests.class);
        suite.addTestSuite(TextAnnotationTests.class);
        suite.addTestSuite(XYBoxAnnotationTests.class);
        suite.addTestSuite(XYDrawableAnnotationTests.class);
        suite.addTestSuite(XYImageAnnotationTests.class);
        suite.addTestSuite(XYLineAnnotationTests.class);
        suite.addTestSuite(XYPointerAnnotationTests.class);
        suite.addTestSuite(XYPolygonAnnotationTests.class);
        suite.addTestSuite(XYShapeAnnotationTests.class);
        suite.addTestSuite(XYTextAnnotationTests.class);
        suite.addTestSuite(XYTitleAnnotationTests.class);
        return suite;
    }

    
    public AnnotationsPackageTests(String name) {
        super(name);
    }

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

}

