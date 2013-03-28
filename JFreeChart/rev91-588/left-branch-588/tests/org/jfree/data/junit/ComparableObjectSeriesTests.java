

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.ComparableObjectItem;
import org.jfree.data.ComparableObjectSeries;


public class ComparableObjectSeriesTests extends TestCase {

    static class MyComparableObjectSeries extends ComparableObjectSeries {
        
        public MyComparableObjectSeries(Comparable key) {
            super(key);
        }
        
        public MyComparableObjectSeries(Comparable key, boolean autoSort, 
                boolean allowDuplicateXValues) {
            super(key, autoSort, allowDuplicateXValues);
        }
        public void add(Comparable x, Object y) {
            super.add(x, y);
        }

        public ComparableObjectItem remove(Comparable x) {
            return super.remove(x);
        }
    }
    
    
    public static Test suite() {
        return new TestSuite(ComparableObjectSeriesTests.class);
    }

    
    public ComparableObjectSeriesTests(String name) {
        super(name);
    }

    
    public void testConstructor1() {
        ComparableObjectSeries s1 = new ComparableObjectSeries("s1");
        assertEquals("s1", s1.getKey());
        assertNull(s1.getDescription());
        assertTrue(s1.getAllowDuplicateXValues());
        assertTrue(s1.getAutoSort());
        assertEquals(0, s1.getItemCount());
        assertEquals(Integer.MAX_VALUE, s1.getMaximumItemCount());
        
        
        boolean pass = false;
        try {
            s1 = new ComparableObjectSeries(null);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }
    
    
    public void testEquals() {
        MyComparableObjectSeries s1 = new MyComparableObjectSeries("A");
        MyComparableObjectSeries s2 = new MyComparableObjectSeries("A");
        assertTrue(s1.equals(s2));
        assertTrue(s2.equals(s1));

        
        s1 = new MyComparableObjectSeries("B");
        assertFalse(s1.equals(s2));
        s2 = new MyComparableObjectSeries("B");
        assertTrue(s1.equals(s2));
        
        
        s1 = new MyComparableObjectSeries("B", false, true);
        assertFalse(s1.equals(s2));
        s2 = new MyComparableObjectSeries("B", false, true);
        assertTrue(s1.equals(s2));

        
        s1 = new MyComparableObjectSeries("B", false, false);
        assertFalse(s1.equals(s2));
        s2 = new MyComparableObjectSeries("B", false, false);
        assertTrue(s1.equals(s2));

        
        s1.add(new Integer(1), "ABC");
        assertFalse(s1.equals(s2));
        s2.add(new Integer(1), "ABC");
        assertTrue(s1.equals(s2));
        
        
        s1.add(new Integer(0), "DEF");
        assertFalse(s1.equals(s2));
        s2.add(new Integer(0), "DEF");
        assertTrue(s1.equals(s2));
        
        
        s1.remove(new Integer(1));
        assertFalse(s1.equals(s2));
        s2.remove(new Integer(1));
        assertTrue(s1.equals(s2));
    }

    
    public void testCloning() {
        MyComparableObjectSeries s1 = new MyComparableObjectSeries("A");
        s1.add(new Integer(1), "ABC");
        MyComparableObjectSeries s2 = null;
        try {
            s2 = (MyComparableObjectSeries) s1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(s1 != s2);
        assertTrue(s1.getClass() == s2.getClass());
        assertTrue(s1.equals(s2));
    }

    
    public void testSerialization() {
        MyComparableObjectSeries s1 = new MyComparableObjectSeries("A");
        s1.add(new Integer(1), "ABC");
        MyComparableObjectSeries s2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            s2 = (MyComparableObjectSeries) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(s1, s2);
    }
    
    
    public void testHashCode() {
        MyComparableObjectSeries s1 = new MyComparableObjectSeries("Test");
        MyComparableObjectSeries s2 = new MyComparableObjectSeries("Test");
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        
        s1.add("A", "1");
        s2.add("A", "1");
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        
        s1.add("B", null);
        s2.add("B", null);
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        
        s1.add("C", "3");
        s2.add("C", "3");
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());

        s1.add("D", "4");
        s2.add("D", "4");
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

}
