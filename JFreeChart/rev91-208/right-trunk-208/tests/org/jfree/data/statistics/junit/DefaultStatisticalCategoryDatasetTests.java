

package org.jfree.data.statistics.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.Range;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;


public class DefaultStatisticalCategoryDatasetTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultStatisticalCategoryDatasetTests.class);
    }

    
    public DefaultStatisticalCategoryDatasetTests(String name) {
        super(name);
    }

    
    public void testGetRangeBounds() {
        DefaultStatisticalCategoryDataset d 
            = new DefaultStatisticalCategoryDataset();
        
        
        assertNull(d.getRangeBounds(true));
        
        
        d.add(4.5, 1.0, "R1", "C1");
        assertEquals(new Range(4.5, 4.5), d.getRangeBounds(false));
        assertEquals(new Range(3.5, 5.5), d.getRangeBounds(true));
        
        
        d.add(0.5, 2.0, "R1", "C2");
        assertEquals(new Range(0.5, 4.5), d.getRangeBounds(false));
        assertEquals(new Range(-1.5, 5.5), d.getRangeBounds(true));
        
        
        d.add(Double.NaN, 0.0, "R1", "C3");
        assertEquals(new Range(0.5, 4.5), d.getRangeBounds(false));
        assertEquals(new Range(-1.5, 5.5), d.getRangeBounds(true));

        
        d.add(Double.NEGATIVE_INFINITY, 0.0, "R1", "C3");
        assertEquals(new Range(Double.NEGATIVE_INFINITY, 4.5), 
                d.getRangeBounds(false));
        assertEquals(new Range(Double.NEGATIVE_INFINITY, 5.5), 
                d.getRangeBounds(true));

        
        d.add(Double.POSITIVE_INFINITY, 0.0, "R1", "C3");
        assertEquals(new Range(Double.NEGATIVE_INFINITY, 
                Double.POSITIVE_INFINITY), d.getRangeBounds(false));
        assertEquals(new Range(Double.NEGATIVE_INFINITY, 
                Double.POSITIVE_INFINITY), d.getRangeBounds(true));
    }
    
    
    public void testEquals() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        DefaultStatisticalCategoryDataset d2 
            = new DefaultStatisticalCategoryDataset();
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

    }

    
    public void testCloning() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        d1.add(1.1, 2.2, "R1", "C1");
        d1.add(3.3, 4.4, "R1", "C2");
        d1.add(null, new Double(5.5), "R1", "C3");
        d1.add(new Double(6.6), null, "R2", "C3");
        DefaultStatisticalCategoryDataset d2 = null;
        try {
            d2 = (DefaultStatisticalCategoryDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            fail(e.toString());
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
    }

    
    public void testSerialization1() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        d1.add(1.1, 2.2, "R1", "C1");
        d1.add(3.3, 4.4, "R1", "C2");
        d1.add(null, new Double(5.5), "R1", "C3");
        d1.add(new Double(6.6), null, "R2", "C3");
        DefaultStatisticalCategoryDataset d2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            d2 = (DefaultStatisticalCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(d1, d2);
    }
    
    
    public void testSerialization2() {
        DefaultStatisticalCategoryDataset d1 
            = new DefaultStatisticalCategoryDataset();
        d1.add(1.2, 3.4, "Row 1", "Column 1");
        DefaultStatisticalCategoryDataset d2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            d2 = (DefaultStatisticalCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(d1, d2);
    }
    
}
