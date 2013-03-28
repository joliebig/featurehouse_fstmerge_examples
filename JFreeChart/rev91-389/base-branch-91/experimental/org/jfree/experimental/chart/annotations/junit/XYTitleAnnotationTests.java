

package org.jfree.experimental.chart.annotations.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.title.TextTitle;
import org.jfree.experimental.chart.annotations.XYTitleAnnotation;


public class XYTitleAnnotationTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYTitleAnnotationTests.class);
    }

    
    public XYTitleAnnotationTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        TextTitle t = new TextTitle("Title");
        XYTitleAnnotation a1 = new XYTitleAnnotation(1.0, 2.0, t);
        XYTitleAnnotation a2 = new XYTitleAnnotation(1.0, 2.0, t);
        assertTrue(a1.equals(a2));
        
        a1 = new XYTitleAnnotation(1.1, 2.0, t);
        assertFalse(a1.equals(a2));
        a2 = new XYTitleAnnotation(1.1, 2.0, t);
        assertTrue(a1.equals(a2));

        a1 = new XYTitleAnnotation(1.1, 2.2, t);
        assertFalse(a1.equals(a2));
        a2 = new XYTitleAnnotation(1.1, 2.2, t);
        assertTrue(a1.equals(a2));
        
        TextTitle t2 = new TextTitle("Title 2");
        a1 = new XYTitleAnnotation(1.1, 2.2, t2);
        assertFalse(a1.equals(a2));
        a2 = new XYTitleAnnotation(1.1, 2.2, t2);
        assertTrue(a1.equals(a2));
    }

    
    public void testHashCode() {
        TextTitle t = new TextTitle("Title");
        XYTitleAnnotation a1 = new XYTitleAnnotation(1.0, 2.0, t);
        XYTitleAnnotation a2 = new XYTitleAnnotation(1.0, 2.0, t);
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        TextTitle t = new TextTitle("Title");
        XYTitleAnnotation a1 = new XYTitleAnnotation(1.0, 2.0, t);
        XYTitleAnnotation a2 = null;
        try {
            a2 = (XYTitleAnnotation) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    
    public void testSerialization() {
        TextTitle t = new TextTitle("Title");
        XYTitleAnnotation a1 = new XYTitleAnnotation(1.0, 2.0, t);
        XYTitleAnnotation a2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            a2 = (XYTitleAnnotation) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(a1, a2);
    }

}
