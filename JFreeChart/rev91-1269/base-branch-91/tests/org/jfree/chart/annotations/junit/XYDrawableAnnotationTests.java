

package org.jfree.chart.annotations.junit;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.ui.Drawable;


public class XYDrawableAnnotationTests extends TestCase {

    static class TestDrawable implements Drawable, Cloneable, Serializable {
        
        public TestDrawable() {
        }
        
        public void draw(Graphics2D g2, Rectangle2D area) {
            
        }
        
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof TestDrawable)) {
                return false;
            }
            return true;
        }
    }
    
    
    public static Test suite() {
        return new TestSuite(XYDrawableAnnotationTests.class);
    }

    
    public XYDrawableAnnotationTests(String name) {
        super(name);
    }

    
    public void testEquals() {    
        XYDrawableAnnotation a1 = new XYDrawableAnnotation(
            10.0, 20.0, 100.0, 200.0, new TestDrawable()
        );
        XYDrawableAnnotation a2 = new XYDrawableAnnotation(
            10.0, 20.0, 100.0, 200.0, new TestDrawable()
        );
        assertTrue(a1.equals(a2));
    }
    
    
    public void testHashCode() {
        XYDrawableAnnotation a1 = new XYDrawableAnnotation(
            10.0, 20.0, 100.0, 200.0, new TestDrawable()
        );
        XYDrawableAnnotation a2 = new XYDrawableAnnotation(
            10.0, 20.0, 100.0, 200.0, new TestDrawable()
        );
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        XYDrawableAnnotation a1 = new XYDrawableAnnotation(
            10.0, 20.0, 100.0, 200.0, new TestDrawable()
        );
        XYDrawableAnnotation a2 = null;
        try {
            a2 = (XYDrawableAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    
    public void testSerialization() {

        XYDrawableAnnotation a1 = new XYDrawableAnnotation(
            10.0, 20.0, 100.0, 200.0, new TestDrawable()
        );
        XYDrawableAnnotation a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (XYDrawableAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
