

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.KeyedObjects;
import org.jfree.data.general.DefaultPieDataset;


public class KeyedObjectsTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(KeyedObjectsTests.class);
    }

    
    public KeyedObjectsTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        
    }

    
    public void testCloning() {
        KeyedObjects ko1 = new KeyedObjects();
        ko1.addObject("V1", new Integer(1));
        ko1.addObject("V2", null);
        ko1.addObject("V3", new Integer(3));
        KeyedObjects ko2 = null;
        try {
            ko2 = (KeyedObjects) ko1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(ko1 != ko2);
        assertTrue(ko1.getClass() == ko2.getClass());
        assertTrue(ko1.equals(ko2));
    }
    
    
    public void testCloning2() {
        
        Object obj1 = new ArrayList();
        KeyedObjects ko1 = new KeyedObjects();
        ko1.addObject("K1", obj1);
        KeyedObjects ko2 = null;
        try {
            ko2 = (KeyedObjects) ko1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(ko1 != ko2);
        assertTrue(ko1.getClass() == ko2.getClass());
        assertTrue(ko1.equals(ko2));
        
        
        assertTrue(ko2.getObject("K1") == obj1); 
        
        
        obj1 = new DefaultPieDataset();
        ko1 = new KeyedObjects();
        ko1.addObject("K1", obj1);
        ko2 = null;
        try {
            ko2 = (KeyedObjects) ko1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(ko1 != ko2);
        assertTrue(ko1.getClass() == ko2.getClass());
        assertTrue(ko1.equals(ko2));
        
        
        assertTrue(ko2.getObject("K1") != obj1); 
    }
    
    
    public void testInsertAndRetrieve() {

        KeyedObjects data = new KeyedObjects();
        data.addObject("A", new Double(1.0));
        data.addObject("B", new Double(2.0));
        data.addObject("C", new Double(3.0));
        data.addObject("D", null);

        
        assertEquals(data.getKey(0), "A");
        assertEquals(data.getKey(1), "B");
        assertEquals(data.getKey(2), "C");
        assertEquals(data.getKey(3), "D");

        
        assertEquals(data.getObject("A"), new Double(1.0));
        assertEquals(data.getObject("B"), new Double(2.0));
        assertEquals(data.getObject("C"), new Double(3.0));
        assertEquals(data.getObject("D"), null);
        assertEquals(data.getObject("Not a key"), null);

        
        assertEquals(data.getObject(0), new Double(1.0));
        assertEquals(data.getObject(1), new Double(2.0));
        assertEquals(data.getObject(2), new Double(3.0));
        assertEquals(data.getObject(3), null);

    }

    
    public void testSerialization() {

        KeyedObjects ko1 = new KeyedObjects();
        ko1.addObject("Key 1", "Object 1");
        ko1.addObject("Key 2", null);
        ko1.addObject("Key 3", "Object 2");

        KeyedObjects ko2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(ko1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            ko2 = (KeyedObjects) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(ko1, ko2);

    }

}
