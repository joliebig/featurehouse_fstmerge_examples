

package org.jfree.chart.renderer.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.util.PublicCloneable;


public class StackedAreaRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StackedAreaRendererTests.class);
    }

    
    public StackedAreaRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = new StackedAreaRenderer();
        assertEquals(r1, r2);

        r1.setRenderAsPercentages(true);
        assertFalse(r1.equals(r2));
        r2.setRenderAsPercentages(true);
        assertTrue(r1.equals(r2));
    }

    
    public void testHashcode() {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = new StackedAreaRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = null;
        try {
            r2 = (StackedAreaRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));

    }

    
    public void testPublicCloneable() {
        StackedAreaRenderer r1 = new StackedAreaRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        StackedAreaRenderer r1 = new StackedAreaRenderer();
        StackedAreaRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (StackedAreaRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

}
