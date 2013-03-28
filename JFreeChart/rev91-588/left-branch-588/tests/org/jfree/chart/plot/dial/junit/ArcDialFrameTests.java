

package org.jfree.chart.plot.dial.junit;

import java.awt.BasicStroke;
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

import org.jfree.chart.plot.dial.ArcDialFrame;


public class ArcDialFrameTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ArcDialFrameTests.class);
    }

    
    public ArcDialFrameTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        ArcDialFrame f1 = new ArcDialFrame();
        ArcDialFrame f2 = new ArcDialFrame();
        assertTrue(f1.equals(f2));
        
        
        f1.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.yellow));
        assertFalse(f1.equals(f2));
        f2.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.yellow));
        assertTrue(f1.equals(f2));
        
        
        f1.setForegroundPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.yellow));
        assertFalse(f1.equals(f2));
        f2.setForegroundPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.yellow));
        assertTrue(f1.equals(f2));
        
        
        f1.setStroke(new BasicStroke(1.1f));
        assertFalse(f1.equals(f2));
        f2.setStroke(new BasicStroke(1.1f));
        assertTrue(f1.equals(f2));
        
        
        f1.setInnerRadius(0.11);
        assertFalse(f1.equals(f2));
        f2.setInnerRadius(0.11);
        assertTrue(f1.equals(f2));
        
        
        f1.setOuterRadius(0.88);
        assertFalse(f1.equals(f2));
        f2.setOuterRadius(0.88);
        assertTrue(f1.equals(f2));
        
        
        f1.setStartAngle(99);
        assertFalse(f1.equals(f2));
        f2.setStartAngle(99);
        assertTrue(f1.equals(f2));
     
        
        f1.setExtent(33);
        assertFalse(f1.equals(f2));
        f2.setExtent(33);
        assertTrue(f1.equals(f2));
        
        
        f1.setVisible(false);
        assertFalse(f1.equals(f2));
        f2.setVisible(false);
        assertTrue(f1.equals(f2));
    }

    
    public void testHashCode() {
        ArcDialFrame f1 = new ArcDialFrame();
        ArcDialFrame f2 = new ArcDialFrame();
        assertTrue(f1.equals(f2));
        int h1 = f1.hashCode();
        int h2 = f2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        ArcDialFrame f1 = new ArcDialFrame();
        ArcDialFrame f2 = null;
        try {
            f2 = (ArcDialFrame) f1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(f1 != f2);
        assertTrue(f1.getClass() == f2.getClass());
        assertTrue(f1.equals(f2));
        
        
        MyDialLayerChangeListener l1 = new MyDialLayerChangeListener();
        f1.addChangeListener(l1);
        assertTrue(f1.hasListener(l1));
        assertFalse(f2.hasListener(l1));
    }

    
    public void testSerialization() {
        ArcDialFrame f1 = new ArcDialFrame();
        ArcDialFrame f2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(f1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            f2 = (ArcDialFrame) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(f1, f2);
    }

}
