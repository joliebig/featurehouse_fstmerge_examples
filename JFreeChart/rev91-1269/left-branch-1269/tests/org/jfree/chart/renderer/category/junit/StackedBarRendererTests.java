

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

import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.util.PublicCloneable;


public class StackedBarRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StackedBarRendererTests.class);
    }

    
    public StackedBarRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StackedBarRenderer r1 = new StackedBarRenderer();
        StackedBarRenderer r2 = new StackedBarRenderer();
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));

        r1.setRenderAsPercentages(true);
        assertFalse(r1.equals(r2));
        r2.setRenderAsPercentages(true);
        assertTrue(r1.equals(r2));
    }

    
    public void testHashCode() {
        StackedBarRenderer r1 = new StackedBarRenderer();
        StackedBarRenderer r2 = new StackedBarRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        StackedBarRenderer r1 = new StackedBarRenderer();
        StackedBarRenderer r2 = null;
        try {
            r2 = (StackedBarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        StackedBarRenderer r1 = new StackedBarRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        StackedBarRenderer r1 = new StackedBarRenderer();
        StackedBarRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (StackedBarRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }

}
