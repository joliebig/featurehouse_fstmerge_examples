

package org.jfree.chart.title.junit;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.title.DateTitle;


public class DateTitleTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DateTitleTests.class);
    }

    
    public DateTitleTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DateTitle t1 = new DateTitle();
        DateTitle t2 = new DateTitle();
        assertEquals(t1, t2);
        
        t1.setText("Test 1");
        assertFalse(t1.equals(t2));
        t2.setText("Test 1");
        assertTrue(t1.equals(t2));
        
        Font f = new Font("SansSerif", Font.PLAIN, 15);
        t1.setFont(f);
        assertFalse(t1.equals(t2));
        t2.setFont(f);
        assertTrue(t1.equals(t2));
        
        t1.setPaint(Color.blue);
        assertFalse(t1.equals(t2));
        t2.setPaint(Color.blue);
        assertTrue(t1.equals(t2));
        
        t1.setBackgroundPaint(Color.blue);
        assertFalse(t1.equals(t2));
        t2.setBackgroundPaint(Color.blue);
        assertTrue(t1.equals(t2));
        
    }

    
    public void testHashcode() {
        DateTitle t1 = new DateTitle();
        DateTitle t2 = new DateTitle();
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testCloning() {
        DateTitle t1 = new DateTitle();
        DateTitle t2 = null;
        try {
            t2 = (DateTitle) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("DateTitleTests.testCloning: failed to clone.");
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {

        DateTitle t1 = new DateTitle();
        DateTitle t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (DateTitle) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(t1, t2);

    }

}
