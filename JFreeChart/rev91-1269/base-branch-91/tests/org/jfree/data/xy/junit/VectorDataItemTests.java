

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.xy.VectorDataItem;


public class VectorDataItemTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(VectorDataItemTests.class);
    }

    
    public VectorDataItemTests(String name) {
        super(name);
    }

    
    public void testEquals() {        
        
        VectorDataItem v1 = new VectorDataItem(1.0, 2.0, 3.0, 4.0);
        VectorDataItem v2 = new VectorDataItem(1.0, 2.0, 3.0, 4.0);
        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));

        v1 = new VectorDataItem(1.1, 2.0, 3.0, 4.0);
        assertFalse(v1.equals(v2));
        v2 = new VectorDataItem(1.1, 2.0, 3.0, 4.0);
        assertTrue(v1.equals(v2));
        
        v1 = new VectorDataItem(1.1, 2.2, 3.0, 4.0);
        assertFalse(v1.equals(v2));
        v2 = new VectorDataItem(1.1, 2.2, 3.0, 4.0);
        assertTrue(v1.equals(v2));

        v1 = new VectorDataItem(1.1, 2.2, 3.3, 4.0);
        assertFalse(v1.equals(v2));
        v2 = new VectorDataItem(1.1, 2.2, 3.3, 4.0);
        assertTrue(v1.equals(v2));
    
        v1 = new VectorDataItem(1.1, 2.2, 3.3, 4.4);
        assertFalse(v1.equals(v2));
        v2 = new VectorDataItem(1.1, 2.2, 3.3, 4.4);
        assertTrue(v1.equals(v2));
    }

    
    public void testHashcode() {
        VectorDataItem v1 = new VectorDataItem(1.0, 2.0, 3.0, 4.0);
        VectorDataItem v2 = new VectorDataItem(1.0, 2.0, 3.0, 4.0);
        assertTrue(v1.equals(v2));
        int h1 = v1.hashCode();
        int h2 = v2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        VectorDataItem v1 = new VectorDataItem(1.0, 2.0, 3.0, 4.0);
        VectorDataItem v2 = null;
        try {
            v2 = (VectorDataItem) v1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(v1 != v2);
        assertTrue(v1.getClass() == v2.getClass());
        assertTrue(v1.equals(v2));
    }

    
    public void testSerialization() {
        VectorDataItem v1 = new VectorDataItem(1.0, 2.0, 3.0, 4.0);
        VectorDataItem v2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(v1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            v2 = (VectorDataItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(v1, v2);
    }

}
