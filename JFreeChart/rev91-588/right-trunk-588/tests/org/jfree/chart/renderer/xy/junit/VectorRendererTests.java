

package org.jfree.chart.renderer.xy.junit;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.xy.VectorRenderer;


public class VectorRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(VectorRendererTests.class);
    }

    
    public VectorRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {        
        
        VectorRenderer r1 = new VectorRenderer();
        VectorRenderer r2 = new VectorRenderer();
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));
        
        
        r1.setSeriesFillPaint(0, Color.green);
        assertFalse(r1.equals(r2));
        r2.setSeriesFillPaint(0, Color.green);
        assertTrue(r1.equals(r2));   
    }

    
    public void testHashcode() {
        VectorRenderer r1 = new VectorRenderer();
        VectorRenderer r2 = new VectorRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        VectorRenderer r1 = new VectorRenderer();
        VectorRenderer r2 = null;
        try {
            r2 = (VectorRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testSerialization() {
        VectorRenderer r1 = new VectorRenderer();
        VectorRenderer r2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (VectorRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
    }

}
