

package org.jfree.chart.renderer.junit;

import java.awt.geom.Point2D;
import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.Outlier;



public class OutlierTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(OutlierTests.class);
    }

    
    public OutlierTests(String name) {
        super(name);
    }
    
    private static final double EPSILON = 0.000000001;
    
    
    public void testConstructor() {
        Outlier out = new Outlier(1.0, 2.0, 3.0);
        assertEquals(-2.0, out.getX(), EPSILON);
        assertEquals(-1.0, out.getY(), EPSILON);
        assertEquals(3.0, out.getRadius(), EPSILON);
    }
        
    
    public void testEquals() {
        Outlier out1 = new Outlier(1.0, 2.0, 3.0);
        Outlier out2 = new Outlier(1.0, 2.0, 3.0);
        assertTrue(out1.equals(out2));
        assertTrue(out2.equals(out1));
        
        out1.setPoint(new Point2D.Double(2.0, 2.0));
        assertFalse(out1.equals(out2));
        out2.setPoint(new Point2D.Double(2.0, 2.0));
        assertTrue(out1.equals(out2));

        out1.setPoint(new Point2D.Double(2.0, 3.0));
        assertFalse(out1.equals(out2));
        out2.setPoint(new Point2D.Double(2.0, 3.0));
        assertTrue(out1.equals(out2));

        out1.setRadius(4.0);
        assertFalse(out1.equals(out2));
        out2.setRadius(4.0);
        assertTrue(out1.equals(out2));
    }
    
    
    public void testCloning() {
        Outlier out1 = new Outlier(1.0, 2.0, 3.0);
        assertFalse(out1 instanceof Cloneable);
    }
    
    
    public void testSerialization() {
        Outlier out1 = new Outlier(1.0, 2.0, 3.0);
        assertFalse(out1 instanceof Serializable);
    }

}

