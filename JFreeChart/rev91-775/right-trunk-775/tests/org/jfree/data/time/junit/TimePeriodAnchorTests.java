

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

import org.jfree.data.time.TimePeriodAnchor;


public class TimePeriodAnchorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(TimePeriodAnchorTests.class);
    }

    
    public TimePeriodAnchorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertTrue(TimePeriodAnchor.START.equals(TimePeriodAnchor.START));
        assertTrue(TimePeriodAnchor.MIDDLE.equals(TimePeriodAnchor.MIDDLE));
        assertTrue(TimePeriodAnchor.END.equals(TimePeriodAnchor.END));
    }
    
    
    public void testSerialization() {

        TimePeriodAnchor a1 = TimePeriodAnchor.START;
        TimePeriodAnchor a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (TimePeriodAnchor) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertTrue(a1 == a2); 

    }

}
