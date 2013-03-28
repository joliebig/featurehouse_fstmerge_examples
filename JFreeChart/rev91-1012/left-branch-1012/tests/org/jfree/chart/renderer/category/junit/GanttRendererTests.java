

package org.jfree.chart.renderer.category.junit;

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

import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.util.PublicCloneable;


public class GanttRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(GanttRendererTests.class);
    }

    
    public GanttRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        GanttRenderer r1 = new GanttRenderer();
        GanttRenderer r2 = new GanttRenderer();
        assertEquals(r1, r2);

        r1.setCompletePaint(Color.yellow);
        assertFalse(r1.equals(r2));
        r2.setCompletePaint(Color.yellow);
        assertTrue(r1.equals(r2));

        r1.setIncompletePaint(Color.green);
        assertFalse(r1.equals(r2));
        r2.setIncompletePaint(Color.green);
        assertTrue(r1.equals(r2));

        r1.setStartPercent(0.11);
        assertFalse(r1.equals(r2));
        r2.setStartPercent(0.11);
        assertTrue(r1.equals(r2));

        r1.setEndPercent(0.88);
        assertFalse(r1.equals(r2));
        r2.setEndPercent(0.88);
        assertTrue(r1.equals(r2));
    }

    
    public void testHashcode() {
        GanttRenderer r1 = new GanttRenderer();
        GanttRenderer r2 = new GanttRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        GanttRenderer r1 = new GanttRenderer();
        GanttRenderer r2 = null;
        try {
            r2 = (GanttRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        GanttRenderer r1 = new GanttRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        GanttRenderer r1 = new GanttRenderer();
        r1.setCompletePaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f,
                4.0f, Color.blue));
        r1.setIncompletePaint(new GradientPaint(4.0f, 3.0f, Color.red, 2.0f,
                1.0f, Color.blue));
        GanttRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (GanttRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

}
