

package org.jfree.chart.axis.junit;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;


public class SubCategoryAxisTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(SubCategoryAxisTests.class);
    }

    
    public SubCategoryAxisTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        SubCategoryAxis a1 = new SubCategoryAxis("Test");
        SubCategoryAxis a2 = new SubCategoryAxis("Test");
        assertTrue(a1.equals(a2));
        assertTrue(a2.equals(a1));

        
        a1.addSubCategory("Sub 1");
        assertFalse(a1.equals(a2));
        a2.addSubCategory("Sub 1");
        assertTrue(a1.equals(a2));

        
        a1.setSubLabelFont(new Font("Serif", Font.BOLD, 15));
        assertFalse(a1.equals(a2));
        a2.setSubLabelFont(new Font("Serif", Font.BOLD, 15));
        assertTrue(a1.equals(a2));

        
        a1.setSubLabelPaint(Color.red);
        assertFalse(a1.equals(a2));
        a2.setSubLabelPaint(Color.red);
        assertTrue(a1.equals(a2));

    }

    
    public void testHashCode() {
        SubCategoryAxis a1 = new SubCategoryAxis("Test");
        SubCategoryAxis a2 = new SubCategoryAxis("Test");
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        SubCategoryAxis a1 = new SubCategoryAxis("Test");
        a1.addSubCategory("SubCategoryA");
        SubCategoryAxis a2 = null;
        try {
            a2 = (SubCategoryAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    
    public void testSerialization() {
        SubCategoryAxis a1 = new SubCategoryAxis("Test Axis");
        a1.addSubCategory("SubCategoryA");
        SubCategoryAxis a2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (SubCategoryAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);
    }

    
    public void test2275695() {
        JFreeChart chart = ChartFactory.createStackedBarChart("Test",
                "Category", "Value", null, PlotOrientation.VERTICAL,
                true, false, false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(new SubCategoryAxis("SubCategoryAxis"));
        boolean success = false;
        try {
            BufferedImage image = new BufferedImage(200 , 100,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();
            chart.draw(g2, new Rectangle2D.Double(0, 0, 200, 100), null, null);
            g2.dispose();
            success = true;
        }
        catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        assertTrue(success);
    }

}
