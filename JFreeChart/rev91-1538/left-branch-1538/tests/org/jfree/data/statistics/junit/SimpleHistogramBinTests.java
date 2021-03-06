

package org.jfree.data.statistics.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.SimpleHistogramBin;


public class SimpleHistogramBinTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(SimpleHistogramBinTests.class);
    }

    
    public SimpleHistogramBinTests(String name) {
        super(name);
    }

    
    public void testAccepts() {
        SimpleHistogramBin bin1 = new SimpleHistogramBin(1.0, 2.0);
        assertFalse(bin1.accepts(0.0));
        assertTrue(bin1.accepts(1.0));
        assertTrue(bin1.accepts(1.5));
        assertTrue(bin1.accepts(2.0));
        assertFalse(bin1.accepts(2.1));
        assertFalse(bin1.accepts(Double.NaN));

        SimpleHistogramBin bin2
            = new SimpleHistogramBin(1.0, 2.0, false, false);
        assertFalse(bin2.accepts(0.0));
        assertFalse(bin2.accepts(1.0));
        assertTrue(bin2.accepts(1.5));
        assertFalse(bin2.accepts(2.0));
        assertFalse(bin2.accepts(2.1));
        assertFalse(bin2.accepts(Double.NaN));
    }

    
    public void testOverlapsWidth() {
        SimpleHistogramBin b1 = new SimpleHistogramBin(1.0, 2.0);
        SimpleHistogramBin b2 = new SimpleHistogramBin(2.0, 3.0);
        SimpleHistogramBin b3 = new SimpleHistogramBin(3.0, 4.0);
        SimpleHistogramBin b4 = new SimpleHistogramBin(0.0, 5.0);
        SimpleHistogramBin b5 = new SimpleHistogramBin(2.0, 3.0, false, true);
        SimpleHistogramBin b6 = new SimpleHistogramBin(2.0, 3.0, true, false);
        assertTrue(b1.overlapsWith(b2));
        assertTrue(b2.overlapsWith(b1));
        assertFalse(b1.overlapsWith(b3));
        assertFalse(b3.overlapsWith(b1));
        assertTrue(b1.overlapsWith(b4));
        assertTrue(b4.overlapsWith(b1));
        assertFalse(b1.overlapsWith(b5));
        assertFalse(b5.overlapsWith(b1));
        assertTrue(b1.overlapsWith(b6));
        assertTrue(b6.overlapsWith(b1));
    }

    
    public void testEquals() {
        SimpleHistogramBin b1 = new SimpleHistogramBin(1.0, 2.0);
        SimpleHistogramBin b2 = new SimpleHistogramBin(1.0, 2.0);
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b1));

        b1 = new SimpleHistogramBin(1.1, 2.0, true, true);
        assertFalse(b1.equals(b2));
        b2 = new SimpleHistogramBin(1.1, 2.0, true, true);
        assertTrue(b1.equals(b2));

        b1 = new SimpleHistogramBin(1.1, 2.2, true, true);
        assertFalse(b1.equals(b2));
        b2 = new SimpleHistogramBin(1.1, 2.2, true, true);
        assertTrue(b1.equals(b2));

        b1 = new SimpleHistogramBin(1.1, 2.2, false, true);
        assertFalse(b1.equals(b2));
        b2 = new SimpleHistogramBin(1.1, 2.2, false, true);
        assertTrue(b1.equals(b2));

        b1 = new SimpleHistogramBin(1.1, 2.2, false, false);
        assertFalse(b1.equals(b2));
        b2 = new SimpleHistogramBin(1.1, 2.2, false, false);
        assertTrue(b1.equals(b2));

        b1.setItemCount(99);
        assertFalse(b1.equals(b2));
        b2.setItemCount(99);
        assertTrue(b1.equals(b2));
    }

    
    public void testCloning() {
        SimpleHistogramBin b1 = new SimpleHistogramBin(1.1, 2.2, false, true);
        b1.setItemCount(99);
        SimpleHistogramBin b2 = null;
        try {
            b2 = (SimpleHistogramBin) b1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(b1 != b2);
        assertTrue(b1.getClass() == b2.getClass());
        assertTrue(b1.equals(b2));

        
        b2.setItemCount(111);
        assertFalse(b1.equals(b2));
    }

    
    public void testSerialization() {

        SimpleHistogramBin b1 = new SimpleHistogramBin(1.0, 2.0, false, true);
        b1.setItemCount(123);
        SimpleHistogramBin b2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(b1);
            out.close();
            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            b2 = (SimpleHistogramBin) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(b1, b2);
    }

}
