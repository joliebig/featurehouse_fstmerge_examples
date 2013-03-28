

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

import org.jfree.chart.axis.DateTickUnit;


public class DateTickUnitTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DateTickUnitTests.class);
    }

    
    public DateTickUnitTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DateTickUnit t1 = new DateTickUnit(DateTickUnit.DAY, 1);
        DateTickUnit t2 = new DateTickUnit(DateTickUnit.DAY, 1);
        assertTrue(t1.equals(t2));
    }

    
    public void testHashCode() {
        DateTickUnit t1 = new DateTickUnit(DateTickUnit.DAY, 1);
        DateTickUnit t2 = new DateTickUnit(DateTickUnit.DAY, 1);
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testSerialization() {

        DateTickUnit a1 = new DateTickUnit(DateTickUnit.DAY, 7);
        DateTickUnit a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (DateTickUnit) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);

    }

}
