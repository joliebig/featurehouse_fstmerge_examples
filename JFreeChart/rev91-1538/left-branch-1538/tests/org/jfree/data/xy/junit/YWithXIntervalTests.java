

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
import org.jfree.data.xy.YWithXInterval;


public class YWithXIntervalTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(YWithXIntervalTests.class);
    }

    
    public YWithXIntervalTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        YWithXInterval i1 = new YWithXInterval(1.0, 0.5, 1.5);
        YWithXInterval i2 = new YWithXInterval(1.0, 0.5, 1.5);
        assertEquals(i1, i2);

        i1 = new YWithXInterval(1.1, 0.5, 1.5);
        assertFalse(i1.equals(i2));
        i2 = new YWithXInterval(1.1, 0.5, 1.5);
        assertTrue(i1.equals(i2));

        i1 = new YWithXInterval(1.1, 0.55, 1.5);
        assertFalse(i1.equals(i2));
        i2 = new YWithXInterval(1.1, 0.55, 1.5);
        assertTrue(i1.equals(i2));

        i1 = new YWithXInterval(1.1, 0.55, 1.55);
        assertFalse(i1.equals(i2));
        i2 = new YWithXInterval(1.1, 0.55, 1.55);
        assertTrue(i1.equals(i2));
    }

    
    public void testCloning() {
        YWithXInterval i1 = new YWithXInterval(1.0, 0.5, 1.5);
        assertFalse(i1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        YWithXInterval i1 = new YWithXInterval(1.0, 0.5, 1.5);
        YWithXInterval i2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(i1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            i2 = (YWithXInterval) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(i1, i2);
    }

}
