

package org.jfree.chart.renderer.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.chart.util.PublicCloneable;


public class DefaultCategoryItemRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultCategoryItemRendererTests.class);
    }

    
    public DefaultCategoryItemRendererTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DefaultCategoryItemRenderer r1 = new DefaultCategoryItemRenderer();
        DefaultCategoryItemRenderer r2 = new DefaultCategoryItemRenderer();
        assertEquals(r1, r2);
    }

    
    public void testHashcode() {
        DefaultCategoryItemRenderer r1 = new DefaultCategoryItemRenderer();
        DefaultCategoryItemRenderer r2 = new DefaultCategoryItemRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        DefaultCategoryItemRenderer r1 = new DefaultCategoryItemRenderer();
        DefaultCategoryItemRenderer r2 = null;
        try {
            r2 = (DefaultCategoryItemRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        DefaultCategoryItemRenderer r1 = new DefaultCategoryItemRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        DefaultCategoryItemRenderer r1 = new DefaultCategoryItemRenderer();
        DefaultCategoryItemRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            r2 = (DefaultCategoryItemRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(r1, r2);

    }

}
