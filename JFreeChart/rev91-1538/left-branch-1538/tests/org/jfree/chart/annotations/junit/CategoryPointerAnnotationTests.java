

package org.jfree.chart.annotations.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.util.PublicCloneable;


public class CategoryPointerAnnotationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CategoryPointerAnnotationTests.class);
    }

    
    public CategoryPointerAnnotationTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        CategoryPointerAnnotation a1 = new CategoryPointerAnnotation("Label",
                "Key 1", 20.0, Math.PI);
        CategoryPointerAnnotation a2 = new CategoryPointerAnnotation("Label",
                "Key 1", 20.0, Math.PI);
        assertTrue(a1.equals(a2));

        a1 = new CategoryPointerAnnotation("Label2", "Key 1", 20.0, Math.PI);
        assertFalse(a1.equals(a2));
        a2 = new CategoryPointerAnnotation("Label2", "Key 1", 20.0, Math.PI);
        assertTrue(a1.equals(a2));

        a1.setCategory("Key 2");
        assertFalse(a1.equals(a2));
        a2.setCategory("Key 2");
        assertTrue(a1.equals(a2));

        a1.setValue(22.0);
        assertFalse(a1.equals(a2));
        a2.setValue(22.0);
        assertTrue(a1.equals(a2));

        
        a1.setAngle(Math.PI / 4.0);
        assertFalse(a1.equals(a2));
        a2.setAngle(Math.PI / 4.0);
        assertTrue(a1.equals(a2));

        
        a1.setTipRadius(20.0);
        assertFalse(a1.equals(a2));
        a2.setTipRadius(20.0);
        assertTrue(a1.equals(a2));

        
        a1.setBaseRadius(5.0);
        assertFalse(a1.equals(a2));
        a2.setBaseRadius(5.0);
        assertTrue(a1.equals(a2));

        
        a1.setArrowLength(33.0);
        assertFalse(a1.equals(a2));
        a2.setArrowLength(33.0);
        assertTrue(a1.equals(a2));

        
        a1.setArrowWidth(9.0);
        assertFalse(a1.equals(a2));
        a2.setArrowWidth(9.0);
        assertTrue(a1.equals(a2));

        
        Stroke stroke = new BasicStroke(1.5f);
        a1.setArrowStroke(stroke);
        assertFalse(a1.equals(a2));
        a2.setArrowStroke(stroke);
        assertTrue(a1.equals(a2));

        
        a1.setArrowPaint(Color.blue);
        assertFalse(a1.equals(a2));
        a2.setArrowPaint(Color.blue);
        assertTrue(a1.equals(a2));

        
        a1.setLabelOffset(10.0);
        assertFalse(a1.equals(a2));
        a2.setLabelOffset(10.0);
        assertTrue(a1.equals(a2));

    }

    
    public void testHashCode() {
        CategoryPointerAnnotation a1 = new CategoryPointerAnnotation("Label",
                "A", 20.0, Math.PI);
        CategoryPointerAnnotation a2 = new CategoryPointerAnnotation("Label",
                "A", 20.0, Math.PI);
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        CategoryPointerAnnotation a1 = new CategoryPointerAnnotation("Label",
                "A", 20.0, Math.PI);
        CategoryPointerAnnotation a2 = null;
        try {
            a2 = (CategoryPointerAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    
    public void testPublicCloneable() {
        CategoryPointerAnnotation a1 = new CategoryPointerAnnotation("Label",
                "A", 20.0, Math.PI);
        assertTrue(a1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        CategoryPointerAnnotation a1 = new CategoryPointerAnnotation("Label",
                "A", 20.0, Math.PI);
        CategoryPointerAnnotation a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            a2 = (CategoryPointerAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);

    }

}
