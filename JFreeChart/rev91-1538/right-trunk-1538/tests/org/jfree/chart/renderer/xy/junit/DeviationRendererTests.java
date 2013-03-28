

package org.jfree.chart.renderer.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.chart.util.PublicCloneable;


public class DeviationRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DeviationRendererTests.class);
    }

    
    public DeviationRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        
        DeviationRenderer r1 = new DeviationRenderer();
        DeviationRenderer r2 = new DeviationRenderer();
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));

        r1.setAlpha(0.1f);
        assertFalse(r1.equals(r2));
        r2.setAlpha(0.1f);
        assertTrue(r1.equals(r2));

    }

    
    public void testHashcode() {
        DeviationRenderer r1 = new DeviationRenderer();
        DeviationRenderer r2 = new DeviationRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        DeviationRenderer r1 = new DeviationRenderer();
        DeviationRenderer r2 = null;
        try {
            r2 = (DeviationRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));

        

    }

    
    public void testPublicCloneable() {
        DeviationRenderer r1 = new DeviationRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        DeviationRenderer r1 = new DeviationRenderer();
        DeviationRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (DeviationRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

}
