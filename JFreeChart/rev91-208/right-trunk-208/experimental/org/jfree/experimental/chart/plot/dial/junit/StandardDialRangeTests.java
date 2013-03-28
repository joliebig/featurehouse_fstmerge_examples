

package org.jfree.experimental.chart.plot.dial.junit;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.experimental.chart.plot.dial.StandardDialRange;


public class StandardDialRangeTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardDialRangeTests.class);
    }

    
    public StandardDialRangeTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardDialRange r1 = new StandardDialRange();
        StandardDialRange r2 = new StandardDialRange();
        assertTrue(r1.equals(r2));
        
        
        r1.setLowerBound(1.1);
        assertFalse(r1.equals(r2));
        r2.setLowerBound(1.1);
        assertTrue(r1.equals(r2));
        
        
        r1.setUpperBound(11.1);
        assertFalse(r1.equals(r2));
        r2.setUpperBound(11.1);
        assertTrue(r1.equals(r2));
        
        
        r1.setIncrement(1.5);
        assertFalse(r1.equals(r2));
        r2.setIncrement(1.5);
        assertTrue(r1.equals(r2));
        
        
        r1.setPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, 
                Color.blue));
        assertFalse(r1.equals(r2));
        r2.setPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, 
                Color.blue));
        assertTrue(r1.equals(r2));
        
    }
    
    
    public void testHashCode() {
        StandardDialRange r1 = new StandardDialRange();
        StandardDialRange r2 = new StandardDialRange();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        StandardDialRange r1 = new StandardDialRange();
        StandardDialRange r2 = null;
        try {
            r2 = (StandardDialRange) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }


    
    public void testSerialization() {
        StandardDialRange r1 = new StandardDialRange();
        StandardDialRange r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (StandardDialRange) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
    }

}
