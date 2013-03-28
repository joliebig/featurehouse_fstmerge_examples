

package org.jfree.chart.renderer.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.DefaultPolarItemRenderer;


public class DefaultPolarItemRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultPolarItemRendererTests.class);
    }

    
    public DefaultPolarItemRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DefaultPolarItemRenderer r1 = new DefaultPolarItemRenderer();
        DefaultPolarItemRenderer r2 = new DefaultPolarItemRenderer();
        assertEquals(r1, r2);
        
        r1.setSeriesFilled(1, true);
        assertFalse(r1.equals(r2));
        r2.setSeriesFilled(1, true);
        assertTrue(r1.equals(r2));
        
    }

    
    public void testHashcode() {
        DefaultPolarItemRenderer r1 = new DefaultPolarItemRenderer();
        DefaultPolarItemRenderer r2 = new DefaultPolarItemRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        DefaultPolarItemRenderer r1 = new DefaultPolarItemRenderer();
        DefaultPolarItemRenderer r2 = null;
        try {
            r2 = (DefaultPolarItemRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        r1.setSeriesFilled(1, true);
        assertFalse(r1.equals(r2));
        r2.setSeriesFilled(1, true);
        assertTrue(r1.equals(r2));
    }

    
    public void testSerialization() {
        DefaultPolarItemRenderer r1 = new DefaultPolarItemRenderer();
        DefaultPolarItemRenderer r2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (DefaultPolarItemRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
    }

}
