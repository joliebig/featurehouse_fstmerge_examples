

package org.jfree.chart.axis.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.AxisSpace;


public class AxisSpaceTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(AxisSpaceTests.class);
    }

    
    public AxisSpaceTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        AxisSpace a1 = new AxisSpace();
        AxisSpace a2 = new AxisSpace();
        assertEquals(a1, a2);
        
        a1.setTop(1.11);
        assertFalse(a1.equals(a2));
        a2.setTop(1.11);
        assertTrue(a1.equals(a2));
        
        a1.setBottom(2.22);
        assertFalse(a1.equals(a2));
        a2.setBottom(2.22);
        assertTrue(a1.equals(a2));

        a1.setLeft(3.33);
        assertFalse(a1.equals(a2));
        a2.setLeft(3.33);
        assertTrue(a1.equals(a2));

        a1.setRight(4.44);
        assertFalse(a1.equals(a2));
        a2.setRight(4.44);
        assertTrue(a1.equals(a2));
    }
    
    
    public void testHashCode() {
        AxisSpace s1 = new AxisSpace();
        AxisSpace s2 = new AxisSpace();
        assertTrue(s1.equals(s2));
        int h1 = s1.hashCode();
        int h2 = s2.hashCode();
        assertEquals(h1, h2);
    }    
    
    
    public void testCloning() {
        AxisSpace s1 = new AxisSpace();
        AxisSpace s2 = null;
        try {
            s2 = (AxisSpace) s1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(s1 != s2);
        assertTrue(s1.getClass() == s2.getClass());
        assertTrue(s1.equals(s2));
    }
    
}

