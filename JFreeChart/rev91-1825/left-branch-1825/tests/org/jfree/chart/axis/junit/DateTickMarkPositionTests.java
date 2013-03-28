

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

import org.jfree.chart.axis.DateTickMarkPosition;


public class DateTickMarkPositionTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DateTickMarkPositionTests.class);
    }

    
    public DateTickMarkPositionTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertEquals(DateTickMarkPosition.START, DateTickMarkPosition.START);
        assertEquals(DateTickMarkPosition.MIDDLE, DateTickMarkPosition.MIDDLE);
        assertEquals(DateTickMarkPosition.END, DateTickMarkPosition.END);
        assertFalse(DateTickMarkPosition.START.equals(null));
        assertFalse(DateTickMarkPosition.START.equals(
                DateTickMarkPosition.END));
        assertFalse(DateTickMarkPosition.MIDDLE.equals(
                DateTickMarkPosition.END));
    }

    
    public void testHashCode() {
        DateTickMarkPosition a1 = DateTickMarkPosition.END;
        DateTickMarkPosition a2 = DateTickMarkPosition.END;
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testSerialization() {
        DateTickMarkPosition p1 = DateTickMarkPosition.MIDDLE;
        DateTickMarkPosition p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (DateTickMarkPosition) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);
        assertTrue(p1 == p2);
    }

}
