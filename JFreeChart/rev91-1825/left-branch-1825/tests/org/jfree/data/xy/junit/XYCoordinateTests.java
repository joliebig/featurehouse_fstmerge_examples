

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.xy.XYCoordinate;


public class XYCoordinateTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYCoordinateTests.class);
    }

    
    public XYCoordinateTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        XYCoordinate v1 = new XYCoordinate(1.0, 2.0);
        XYCoordinate v2 = new XYCoordinate(1.0, 2.0);
        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));

        v1 = new XYCoordinate(1.1, 2.0);
        assertFalse(v1.equals(v2));
        v2 = new XYCoordinate(1.1, 2.0);
        assertTrue(v1.equals(v2));

        v1 = new XYCoordinate(1.1, 2.2);
        assertFalse(v1.equals(v2));
        v2 = new XYCoordinate(1.1, 2.2);
        assertTrue(v1.equals(v2));
    }

    
    public void testHashcode() {
        XYCoordinate v1 = new XYCoordinate(1.0, 2.0);
        XYCoordinate v2 = new XYCoordinate(1.0, 2.0);
        assertTrue(v1.equals(v2));
        int h1 = v1.hashCode();
        int h2 = v2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        XYCoordinate v1 = new XYCoordinate(1.0, 2.0);
        assertFalse(v1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        XYCoordinate v1 = new XYCoordinate(1.0, 2.0);
        XYCoordinate v2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(v1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            v2 = (XYCoordinate) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(v1, v2);
    }

}
