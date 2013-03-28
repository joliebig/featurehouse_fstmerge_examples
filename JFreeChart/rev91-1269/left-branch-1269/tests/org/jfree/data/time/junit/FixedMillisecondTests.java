

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.FixedMillisecond;


public class FixedMillisecondTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(FixedMillisecondTests.class);
    }

    
    public FixedMillisecondTests(String name) {
        super(name);
    }

    
    public void testSerialization() {

        FixedMillisecond m1 = new FixedMillisecond();
        FixedMillisecond m2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            m2 = (FixedMillisecond) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(m1, m2);

    }

    
    public void testHashcode() {
        FixedMillisecond m1 = new FixedMillisecond(500000L);
        FixedMillisecond m2 = new FixedMillisecond(500000L);
        assertTrue(m1.equals(m2));
        int h1 = m1.hashCode();
        int h2 = m2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testNotCloneable() {
        FixedMillisecond m = new FixedMillisecond(500000L);
        assertFalse(m instanceof Cloneable);
    }

    
    public void testImmutability() {
    	Date d = new Date(20L);
    	FixedMillisecond fm = new FixedMillisecond(d);
    	d.setTime(22L);
    	assertEquals(20L, fm.getFirstMillisecond());
    }
}
