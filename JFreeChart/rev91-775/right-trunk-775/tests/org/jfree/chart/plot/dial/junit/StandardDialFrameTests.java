

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

import org.jfree.chart.plot.dial.StandardDialFrame;


public class StandardDialFrameTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardDialFrameTests.class);
    }

    
    public StandardDialFrameTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardDialFrame f1 = new StandardDialFrame();
        StandardDialFrame f2 = new StandardDialFrame();
        assertTrue(f1.equals(f2));

        
        f1.setRadius(0.2);
        assertFalse(f1.equals(f2));
        f2.setRadius(0.2);
        assertTrue(f1.equals(f2));
        
        
        f1.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.white, 3.0f,
                4.0f, Color.yellow));
        assertFalse(f1.equals(f2));
        f2.setBackgroundPaint(new GradientPaint(1.0f, 2.0f, Color.white, 3.0f,
                4.0f, Color.yellow));
        assertTrue(f1.equals(f2));
        
        
        f1.setForegroundPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 3.0f,
                4.0f, Color.green));
        assertFalse(f1.equals(f2));
        f2.setForegroundPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 3.0f,
                4.0f, Color.green));
        assertTrue(f1.equals(f2));
        
        
        f1.setStroke(new BasicStroke(2.4f));
        assertFalse(f1.equals(f2));
        f2.setStroke(new BasicStroke(2.4f));
        assertTrue(f1.equals(f2));
        
        
        f1.setVisible(false);
        assertFalse(f1.equals(f2));
        f2.setVisible(false);
        assertTrue(f1.equals(f2));
    }

    
    public void testHashCode() {
        StandardDialFrame f1 = new StandardDialFrame();
        StandardDialFrame f2 = new StandardDialFrame();
        assertTrue(f1.equals(f2));
        int h1 = f1.hashCode();
        int h2 = f2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        StandardDialFrame f1 = new StandardDialFrame();
        StandardDialFrame f2 = null;
        try {
            f2 = (StandardDialFrame) f1.clone();
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
        StandardDialFrame f1 = new StandardDialFrame();
        StandardDialFrame f2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(f1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            f2 = (StandardDialFrame) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(f1, f2);
    }

}
