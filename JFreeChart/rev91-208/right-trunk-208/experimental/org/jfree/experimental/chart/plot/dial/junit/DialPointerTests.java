

package org.jfree.experimental.chart.plot.dial.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.experimental.chart.plot.dial.DialPointer;


public class DialPointerTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DialPointerTests.class);
    }

    
    public DialPointerTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DialPointer i1 = new DialPointer.Pin(1);
        DialPointer i2 = new DialPointer.Pin(1);
        assertTrue(i1.equals(i2));
        
        
        i1 = new DialPointer.Pin(2);
        assertFalse(i1.equals(i2));
        i2 = new DialPointer.Pin(2);
        assertTrue(i1.equals(i2));        
    }

    
    public void testHashCode() {
        DialPointer i1 = new DialPointer.Pin(1);
        DialPointer i2 = new DialPointer.Pin(1);
        assertTrue(i1.equals(i2));
        int h1 = i1.hashCode();
        int h2 = i2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        DialPointer i1 = new DialPointer.Pin(1);
        DialPointer i2 = null;
        try {
            i2 = (DialPointer) i1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(i1 != i2);
        assertTrue(i1.getClass() == i2.getClass());
        assertTrue(i1.equals(i2));
        
        
    }


    
    public void testSerialization() {
        
        DialPointer i1 = new DialPointer.Pin(1);
        DialPointer i2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(i1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            i2 = (DialPointer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(i1, i2);
        
        
    }

}
