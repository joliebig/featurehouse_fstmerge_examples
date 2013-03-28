

package org.jfree.chart.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.HashUtilities;


public class HashUtilitiesTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(HashUtilitiesTests.class);
    }

    
    public HashUtilitiesTests(String name) {
        super(name);
    }

    
    public void testHashCodeForDoubleArray() {
        double[] a1 = new double[] { 1.0 };
        double[] a2 = new double[] { 1.0 };
        int h1 = HashUtilities.hashCodeForDoubleArray(a1);
        int h2 = HashUtilities.hashCodeForDoubleArray(a2);
        assertTrue(h1 == h2);
        
        double[] a3 = new double[] { 0.5, 1.0 };
        int h3 = HashUtilities.hashCodeForDoubleArray(a3);
        assertFalse(h1 == h3);
    }
}
