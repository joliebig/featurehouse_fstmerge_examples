

package org.jfree.experimental.chart.plot.dial.junit;

import java.awt.Color;
import java.awt.Font;
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

import org.jfree.experimental.chart.plot.dial.DialTextAnnotation;


public class DialTextAnnotationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DialTextAnnotationTests.class);
    }

    
    public DialTextAnnotationTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DialTextAnnotation a1 = new DialTextAnnotation("A1");
        DialTextAnnotation a2 = new DialTextAnnotation("A1");
        assertTrue(a1.equals(a2));
        
        
        a1.setAngle(1.1);
        assertFalse(a1.equals(a2));
        a2.setAngle(1.1);
        assertTrue(a1.equals(a2));
        
        
        a1.setRadius(9.9);
        assertFalse(a1.equals(a2));
        a2.setRadius(9.9);
        assertTrue(a1.equals(a2));
        
        
        Font f = new Font("SansSerif", Font.PLAIN, 14);
        a1.setFont(f);
        assertFalse(a1.equals(a2));
        a2.setFont(f);
        assertTrue(a1.equals(a2));
        
        
        a1.setPaint(Color.red);
        assertFalse(a1.equals(a2));
        a2.setPaint(Color.red);
        assertTrue(a1.equals(a2));
        
        
        a1.setLabel("ABC");
        assertFalse(a1.equals(a2));
        a2.setLabel("ABC");
        assertTrue(a1.equals(a2));
        
        
        a1.setVisible(false);
        assertFalse(a1.equals(a2));
        a2.setVisible(false);
        assertTrue(a1.equals(a2));
    }

    
    public void testHashCode() {
        DialTextAnnotation a1 = new DialTextAnnotation("A1");
        DialTextAnnotation a2 = new DialTextAnnotation("A1");
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        
        DialTextAnnotation a1 = new DialTextAnnotation("A1");
        DialTextAnnotation a2 = null;
        try {
            a2 = (DialTextAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
        
        
        MyDialLayerChangeListener l1 = new MyDialLayerChangeListener();
        a1.addChangeListener(l1);
        assertTrue(a1.hasListener(l1));
        assertFalse(a2.hasListener(l1));
       
    }


    
    public void testSerialization() {
        
        DialTextAnnotation a1 = new DialTextAnnotation("A1");
        DialTextAnnotation a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (DialTextAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);
        
        
        a1 = new DialTextAnnotation("A1");
        a1.setPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f, 
                Color.blue));
        a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (DialTextAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);
        
        
    }

}
