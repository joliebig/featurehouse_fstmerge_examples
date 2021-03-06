

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

import org.jfree.chart.needle.PlumNeedle;


public class PlumNeedleTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(PlumNeedleTests.class);
    }

    
    public PlumNeedleTests(String name) {
        super(name);
    }

    
    public void testEquals() {
       PlumNeedle n1 = new PlumNeedle();
       PlumNeedle n2 = new PlumNeedle();
       assertTrue(n1.equals(n2));
       assertTrue(n2.equals(n1));
    }

    
    public void testCloning() {
        PlumNeedle n1 = new PlumNeedle();
        PlumNeedle n2 = null;
        try {
            n2 = (PlumNeedle) n1.clone();
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
        PlumNeedle n1 = new PlumNeedle();
        PlumNeedle n2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(n1);
            out.close();
            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            n2 = (PlumNeedle) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(n1.equals(n2));
    }

}
