

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.GreyPalette;


public class ColorBarTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ColorBarTests.class);
    }

    
    public ColorBarTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        ColorBar c1 = new ColorBar("Test");
        ColorBar c2 = new ColorBar("Test");
        assertEquals(c1, c2);

        c1.setAxis(new NumberAxis("Axis 1"));
        assertTrue(!c1.equals(c2));
        c2.setAxis(new NumberAxis("Axis 1"));
        assertTrue(c1.equals(c2));

        c1.setColorPalette(new GreyPalette());
        assertTrue(!c1.equals(c2));
        c2.setColorPalette(new GreyPalette());
        assertTrue(c1.equals(c2));
    }

    
    public void testHashCode() {
        ColorBar c1 = new ColorBar("Test");
        ColorBar c2 = new ColorBar("Test");
        assertTrue(c1.equals(c2));
        int h1 = c1.hashCode();
        int h2 = c2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        ColorBar c1 = new ColorBar("Test");
        ColorBar c2 = null;
        try {
            c2 = (ColorBar) c1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(c1 != c2);
        assertTrue(c1.getClass() == c2.getClass());
        assertTrue(c1.equals(c2));
    }

    
    public void testSerialization() {

        ColorBar a1 = new ColorBar("Test Axis");
        ColorBar a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (ColorBar) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
