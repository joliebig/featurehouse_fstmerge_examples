

package org.jfree.experimental.chart.plot.dial.junit;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.experimental.chart.plot.dial.DialBackground;
import org.jfree.experimental.chart.plot.dial.DialCap;
import org.jfree.experimental.chart.plot.dial.DialPlot;
import org.jfree.experimental.chart.plot.dial.SimpleDialFrame;
import org.jfree.experimental.chart.plot.dial.StandardDialScale;


public class DialPlotTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DialPlotTests.class);
    }

    
    public DialPlotTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DialPlot p1 = new DialPlot();
        DialPlot p2 = new DialPlot();
        assertTrue(p1.equals(p2));
        
        
        p1.setBackground(new DialBackground(Color.green));
        assertFalse(p1.equals(p2));
        p2.setBackground(new DialBackground(Color.green));
        assertTrue(p1.equals(p2));
        
        p1.setBackground(null);
        assertFalse(p1.equals(p2));
        p2.setBackground(null);
        assertTrue(p1.equals(p2));
        
        
        DialCap cap1 = new DialCap();
        cap1.setFillPaint(Color.red);
        p1.setCap(cap1);
        assertFalse(p1.equals(p2));
        DialCap cap2 = new DialCap();
        cap2.setFillPaint(Color.red);
        p2.setCap(cap2);
        assertTrue(p1.equals(p2));
        
        p1.setCap(null);
        assertFalse(p1.equals(p2));
        p2.setCap(null);
        assertTrue(p1.equals(p2));
        
        
        SimpleDialFrame f1 = new SimpleDialFrame();
        f1.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 
                4.0f, Color.white));
        p1.setDialFrame(f1);
        assertFalse(p1.equals(p2));
        SimpleDialFrame f2 = new SimpleDialFrame();
        f2.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 
                4.0f, Color.white));
        p2.setDialFrame(f2);
        assertTrue(p1.equals(p2));
        
        
        p1.setView(0.2, 0.0, 0.8, 1.0);
        assertFalse(p1.equals(p2));
        p2.setView(0.2, 0.0, 0.8, 1.0);
        assertTrue(p1.equals(p2));
        
        
        p1.addLayer(new StandardDialScale());
        assertFalse(p1.equals(p2));
        p2.addLayer(new StandardDialScale());
        assertTrue(p1.equals(p2));
    }

    
    public void testHashCode() {
        DialPlot p1 = new DialPlot();
        DialPlot p2 = new DialPlot();
        assertTrue(p1.equals(p2));
        int h1 = p1.hashCode();
        int h2 = p2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        DialPlot p1 = new DialPlot();
        DialPlot p2 = null;
        try {
            p2 = (DialPlot) p1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(p1 != p2);
        assertTrue(p1.getClass() == p2.getClass());
        assertTrue(p1.equals(p2));
    }


    
    public void testSerialization() {
        DialPlot p1 = new DialPlot();
        DialPlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (DialPlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);
    }

}
