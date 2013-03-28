

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

import org.jfree.data.xy.YInterval;


public class YIntervalTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(YIntervalTests.class);
    }

    
    public YIntervalTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        YInterval i1 = new YInterval(1.0, 0.5, 1.5);
        YInterval i2 = new YInterval(1.0, 0.5, 1.5);
        assertEquals(i1, i2);
        
        i1 = new YInterval(1.1, 0.5, 1.5);
        assertFalse(i1.equals(i2));
        i2 = new YInterval(1.1, 0.5, 1.5);
        assertTrue(i1.equals(i2));

        i1 = new YInterval(1.1, 0.55, 1.5);
        assertFalse(i1.equals(i2));
        i2 = new YInterval(1.1, 0.55, 1.5);
        assertTrue(i1.equals(i2));
    
        i1 = new YInterval(1.1, 0.55, 1.55);
        assertFalse(i1.equals(i2));
        i2 = new YInterval(1.1, 0.55, 1.55);
        assertTrue(i1.equals(i2));
    }

    
    public void testCloning() {
        YInterval i1 = new YInterval(1.0, 0.5, 1.5);
        assertFalse(i1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        YInterval i1 = new YInterval(1.0, 0.5, 1.5);
        YInterval i2 = null;
        
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(i1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            i2 = (YInterval) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(i1, i2);
    }
    
}
