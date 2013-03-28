

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodValue;


public class TimePeriodValueTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(TimePeriodValueTests.class);
    }

    
    public TimePeriodValueTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        
    }

    
    public void testEqualsSelf() {
        TimePeriodValue tpv = new TimePeriodValue(new Day(), 55.75);
        assertTrue(tpv.equals(tpv));
    }

    
    public void testEquals() {
        TimePeriodValue tpv1 = new TimePeriodValue(new Day(30, 7, 2003), 55.75);
        TimePeriodValue tpv2 = new TimePeriodValue(new Day(30, 7, 2003), 55.75);
        assertTrue(tpv1.equals(tpv2));
        assertTrue(tpv2.equals(tpv1));
    }

    
    public void testSerialization() {

        TimePeriodValue tpv1 = new TimePeriodValue(new Day(30, 7, 2003), 55.75);
        TimePeriodValue tpv2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(tpv1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            tpv2 = (TimePeriodValue) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(tpv1, tpv2);

    }

}
