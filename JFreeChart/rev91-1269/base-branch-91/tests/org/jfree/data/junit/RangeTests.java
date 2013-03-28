

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.Range;


public class RangeTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(RangeTests.class);
    }

    
    public RangeTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        Range r1 = new Range(0.0, 1.0);
        Range r2 = new Range(0.0, 1.0);
        assertEquals(r1, r2);
        assertEquals(r2, r1);

        r1 = new Range(0.0, 1.0);
        r2 = new Range(0.5, 1.0);
        assertFalse(r1.equals(r2));

        r1 = new Range(0.0, 1.0);
        r2 = new Range(0.0, 2.0);
        assertFalse(r1.equals(r2));
        
    }
    
    
    public void testContains() {
        Range r1 = new Range(0.0, 1.0);
        assertFalse(r1.contains(Double.NaN));
        assertFalse(r1.contains(Double.NEGATIVE_INFINITY));
        assertFalse(r1.contains(-1.0));
        assertTrue(r1.contains(0.0));
        assertTrue(r1.contains(0.5));
        assertTrue(r1.contains(1.0));
        assertFalse(r1.contains(2.0));
        assertFalse(r1.contains(Double.POSITIVE_INFINITY));
    }
    
    
    public void testConstrain() {
        Range r1 = new Range(0.0, 1.0);
        
        double d = r1.constrain(0.5);
        assertEquals(0.5, d, 0.0000001);
        
        d = r1.constrain(0.0);
        assertEquals(0.0, d, 0.0000001);

        d = r1.constrain(1.0);
        assertEquals(1.0, d, 0.0000001);
        
        d = r1.constrain(-1.0);
        assertEquals(0.0, d, 0.0000001);
        
        d = r1.constrain(2.0);
        assertEquals(1.0, d, 0.0000001);

        d = r1.constrain(Double.POSITIVE_INFINITY);
        assertEquals(1.0, d, 0.0000001);

        d = r1.constrain(Double.NEGATIVE_INFINITY);
        assertEquals(0.0, d, 0.0000001);

        d = r1.constrain(Double.NaN);
        assertTrue(Double.isNaN(d));
    }
    
    
    public void testIntersects() {
        Range r1 = new Range(0.0, 1.0);
        assertFalse(r1.intersects(-2.0, -1.0));
        assertFalse(r1.intersects(-2.0, 0.0));
        assertTrue(r1.intersects(-2.0, 0.5));
        assertTrue(r1.intersects(-2.0, 1.0));
        assertTrue(r1.intersects(-2.0, 1.5));
        assertTrue(r1.intersects(0.0, 0.5));
        assertTrue(r1.intersects(0.0, 1.0));
        assertTrue(r1.intersects(0.0, 1.5));
        assertTrue(r1.intersects(0.5, 0.6));
        assertTrue(r1.intersects(0.5, 1.0));
        assertTrue(r1.intersects(0.5, 1.5));
        assertFalse(r1.intersects(1.0, 1.1));
        assertFalse(r1.intersects(1.5, 2.0));
    }
    
    
    public void testExpand() {
        Range r1 = new Range(0.0, 100.0);
        Range r2 = Range.expand(r1, 0.10, 0.10);
        assertEquals(-10.0, r2.getLowerBound(), 0.001);
        assertEquals(110.0, r2.getUpperBound(), 0.001);
    }
    
    
    public void testSerialization() {

        Range r1 = new Range(25.0, 133.42);
        Range r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (Range) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }

}
