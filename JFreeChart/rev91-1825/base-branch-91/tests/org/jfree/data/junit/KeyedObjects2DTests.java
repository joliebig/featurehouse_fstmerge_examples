

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.KeyedObjects2D;


public class KeyedObjects2DTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(KeyedObjects2DTests.class);
    }

    
    public KeyedObjects2DTests(String name) {
        super(name);
    }

    
    public void testCloning() {
        KeyedObjects2D o1 = new KeyedObjects2D();
        o1.setObject(new Integer(1), "V1", "C1");
        o1.setObject(null, "V2", "C1");
        o1.setObject(new Integer(3), "V3", "C2");
        KeyedObjects2D o2 = null;
        try {
            o2 = (KeyedObjects2D) o1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(o1 != o2);
        assertTrue(o1.getClass() == o2.getClass());
        assertTrue(o1.equals(o2));
    }
    
    
    public void testSerialization() {

        KeyedObjects2D ko2D1 = new KeyedObjects2D();
        ko2D1.addObject(new Double(234.2), "Row1", "Col1");
        ko2D1.addObject(null, "Row1", "Col2");
        ko2D1.addObject(new Double(345.9), "Row2", "Col1");
        ko2D1.addObject(new Double(452.7), "Row2", "Col2");

        KeyedObjects2D ko2D2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(ko2D1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            ko2D2 = (KeyedObjects2D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(ko2D1, ko2D2);

    }

}
