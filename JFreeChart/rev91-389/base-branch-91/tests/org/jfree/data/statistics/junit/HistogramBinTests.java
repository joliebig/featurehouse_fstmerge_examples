

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

import org.jfree.data.statistics.HistogramBin;


public class HistogramBinTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(HistogramBinTests.class);
    }

    
    public HistogramBinTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        double start = 10.0;
        double end = 20.0;
        HistogramBin b1 = new HistogramBin(start, end);
        HistogramBin b2 = new HistogramBin(start, end);
        
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b1));

    }

    
    public void testCloning() {
        double start = 10.0;
        double end = 20.0;
        HistogramBin b1 = new HistogramBin(start, end);
        HistogramBin b2 = null;
        try {
            b2 = (HistogramBin) b1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(b1 != b2);
        assertTrue(b1.getClass() == b2.getClass());
        assertTrue(b1.equals(b2));
    }

    
    public void testSerialization() {

        double start = 10.0;
        double end = 20.0;
        HistogramBin b1 = new HistogramBin(start, end);
        HistogramBin b2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(b1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            b2 = (HistogramBin) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(b1, b2);

    }

}
