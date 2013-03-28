

package org.jfree.chart.annotations.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.annotations.XYBoxAnnotation;


public class XYBoxAnnotationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYBoxAnnotationTests.class);
    }

    
    public XYBoxAnnotationTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        XYBoxAnnotation a1 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(1.2f), Color.red, Color.blue
        );
        XYBoxAnnotation a2 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(1.2f), Color.red, Color.blue
        );
        assertTrue(a1.equals(a2));
        assertTrue(a2.equals(a1));
      
        
        a1 = new XYBoxAnnotation(
            2.0, 2.0, 3.0, 4.0, new BasicStroke(1.2f), Color.red, Color.blue
        );
        assertFalse(a1.equals(a2));
        a2 = new XYBoxAnnotation(
            2.0, 2.0, 3.0, 4.0, new BasicStroke(1.2f), Color.red, Color.blue
        );
        assertTrue(a1.equals(a2));
        
        
        a1 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(2.3f), Color.red, Color.blue
        );
        assertFalse(a1.equals(a2));
        a2 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(2.3f), Color.red, Color.blue
        );
        assertTrue(a1.equals(a2));
        
        GradientPaint gp1a = new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red);
        GradientPaint gp1b = new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red);
        GradientPaint gp2a = new GradientPaint(5.0f, 6.0f, Color.pink, 
                7.0f, 8.0f, Color.white);
        GradientPaint gp2b = new GradientPaint(5.0f, 6.0f, Color.pink, 
                7.0f, 8.0f, Color.white);
        
        
        a1 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(2.3f), gp1a, Color.blue
        );
        assertFalse(a1.equals(a2));
        a2 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(2.3f), gp1b, Color.blue
        );
        assertTrue(a1.equals(a2));
        
        
        a1 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(2.3f), gp1a, gp2a
        );
        assertFalse(a1.equals(a2));
        a2 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(2.3f), gp1b, gp2b
        );
        assertTrue(a1.equals(a2));
    }

    
    public void testHashCode() {
        XYBoxAnnotation a1 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(1.2f), Color.red, Color.blue
        );
        XYBoxAnnotation a2 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(1.2f), Color.red, Color.blue
        );
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {

        XYBoxAnnotation a1 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, new BasicStroke(1.2f), Color.red, Color.blue
        );
        XYBoxAnnotation a2 = null;
        try {
            a2 = (XYBoxAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    
    public void testSerialization() {

        XYBoxAnnotation a1 = new XYBoxAnnotation(
            1.0, 2.0, 3.0, 4.0, 
            new BasicStroke(1.2f), Color.red, Color.blue
        );
        XYBoxAnnotation a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (XYBoxAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }

}
