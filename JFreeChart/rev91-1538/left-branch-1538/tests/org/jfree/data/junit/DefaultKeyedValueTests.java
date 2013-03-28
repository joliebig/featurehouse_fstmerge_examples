

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

import org.jfree.data.DefaultKeyedValue;


public class DefaultKeyedValueTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultKeyedValueTests.class);
    }

    
    public DefaultKeyedValueTests(String name) {
        super(name);
    }

    
    public void testConstructor() {
        DefaultKeyedValue v = new DefaultKeyedValue("A", new Integer(1));
        assertEquals("A", v.getKey());
        assertEquals(new Integer(1), v.getValue());

        
        boolean pass = false;
        try {
             new DefaultKeyedValue(null, new Integer(1));
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);

        
        v = new DefaultKeyedValue("A", null);
        assertNull(v.getValue());
    }

    
    public void testEquals() {

        DefaultKeyedValue v1 = new DefaultKeyedValue("Test", new Double(45.5));
        DefaultKeyedValue v2 = new DefaultKeyedValue("Test", new Double(45.5));
        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));

        v1 = new DefaultKeyedValue("Test 1", new Double(45.5));
        v2 = new DefaultKeyedValue("Test 2", new Double(45.5));
        assertFalse(v1.equals(v2));

        v1 = new DefaultKeyedValue("Test", new Double(45.5));
        v2 = new DefaultKeyedValue("Test", new Double(45.6));
        assertFalse(v1.equals(v2));

    }

    
    public void testCloning() {
        DefaultKeyedValue v1 = new DefaultKeyedValue("Test", new Double(45.5));
        DefaultKeyedValue v2 = null;
        try {
            v2 = (DefaultKeyedValue) v1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(v1 != v2);
        assertTrue(v1.getClass() == v2.getClass());
        assertTrue(v1.equals(v2));

        
        v2.setValue(new Double(12.3));
        assertFalse(v1.equals(v2));
    }

    
    public void testSerialization() {

        DefaultKeyedValue v1 = new DefaultKeyedValue("Test", new Double(25.3));
        DefaultKeyedValue v2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(v1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            v2 = (DefaultKeyedValue) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(v1, v2);

    }

}
