

package org.jfree.data.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.DefaultIntervalCategoryDataset;


public class DefaultIntervalCategoryDatasetTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(DefaultIntervalCategoryDatasetTests.class);
    }

    
    public DefaultIntervalCategoryDatasetTests(String name) {
        super(name);
    }
    
    
    public void testGetValue() {        
        double[] starts_S1 = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2 = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1 = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2 = new double[] {0.7, 0.8, 0.9};
        double[][] starts = new double[][] {starts_S1, starts_S2};
        double[][] ends = new double[][] {ends_S1, ends_S2};
        DefaultIntervalCategoryDataset d 
                = new DefaultIntervalCategoryDataset(starts, ends);        

        assertEquals(new Double(0.1), d.getStartValue("Series 1", 
                "Category 1"));
        assertEquals(new Double(0.2), d.getStartValue("Series 1", 
                "Category 2"));
        assertEquals(new Double(0.3), d.getStartValue("Series 1", 
                "Category 3"));
        assertEquals(new Double(0.3), d.getStartValue("Series 2", 
                "Category 1"));
        assertEquals(new Double(0.4), d.getStartValue("Series 2", 
                "Category 2"));
        assertEquals(new Double(0.5), d.getStartValue("Series 2", 
                "Category 3"));
        
        assertEquals(new Double(0.5), d.getEndValue("Series 1", 
                "Category 1"));
        assertEquals(new Double(0.6), d.getEndValue("Series 1", 
                "Category 2"));
        assertEquals(new Double(0.7), d.getEndValue("Series 1", 
                "Category 3"));
        assertEquals(new Double(0.7), d.getEndValue("Series 2", 
                "Category 1"));
        assertEquals(new Double(0.8), d.getEndValue("Series 2", 
                "Category 2"));
        assertEquals(new Double(0.9), d.getEndValue("Series 2", 
                "Category 3"));

        boolean pass = false;
        try {
            d.getValue("XX", "Category 1");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass);
        
        pass = false;
        try {
            d.getValue("Series 1", "XX");
        }
        catch (UnknownKeyException e) {
            pass = true;   
        }
        assertTrue(pass);
    }
    
   
    
    public void testGetRowAndColumnCount() {
        double[] starts_S1 = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2 = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1 = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2 = new double[] {0.7, 0.8, 0.9};
        double[][] starts = new double[][] {starts_S1, starts_S2};
        double[][] ends = new double[][] {ends_S1, ends_S2};
        DefaultIntervalCategoryDataset d 
                = new DefaultIntervalCategoryDataset(starts, ends);        

        assertEquals(2, d.getRowCount());
        assertEquals(3, d.getColumnCount());
    }

    
    public void testEquals() {
        double[] starts_S1A = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2A = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1A = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2A = new double[] {0.7, 0.8, 0.9};
        double[][] startsA = new double[][] {starts_S1A, starts_S2A};
        double[][] endsA = new double[][] {ends_S1A, ends_S2A};
        DefaultIntervalCategoryDataset dA 
                = new DefaultIntervalCategoryDataset(startsA, endsA);        

        double[] starts_S1B = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2B = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1B = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2B = new double[] {0.7, 0.8, 0.9};
        double[][] startsB = new double[][] {starts_S1B, starts_S2B};
        double[][] endsB = new double[][] {ends_S1B, ends_S2B};
        DefaultIntervalCategoryDataset dB 
                = new DefaultIntervalCategoryDataset(startsB, endsB);        
            
        assertTrue(dA.equals(dB));
        assertTrue(dB.equals(dA));
    }

    
    public void testSerialization() {

        double[] starts_S1 = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2 = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1 = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2 = new double[] {0.7, 0.8, 0.9};
        double[][] starts = new double[][] {starts_S1, starts_S2};
        double[][] ends = new double[][] {ends_S1, ends_S2};
        DefaultIntervalCategoryDataset d1
                = new DefaultIntervalCategoryDataset(starts, ends);        
        DefaultIntervalCategoryDataset d2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            d2 = (DefaultIntervalCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(d1, d2);

    }
    
    
    public void testCloning() {
        double[] starts_S1 = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2 = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1 = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2 = new double[] {0.7, 0.8, 0.9};
        double[][] starts = new double[][] {starts_S1, starts_S2};
        double[][] ends = new double[][] {ends_S1, ends_S2};
        DefaultIntervalCategoryDataset d1 = new DefaultIntervalCategoryDataset(
                starts, ends);
        DefaultIntervalCategoryDataset d2 = null;
        try {
            d2 = (DefaultIntervalCategoryDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
        
        
        d1.setStartValue(0, "Category 1", new Double(0.99));
        assertFalse(d1.equals(d2));
        d2.setStartValue(0, "Category 1", new Double(0.99));
        assertTrue(d1.equals(d2));
    }

    
    public void testSetStartValue() {
        double[] starts_S1 = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2 = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1 = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2 = new double[] {0.7, 0.8, 0.9};
        double[][] starts = new double[][] {starts_S1, starts_S2};
        double[][] ends = new double[][] {ends_S1, ends_S2};
        DefaultIntervalCategoryDataset d1 = new DefaultIntervalCategoryDataset(
                starts, ends);
        d1.setStartValue(0, "Category 2", new Double(99.9));
        assertEquals(new Double(99.9), d1.getStartValue("Series 1", 
                "Category 2"));
        
        boolean pass = false;
        try {
            d1.setStartValue(-1, "Category 2", new Double(99.9));
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
        
        pass = false;
        try {
            d1.setStartValue(2, "Category 2", new Double(99.9));
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }
   
    
    public void testSetEndValue() {
        double[] starts_S1 = new double[] {0.1, 0.2, 0.3};
        double[] starts_S2 = new double[] {0.3, 0.4, 0.5};
        double[] ends_S1 = new double[] {0.5, 0.6, 0.7};
        double[] ends_S2 = new double[] {0.7, 0.8, 0.9};
        double[][] starts = new double[][] {starts_S1, starts_S2};
        double[][] ends = new double[][] {ends_S1, ends_S2};
        DefaultIntervalCategoryDataset d1 = new DefaultIntervalCategoryDataset(
                starts, ends);
        d1.setEndValue(0, "Category 2", new Double(99.9));
        assertEquals(new Double(99.9), d1.getEndValue("Series 1", 
                "Category 2"));
        
        boolean pass = false;
        try {
            d1.setEndValue(-1, "Category 2", new Double(99.9));
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
        
        pass = false;
        try {
            d1.setEndValue(2, "Category 2", new Double(99.9));
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }
}
