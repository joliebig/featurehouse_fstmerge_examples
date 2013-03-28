

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

import org.jfree.chart.axis.CategoryLabelWidthType;


public class CategoryLabelWidthTypeTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CategoryLabelWidthTypeTests.class);
    }

    
    public CategoryLabelWidthTypeTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertEquals(CategoryLabelWidthType.CATEGORY,
                CategoryLabelWidthType.CATEGORY);
        assertEquals(CategoryLabelWidthType.RANGE,
                CategoryLabelWidthType.RANGE);
    }

    
    public void testHashCode() {
        CategoryLabelWidthType a1 = CategoryLabelWidthType.CATEGORY;
        CategoryLabelWidthType a2 = CategoryLabelWidthType.CATEGORY;
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testSerialization() {
        CategoryLabelWidthType w1 = CategoryLabelWidthType.RANGE;
        CategoryLabelWidthType w2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(w1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            w2 = (CategoryLabelWidthType) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(w1, w2);
        assertTrue(w1 == w2);
    }

}
