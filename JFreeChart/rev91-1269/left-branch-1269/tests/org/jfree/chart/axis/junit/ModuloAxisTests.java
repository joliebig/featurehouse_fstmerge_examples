

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

import org.jfree.chart.axis.ModuloAxis;
import org.jfree.data.Range;


public class ModuloAxisTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ModuloAxisTests.class);
    }

    
    public ModuloAxisTests(String name) {
        super(name);
    }

    
    public void testCloning() {
        ModuloAxis a1 = new ModuloAxis("Test", new Range(0.0, 1.0));
        ModuloAxis a2 = null;
        try {
            a2 = (ModuloAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    
    public void testEquals() {
        ModuloAxis a1 = new ModuloAxis("Test", new Range(0.0, 1.0));
        ModuloAxis a2 = new ModuloAxis("Test", new Range(0.0, 1.0));
        assertTrue(a1.equals(a2));

        a1.setDisplayRange(0.1, 1.1);
        assertFalse(a1.equals(a2));
        a2.setDisplayRange(0.1, 1.1);
        assertTrue(a1.equals(a2));
    }

    
    public void testHashCode() {
        ModuloAxis a1 = new ModuloAxis("Test", new Range(0.0, 1.0));
        ModuloAxis a2 = new ModuloAxis("Test", new Range(0.0, 1.0));
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testSerialization() {

        ModuloAxis a1 = new ModuloAxis("Test", new Range(0.0, 1.0));
        ModuloAxis a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (ModuloAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);

    }

}
