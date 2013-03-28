

package org.jfree.chart.needle.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.needle.PointerNeedle;


public class PointerNeedleTests extends TestCase {
    
    public static Test suite() {
        return new TestSuite(PointerNeedleTests.class);
    }

    
    public PointerNeedleTests(String name) {
        super(name);
    }

    
    public void testEquals() {
       PointerNeedle n1 = new PointerNeedle();
       PointerNeedle n2 = new PointerNeedle();
       assertTrue(n1.equals(n2));
       assertTrue(n2.equals(n1));
    }

    
    public void testCloning() {
        PointerNeedle n1 = new PointerNeedle();
        PointerNeedle n2 = null;
        try {
            n2 = (PointerNeedle) n1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.err.println("Failed to clone.");
        }
        assertTrue(n1 != n2);
        assertTrue(n1.getClass() == n2.getClass());
        assertTrue(n1.equals(n2));
    }

    
    public void testSerialization() {
        PointerNeedle n1 = new PointerNeedle();
        PointerNeedle n2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(n1);
            out.close();
            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            n2 = (PointerNeedle) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(n1.equals(n2));
    }

}
