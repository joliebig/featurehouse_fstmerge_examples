

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

import org.jfree.data.statistics.MeanAndStandardDeviation;


public class MeanAndStandardDeviationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(MeanAndStandardDeviationTests.class);
    }

    
    public MeanAndStandardDeviationTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        MeanAndStandardDeviation m1 = new MeanAndStandardDeviation(1.2, 3.4);
        MeanAndStandardDeviation m2 = new MeanAndStandardDeviation(1.2, 3.4);
        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));

        m1 = new MeanAndStandardDeviation(1.0, 3.4);
        assertFalse(m1.equals(m2));
        m2 = new MeanAndStandardDeviation(1.0, 3.4);
        assertTrue(m1.equals(m2));

        m1 = new MeanAndStandardDeviation(1.0, 3.0);
        assertFalse(m1.equals(m2));
        m2 = new MeanAndStandardDeviation(1.0, 3.0);
        assertTrue(m1.equals(m2));
    }

    
    public void testCloning() {
        MeanAndStandardDeviation m1 = new MeanAndStandardDeviation(1.2, 3.4);
        assertFalse(m1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        MeanAndStandardDeviation m1 = new MeanAndStandardDeviation(1.2, 3.4);
        MeanAndStandardDeviation m2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (MeanAndStandardDeviation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(m1, m2);

    }
}
