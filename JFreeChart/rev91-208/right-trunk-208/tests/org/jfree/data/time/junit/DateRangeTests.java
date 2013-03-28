

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

import org.jfree.data.time.DateRange;


public class DateRangeTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(DateRangeTests.class);
    }

    
    public DateRangeTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DateRange r1 = new DateRange(new Date(1000L), new Date(2000L));
        DateRange r2 = new DateRange(new Date(1000L), new Date(2000L));
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));

        r1 = new DateRange(new Date(1111L), new Date(2000L));
        assertFalse(r1.equals(r2));
        r2 = new DateRange(new Date(1111L), new Date(2000L));
        assertTrue(r1.equals(r2));

        r1 = new DateRange(new Date(1111L), new Date(2222L));
        assertFalse(r1.equals(r2));
        r2 = new DateRange(new Date(1111L), new Date(2222L));
        assertTrue(r1.equals(r2));
    }

    
    public void testSerialization() {
        DateRange r1 = new DateRange(new Date(1000L), new Date(2000L));
        DateRange r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (DateRange) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);
    }
    
    
    public void testClone() {
        DateRange r1 = new DateRange(new Date(1000L), new Date(2000L));
        assertFalse(r1 instanceof Cloneable);
    }
    
}
