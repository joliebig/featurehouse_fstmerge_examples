

package org.jfree.chart.title.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.ImageTitle;


public class ImageTitleTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ImageTitleTests.class);
    }

    
    public ImageTitleTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        ImageTitle t1 = new ImageTitle(JFreeChart.INFO.getLogo());
        ImageTitle t2 = new ImageTitle(JFreeChart.INFO.getLogo());
        assertEquals(t1, t2);        
    }

    
    public void testHashcode() {
        ImageTitle t1 = new ImageTitle(JFreeChart.INFO.getLogo());
        ImageTitle t2 = new ImageTitle(JFreeChart.INFO.getLogo());
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        ImageTitle t1 = new ImageTitle(JFreeChart.INFO.getLogo());
        ImageTitle t2 = null;
        try {
            t2 = (ImageTitle) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("ImageTitleTests.testCloning: failed to clone.");
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {

        

    }
    
    private static final double EPSILON = 0.00000001;
    
    
    public void testWidthAndHeight() {
        ImageTitle t1 = new ImageTitle(JFreeChart.INFO.getLogo());
        assertEquals(100, t1.getWidth(), EPSILON);
        assertEquals(100, t1.getHeight(), EPSILON);
    }

}
