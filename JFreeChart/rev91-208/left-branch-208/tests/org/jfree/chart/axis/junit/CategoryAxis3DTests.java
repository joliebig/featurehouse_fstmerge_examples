

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

import org.jfree.chart.axis.CategoryAxis3D;


public class CategoryAxis3DTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CategoryAxis3DTests.class);
    }

    
    public CategoryAxis3DTests(String name) {
        super(name);
    }

    
    public void testCloning() {
        CategoryAxis3D a1 = new CategoryAxis3D("Test");
        CategoryAxis3D a2 = null;
        try {
            a2 = (CategoryAxis3D) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    
    public void testSerialization() {

        CategoryAxis3D a1 = new CategoryAxis3D("Test Axis");
        CategoryAxis3D a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (CategoryAxis3D) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
