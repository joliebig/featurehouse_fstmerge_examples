

package org.jfree.data.general.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.general.DefaultKeyedValueDataset;


public class DefaultKeyedValueDatasetTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultKeyedValueDatasetTests.class);
    }

    
    public DefaultKeyedValueDatasetTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        DefaultKeyedValueDataset d1 
            = new DefaultKeyedValueDataset("Test", new Double(45.5));
        DefaultKeyedValueDataset d2 
            = new DefaultKeyedValueDataset("Test", new Double(45.5));
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

        d1 = new DefaultKeyedValueDataset("Test 1", new Double(45.5));
        d2 = new DefaultKeyedValueDataset("Test 2", new Double(45.5));
        assertFalse(d1.equals(d2));

        d1 = new DefaultKeyedValueDataset("Test", new Double(45.5));
        d2 = new DefaultKeyedValueDataset("Test", new Double(45.6));
        assertFalse(d1.equals(d2));

    }

    
    public void testCloning() {
        DefaultKeyedValueDataset d1 
            = new DefaultKeyedValueDataset("Test", new Double(45.5));
        DefaultKeyedValueDataset d2 = null;
        try {
            d2 = (DefaultKeyedValueDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
    }
    
    
    public void testCloneIndependence() {
        DefaultKeyedValueDataset d1 
            = new DefaultKeyedValueDataset("Key", new Double(10.0));
        DefaultKeyedValueDataset d2 = null;
        try {
            d2 = (DefaultKeyedValueDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(d1.equals(d2));
        d2.updateValue(new Double(99.9));
        assertFalse(d1.equals(d2));
        d2.updateValue(new Double(10.0));
        assertTrue(d1.equals(d2));
    }

    
    public void testSerialization() {

        DefaultKeyedValueDataset d1  
            = new DefaultKeyedValueDataset("Test", new Double(25.3));
        DefaultKeyedValueDataset d2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DefaultKeyedValueDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);
    }

}
