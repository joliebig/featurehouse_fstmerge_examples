

package org.jfree.chart.junit;

import java.awt.BasicStroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.StrokeMap;


public class StrokeMapTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StrokeMapTests.class);
    }

    
    public StrokeMapTests(String name) {
        super(name);
    }

    
    public void testGetStroke() {
        StrokeMap m1 = new StrokeMap();
        assertEquals(null, m1.getStroke("A"));
        m1.put("A", new BasicStroke(1.1f));
        assertEquals(new BasicStroke(1.1f), m1.getStroke("A"));
        m1.put("A", null);
        assertEquals(null, m1.getStroke("A"));

        
        boolean pass = false;
        try {
            m1.getStroke(null);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testPut() {
        StrokeMap m1 = new StrokeMap();
        m1.put("A", new BasicStroke(1.1f));
        assertEquals(new BasicStroke(1.1f), m1.getStroke("A"));

        
        boolean pass = false;
        try {
            m1.put(null, new BasicStroke(1.1f));
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testEquals() {
        StrokeMap m1 = new StrokeMap();
        StrokeMap m2 = new StrokeMap();
        assertTrue(m1.equals(m1));
        assertTrue(m1.equals(m2));
        assertFalse(m1.equals(null));
        assertFalse(m1.equals("ABC"));

        m1.put("K1", new BasicStroke(1.1f));
        assertFalse(m1.equals(m2));
        m2.put("K1", new BasicStroke(1.1f));
        assertTrue(m1.equals(m2));

        m1.put("K2", new BasicStroke(2.2f));
        assertFalse(m1.equals(m2));
        m2.put("K2", new BasicStroke(2.2f));
        assertTrue(m1.equals(m2));

        m1.put("K2", null);
        assertFalse(m1.equals(m2));
        m2.put("K2", null);
        assertTrue(m1.equals(m2));
    }

    
    public void testCloning() {
        StrokeMap m1 = new StrokeMap();
        StrokeMap m2 = null;
        try {
            m2 = (StrokeMap) m1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(m1.equals(m2));

        m1.put("K1", new BasicStroke(1.1f));
        m1.put("K2", new BasicStroke(2.2f));
        try {
            m2 = (StrokeMap) m1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(m1.equals(m2));
    }

    
    public void testSerialization1() {
        StrokeMap m1 = new StrokeMap();
        StrokeMap m2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            m2 = (StrokeMap) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(m1, m2);
    }

    
    public void testSerialization2() {
        StrokeMap m1 = new StrokeMap();
        m1.put("K1", new BasicStroke(1.1f));
        m1.put("K2", new BasicStroke(2.2f));
        StrokeMap m2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            m2 = (StrokeMap) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(m1, m2);
    }

}

