

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

import org.jfree.data.ComparableObjectItem;


public class ComparableObjectItemTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ComparableObjectItemTests.class);
    }

    
    public ComparableObjectItemTests(String name) {
        super(name);
    }

    
    public void testConstructor() {
        
        boolean pass = false;
        try {
             new ComparableObjectItem(null, 
                    "XYZ");
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }
    
    
    public void testEquals() {
        ComparableObjectItem item1 = new ComparableObjectItem(new Integer(1), 
                "XYZ");
        ComparableObjectItem item2 = new ComparableObjectItem(new Integer(1), 
                "XYZ");
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));

        item1 = new ComparableObjectItem(new Integer(2), "XYZ");
        assertFalse(item1.equals(item2));
        item2 = new ComparableObjectItem(new Integer(2), "XYZ");
        assertTrue(item1.equals(item2));
        
        item1 = new ComparableObjectItem(new Integer(2), null);
        assertFalse(item1.equals(item2));
        item2 = new ComparableObjectItem(new Integer(2), null);
        assertTrue(item1.equals(item2));        
    }

    
    public void testCloning() {
        ComparableObjectItem item1 = new ComparableObjectItem(new Integer(1), 
                "XYZ");
        ComparableObjectItem item2 = null;
        try {
            item2 = (ComparableObjectItem) item1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(item1 != item2);
        assertTrue(item1.getClass() == item2.getClass());
        assertTrue(item1.equals(item2));
    }

    
    public void testSerialization() {
        ComparableObjectItem item1 = new ComparableObjectItem(new Integer(1), 
                "XYZ");
        ComparableObjectItem item2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(item1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            item2 = (ComparableObjectItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(item1, item2);
    }
    
    
    public void testCompareTo() {
        ComparableObjectItem item1 = new ComparableObjectItem(new Integer(1), 
                "XYZ");
        ComparableObjectItem item2 = new ComparableObjectItem(new Integer(2), 
                "XYZ");
        ComparableObjectItem item3 = new ComparableObjectItem(new Integer(3), 
                "XYZ");
        ComparableObjectItem item4 = new ComparableObjectItem(new Integer(1), 
                "XYZ");
        assertTrue(item2.compareTo(item1) > 0);
        assertTrue(item3.compareTo(item1) > 0);
        assertTrue(item4.compareTo(item1) == 0);
        assertTrue(item1.compareTo(item2) < 0);
    }

}
