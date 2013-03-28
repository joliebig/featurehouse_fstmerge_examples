

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

import org.jfree.data.time.SimpleTimePeriod;


public class SimpleTimePeriodTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(SimpleTimePeriodTests.class);
    }

    
    public SimpleTimePeriodTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        
    }

    
    public void testEqualsSelf() {
        SimpleTimePeriod p = new SimpleTimePeriod(new Date(1000L),
                new Date(1001L));
        assertTrue(p.equals(p));
    }

    
    public void testEquals() {
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(1000L),
                new Date(1004L));
        SimpleTimePeriod p2 = new SimpleTimePeriod(new Date(1000L),
                new Date(1004L));
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));

        p1 = new SimpleTimePeriod(new Date(1002L), new Date(1004L));
        assertFalse(p1.equals(p2));
        p2 = new SimpleTimePeriod(new Date(1002L), new Date(1004L));
        assertTrue(p1.equals(p2));

        p1 = new SimpleTimePeriod(new Date(1002L), new Date(1003L));
        assertFalse(p1.equals(p2));
        p2 = new SimpleTimePeriod(new Date(1002L), new Date(1003L));
        assertTrue(p1.equals(p2));
    }

    
    public void testSerialization() {
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(1000L),
                new Date(1001L));
        SimpleTimePeriod p2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();
            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (SimpleTimePeriod) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);
    }

    
    public void testHashcode() {
        SimpleTimePeriod s1 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        SimpleTimePeriod s2 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        assertTrue(s1.equals(s2));
        int h1 = s1.hashCode();
        int h2 = s2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testClone() {
        SimpleTimePeriod s1 = new SimpleTimePeriod(new Date(10L),
                new Date(20));
        assertFalse(s1 instanceof Cloneable);
    }

    
    public void testImmutable() {
        SimpleTimePeriod p1 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        SimpleTimePeriod p2 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        assertEquals(p1, p2);
        p1.getStart().setTime(11L);
        assertEquals(p1, p2);

        Date d1 = new Date(10L);
        Date d2 = new Date(20L);
        p1 = new SimpleTimePeriod(d1, d2);
        d1.setTime(11L);
        assertEquals(new Date(10L), p1.getStart());
    }

    
    public void testCompareTo() {
        SimpleTimePeriod s1 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        SimpleTimePeriod s2 = new SimpleTimePeriod(new Date(10L),
                new Date(20L));
        assertEquals(0, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(9L), new Date(21L));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(-1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(11L), new Date(19L));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(9L), new Date(19L));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(-1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(11L), new Date(21));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(10L), new Date(18));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(-1, s1.compareTo(s2));

        s1 = new SimpleTimePeriod(new Date(10L), new Date(22));
        s2 = new SimpleTimePeriod(new Date(10L), new Date(20L));
        assertEquals(1, s1.compareTo(s2));
    }

}
