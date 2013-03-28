

package org.jfree.chart.util.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.util.BooleanList;


public class BooleanListTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(BooleanListTests.class);
    }

    
    public BooleanListTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        BooleanList l1 = new BooleanList();
        l1.setBoolean(0, Boolean.TRUE);
        l1.setBoolean(1, Boolean.FALSE);
        l1.setBoolean(2, null);
        
        BooleanList l2 = new BooleanList();
        l2.setBoolean(0, Boolean.TRUE);
        l2.setBoolean(1, Boolean.FALSE);
        l2.setBoolean(2, null);
        
        assertTrue(l1.equals(l2));
        assertTrue(l2.equals(l2));
        
    }
    
    
    
    public void testCloning() {
        
        BooleanList l1 = new BooleanList();
        l1.setBoolean(0, Boolean.TRUE);
        l1.setBoolean(1, Boolean.FALSE);
        l1.setBoolean(2, null);
        
        BooleanList l2 = null;
        try {
            l2 = (BooleanList) l1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println(
                    "BooleanListTests.testCloning: failed to clone.");
        }
        assertTrue(l1 != l2);
        assertTrue(l1.getClass() == l2.getClass());
        assertTrue(l1.equals(l2));
        
        l2.setBoolean(0, Boolean.FALSE);
        assertFalse(l1.equals(l2));
        
    }
    
    
    public void testSerialization() {

        BooleanList l1 = new BooleanList();
        l1.setBoolean(0, Boolean.TRUE);
        l1.setBoolean(1, Boolean.FALSE);
        l1.setBoolean(2, null);

        BooleanList l2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(l1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            l2 = (BooleanList) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(l1, l2);

    }

    
    public void testHashCode() {
        BooleanList l1 = new BooleanList();
        BooleanList l2 = new BooleanList();
        assertTrue(l1.hashCode() == l2.hashCode());
    
        l1.setBoolean(0, Boolean.TRUE);
        assertFalse(l1.hashCode() == l2.hashCode());
        l2.setBoolean(0, Boolean.TRUE);
        assertTrue(l1.hashCode() == l2.hashCode());
    }
}
