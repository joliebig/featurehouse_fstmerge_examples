

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.NumberTickUnit;


public class NumberTickUnitTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(NumberTickUnitTests.class);
    }

    
    public NumberTickUnitTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        NumberTickUnit t2 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));
        
        t1 = new NumberTickUnit(3.21, new DecimalFormat("0.00"));
        assertFalse(t1.equals(t2));
        t2 = new NumberTickUnit(3.21, new DecimalFormat("0.00"));
        assertTrue(t1.equals(t2));
        
        t1 = new NumberTickUnit(3.21, new DecimalFormat("0.000"));
        assertFalse(t1.equals(t2));
        t2 = new NumberTickUnit(3.21, new DecimalFormat("0.000"));
        assertTrue(t1.equals(t2));
    }

    
    public void testHashCode() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        NumberTickUnit t2 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        assertFalse(t1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        NumberTickUnit t1 = new NumberTickUnit(1.23, new DecimalFormat("0.00"));
        NumberTickUnit t2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (NumberTickUnit) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(t1, t2);
    }

}
