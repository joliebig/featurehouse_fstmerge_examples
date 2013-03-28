

package org.jfree.data.function.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.function.LineFunction2D;


public class LineFunction2DTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(LineFunction2DTests.class);
    }

    
    public LineFunction2DTests(String name) {
        super(name);
    }

    private static final double EPSILON = 0.000000001;

    
    public void testConstructor() {
        LineFunction2D f = new LineFunction2D(1.0, 2.0);
        assertEquals(1.0, f.getIntercept(), EPSILON);
        assertEquals(2.0, f.getSlope(), EPSILON);
    }



    
    public void testEquals() {
        LineFunction2D f1 = new LineFunction2D(1.0, 2.0);
        LineFunction2D f2 = new LineFunction2D(1.0, 2.0);
        assertTrue(f1.equals(f2));
        f1 = new LineFunction2D(2.0, 3.0);
        assertFalse(f1.equals(f2));
        f2 = new LineFunction2D(2.0, 3.0);
        assertTrue(f1.equals(f2));
    }

    
    public void testSerialization() {
        LineFunction2D f1 = new LineFunction2D(1.0, 2.0);
        LineFunction2D f2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(f1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            f2 = (LineFunction2D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(f1, f2);
    }

    
    public void testHashCode() {
        LineFunction2D f1 = new LineFunction2D(1.0, 2.0);
        LineFunction2D f2 = new LineFunction2D(1.0, 2.0);
        assertEquals(f1.hashCode(), f2.hashCode());
    }

}

