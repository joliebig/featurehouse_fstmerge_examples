

package org.jfree.chart.title.junit;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;


public class LegendTitleTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(LegendTitleTests.class);
    }

    
    public LegendTitleTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        XYPlot plot1 = new XYPlot();
        LegendTitle t1 = new LegendTitle(plot1);
        LegendTitle t2 = new LegendTitle(plot1);
        assertEquals(t1, t2);
        
        t1.setBackgroundPaint(
            new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, Color.yellow)
        );
        assertFalse(t1.equals(t2));
        t2.setBackgroundPaint(
            new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, Color.yellow)
        );
        assertTrue(t1.equals(t2));
        
        t1.setLegendItemGraphicEdge(RectangleEdge.BOTTOM);
        assertFalse(t1.equals(t2));
        t2.setLegendItemGraphicEdge(RectangleEdge.BOTTOM);
        assertTrue(t1.equals(t2));
        
        t1.setLegendItemGraphicAnchor(RectangleAnchor.BOTTOM_LEFT);
        assertFalse(t1.equals(t2));
        t2.setLegendItemGraphicAnchor(RectangleAnchor.BOTTOM_LEFT);
        assertTrue(t1.equals(t2));
        
        t1.setLegendItemGraphicLocation(RectangleAnchor.TOP_LEFT);
        assertFalse(t1.equals(t2));
        t2.setLegendItemGraphicLocation(RectangleAnchor.TOP_LEFT);
        assertTrue(t1.equals(t2));
        
        t1.setItemFont(new Font("Dialog", Font.PLAIN, 19));
        assertFalse(t1.equals(t2));
        t2.setItemFont(new Font("Dialog", Font.PLAIN, 19));
        assertTrue(t1.equals(t2));
    }

    
    public void testHashcode() {
        XYPlot plot1 = new XYPlot();
        LegendTitle t1 = new LegendTitle(plot1);
        LegendTitle t2 = new LegendTitle(plot1);
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        XYPlot plot = new XYPlot();
        Rectangle2D bounds1 = new Rectangle2D.Double(10.0, 20.0, 30.0, 40.0);
        LegendTitle t1 = new LegendTitle(plot);
        t1.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 
                4.0f, Color.yellow));
        t1.setBounds(bounds1);
        LegendTitle t2 = null;
        try {
            t2 = (LegendTitle) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
        
        
        bounds1.setFrame(40.0, 30.0, 20.0, 10.0);
        assertFalse(t1.equals(t2));
        t2.setBounds(new Rectangle2D.Double(40.0, 30.0, 20.0, 10.0));
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {

        XYPlot plot = new XYPlot();
        LegendTitle t1 = new LegendTitle(plot);
        LegendTitle t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (LegendTitle) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertTrue(t1.equals(t2));
        assertTrue(t2.getSources()[0].equals(plot));
    }

}
