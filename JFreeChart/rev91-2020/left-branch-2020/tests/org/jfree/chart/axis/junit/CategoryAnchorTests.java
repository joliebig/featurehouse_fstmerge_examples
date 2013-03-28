

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.CategoryAnchor;


public class CategoryAnchorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CategoryAnchorTests.class);
    }

    
    public CategoryAnchorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertEquals(CategoryAnchor.START, CategoryAnchor.START);
        assertEquals(CategoryAnchor.MIDDLE, CategoryAnchor.MIDDLE);
        assertEquals(CategoryAnchor.END, CategoryAnchor.END);
        assertFalse(CategoryAnchor.START.equals(CategoryAnchor.END));
        assertFalse(CategoryAnchor.MIDDLE.equals(CategoryAnchor.END));
    }

    
    public void testHashCode() {
        CategoryAnchor a1 = CategoryAnchor.START;
        CategoryAnchor a2 = CategoryAnchor.START;
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testSerialization() {
        CategoryAnchor a1 = CategoryAnchor.MIDDLE;
        CategoryAnchor a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (CategoryAnchor) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);
        assertTrue(a1 == a2);
    }

}
