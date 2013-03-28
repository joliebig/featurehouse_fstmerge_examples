

package org.jfree.chart.renderer.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.jfree.chart.renderer.GrayPaintScale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



public class GrayPaintScaleTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(GrayPaintScaleTests.class);
    }

    
    public GrayPaintScaleTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        GrayPaintScale g1 = new GrayPaintScale();
        GrayPaintScale g2 = new GrayPaintScale();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
        
        g1 = new GrayPaintScale(0.0, 1.0);
        g2 = new GrayPaintScale(0.0, 1.0);
        assertTrue(g1.equals(g2));
        g1 = new GrayPaintScale(0.1, 1.0);
        assertFalse(g1.equals(g2));
        g2 = new GrayPaintScale(0.1, 1.0);
        assertTrue(g1.equals(g2));
        
        g1 = new GrayPaintScale(0.1, 0.9);
        assertFalse(g1.equals(g2));
        g2 = new GrayPaintScale(0.1, 0.9);
        assertTrue(g1.equals(g2));
    }
    
    
    public void testCloning() {
        GrayPaintScale g1 = new GrayPaintScale();
        GrayPaintScale g2 = null;
        try {
            g2 = (GrayPaintScale) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }
    
    
    public void testSerialization() {
        GrayPaintScale g1 = new GrayPaintScale();
        GrayPaintScale g2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (GrayPaintScale) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);       
    }

}
