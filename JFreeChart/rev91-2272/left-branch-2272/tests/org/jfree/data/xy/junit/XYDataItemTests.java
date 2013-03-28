

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

import org.jfree.data.xy.XYDataItem;


public class XYDataItemTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYDataItemTests.class);
    }

    
    public XYDataItemTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        XYDataItem i1 = new XYDataItem(1.0, 1.1);
        XYDataItem i2 = new XYDataItem(1.0, 1.1);
        assertTrue(i1.equals(i2));
        assertTrue(i2.equals(i1));

        i1.setY(new Double(9.9));
        assertFalse(i1.equals(i2));

        i2.setY(new Double(9.9));
        assertTrue(i1.equals(i2));

    }

    
    public void testCloning() {
        XYDataItem i1 = new XYDataItem(1.0, 1.1);
        XYDataItem i2 = null;
        i2 = (XYDataItem) i1.clone();
        assertTrue(i1 != i2);
        assertTrue(i1.getClass() == i2.getClass());
        assertTrue(i1.equals(i2));
    }

    
    public void testSerialization() {

        XYDataItem i1 = new XYDataItem(1.0, 1.1);
        XYDataItem i2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(i1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            i2 = (XYDataItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(i1, i2);

    }

}
