

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

import org.jfree.chart.axis.AxisLocation;


public class AxisLocationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(AxisLocationTests.class);
    }

    
    public AxisLocationTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        assertEquals(AxisLocation.TOP_OR_RIGHT, AxisLocation.TOP_OR_RIGHT);
        assertEquals(AxisLocation.BOTTOM_OR_RIGHT, 
                AxisLocation.BOTTOM_OR_RIGHT);    
        assertEquals(AxisLocation.TOP_OR_LEFT, AxisLocation.TOP_OR_LEFT);    
        assertEquals(AxisLocation.BOTTOM_OR_LEFT, AxisLocation.BOTTOM_OR_LEFT);
    }
    
    
    public void testHashCode() {
        AxisLocation a1 = AxisLocation.TOP_OR_RIGHT;
        AxisLocation a2 = AxisLocation.TOP_OR_RIGHT;
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testSerialization() {
        AxisLocation location1 = AxisLocation.BOTTOM_OR_RIGHT;
        AxisLocation location2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(location1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            location2 = (AxisLocation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(location1, location2);
        boolean same = location1 == location2;
        assertEquals(true, same);        
    }

}
