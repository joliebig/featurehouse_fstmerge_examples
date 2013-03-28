

package org.jfree.chart.title.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.title.ShortTextTitle;


public class ShortTextTitleTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ShortTextTitleTests.class);
    }

    
    public ShortTextTitleTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        ShortTextTitle t1 = new ShortTextTitle("ABC");
        ShortTextTitle t2 = new ShortTextTitle("ABC");
        assertEquals(t1, t2);

        t1.setText("Test 1");
        assertFalse(t1.equals(t2));
        t2.setText("Test 1");
        assertTrue(t1.equals(t2));
    }

    
    public void testHashcode() {
        ShortTextTitle t1 = new ShortTextTitle("ABC");
        ShortTextTitle t2 = new ShortTextTitle("ABC");
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        ShortTextTitle t1 = new ShortTextTitle("ABC");
        ShortTextTitle t2 = null;
        try {
            t2 = (ShortTextTitle) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {

        ShortTextTitle t1 = new ShortTextTitle("ABC");
        ShortTextTitle t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            t2 = (ShortTextTitle) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(t1, t2);

    }

}
