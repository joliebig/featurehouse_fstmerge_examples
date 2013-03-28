

package org.jfree.chart.annotations.junit;

import java.awt.Image;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.util.RectangleAnchor;


public class XYImageAnnotationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYImageAnnotationTests.class);
    }

    
    public XYImageAnnotationTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        Image image = JFreeChart.INFO.getLogo();
        XYImageAnnotation a1 = new XYImageAnnotation(10.0, 20.0, image);
        XYImageAnnotation a2 = new XYImageAnnotation(10.0, 20.0, image);
        assertTrue(a1.equals(a2));
        
        a1 = new XYImageAnnotation(10.0, 20.0, image, RectangleAnchor.LEFT);
        assertFalse(a1.equals(a2));
        a2 = new XYImageAnnotation(10.0, 20.0, image, RectangleAnchor.LEFT);
        assertTrue(a1.equals(a2));
    }

    
    public void testHashCode() {
        Image image = JFreeChart.INFO.getLogo();
        XYImageAnnotation a1 = new XYImageAnnotation(10.0, 20.0, image);
        XYImageAnnotation a2 = new XYImageAnnotation(10.0, 20.0, image);
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        XYImageAnnotation a1 = new XYImageAnnotation(10.0, 20.0, 
                JFreeChart.INFO.getLogo());
        XYImageAnnotation a2 = null;
        try {
            a2 = (XYImageAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }


























}
