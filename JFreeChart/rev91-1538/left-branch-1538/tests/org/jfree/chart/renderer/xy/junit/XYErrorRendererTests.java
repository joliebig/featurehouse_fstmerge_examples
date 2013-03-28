

package org.jfree.chart.renderer.xy.junit;

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

import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.util.PublicCloneable;


public class XYErrorRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYErrorRendererTests.class);
    }

    
    public XYErrorRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        XYErrorRenderer r1 = new XYErrorRenderer();
        XYErrorRenderer r2 = new XYErrorRenderer();
        assertEquals(r1, r2);

        
        r1.setDrawXError(false);
        assertFalse(r1.equals(r2));
        r2.setDrawXError(false);
        assertTrue(r1.equals(r2));

        
        r1.setDrawYError(false);
        assertFalse(r1.equals(r2));
        r2.setDrawYError(false);
        assertTrue(r1.equals(r2));

        
        r1.setCapLength(9.0);
        assertFalse(r1.equals(r2));
        r2.setCapLength(9.0);
        assertTrue(r1.equals(r2));

        
        r1.setErrorPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.green));
        assertFalse(r1.equals(r2));
        r2.setErrorPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.green));
        assertTrue(r1.equals(r2));
    }

    
    public void testHashcode() {
        XYErrorRenderer r1 = new XYErrorRenderer();
        XYErrorRenderer r2 = new XYErrorRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        XYErrorRenderer r1 = new XYErrorRenderer();
        r1.setErrorPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.white));
        XYErrorRenderer r2 = null;
        try {
            r2 = (XYErrorRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        XYErrorRenderer r1 = new XYErrorRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        XYErrorRenderer r1 = new XYErrorRenderer();
        r1.setErrorPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.white));
        XYErrorRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (XYErrorRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

}
