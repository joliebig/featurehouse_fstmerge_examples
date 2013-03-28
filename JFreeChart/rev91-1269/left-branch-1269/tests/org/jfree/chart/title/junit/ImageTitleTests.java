

package org.jfree.chart.title.junit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.ImageTitle;
import org.jfree.ui.Size2D;


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

        t1.setImage(new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB));
        assertFalse(t1.equals(t2));
        t2.setImage(new BufferedImage(2, 1, BufferedImage.TYPE_INT_RGB));
        
        
        assertFalse(t1.equals(t2));
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
            e.printStackTrace();
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

    
    public void testArrangeNN() {
        BufferedImage image = new BufferedImage(100, 100,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        ImageTitle t = new ImageTitle(JFreeChart.INFO.getLogo());
        Size2D s = t.arrange(g2);
        assertEquals(102.0, s.getWidth(), EPSILON);
        assertEquals(102.0, s.getHeight(), EPSILON);

        t.setPadding(1.0, 2.0, 3.0, 4.0);
        s = t.arrange(g2);
        assertEquals(106.0, s.getWidth(), EPSILON);
        assertEquals(104.0, s.getHeight(), EPSILON);

        t.setMargin(5.0, 6.0, 7.0, 8.0);
        s = t.arrange(g2);
        assertEquals(120.0, s.getWidth(), EPSILON);
        assertEquals(116.0, s.getHeight(), EPSILON);
    }

}
