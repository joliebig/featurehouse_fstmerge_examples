

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.xy.DefaultXYDataset;


public class DefaultXYDatasetTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultXYDatasetTests.class);
    }

    
    public DefaultXYDatasetTests(String name) {
        super(name);
    }

    
    public void testEquals() {
 
        DefaultXYDataset d1 = new DefaultXYDataset();
        DefaultXYDataset d2 = new DefaultXYDataset();
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

        double[] x1 = new double[] {1.0, 2.0, 3.0};
        double[] y1 = new double[] {4.0, 5.0, 6.0};
        double[][] data1 = new double[][] {x1, y1};
        double[] x2 = new double[] {1.0, 2.0, 3.0};
        double[] y2 = new double[] {4.0, 5.0, 6.0};
        double[][] data2 = new double[][] {x2, y2};
        d1.addSeries("S1", data1);
        assertFalse(d1.equals(d2));
        d2.addSeries("S1", data2);
        assertTrue(d1.equals(d2));
    }

    
    public void testCloning() {
        DefaultXYDataset d1 = new DefaultXYDataset();
        DefaultXYDataset d2 = null;
        try {
            d2 = (DefaultXYDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
        
        
        double[] x1 = new double[] {1.0, 2.0, 3.0};
        double[] y1 = new double[] {4.0, 5.0, 6.0};
        double[][] data1 = new double[][] {x1, y1};
        d1.addSeries("S1", data1);
        try {
            d2 = (DefaultXYDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
        
        
        x1[1] = 2.2;
        assertFalse(d1.equals(d2));
        x1[1] = 2.0;
        assertTrue(d1.equals(d2));
    }

    
    public void testSerialization() {

        DefaultXYDataset d1 = new DefaultXYDataset();
        DefaultXYDataset d2 = null;
        
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DefaultXYDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(d1, d2);

        
        double[] x1 = new double[] {1.0, 2.0, 3.0};
        double[] y1 = new double[] {4.0, 5.0, 6.0};
        double[][] data1 = new double[][] {x1, y1};
        d1.addSeries("S1", data1);
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DefaultXYDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(d1, d2);
        
    }
    
    
    public void testGetSeriesKey() {
        DefaultXYDataset d = createSampleDataset1();
        assertEquals("S1", d.getSeriesKey(0));
        assertEquals("S2", d.getSeriesKey(1));
        
        
        boolean pass = false;
        try {
             d.getSeriesKey(-1);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
        
        pass = false;
        try {
             d.getSeriesKey(2);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }
    
    
    public void testIndexOf() {
        DefaultXYDataset d = createSampleDataset1();
        assertEquals(0, d.indexOf("S1"));
        assertEquals(1, d.indexOf("S2"));
        assertEquals(-1, d.indexOf("Green Eggs and Ham"));
        assertEquals(-1, d.indexOf(null));
    }
    
    static final double EPSILON = 0.0000000001;
    
    
    public void testAddSeries() {
        DefaultXYDataset d = new DefaultXYDataset();
        d.addSeries("S1", new double[][] {{1.0}, {2.0}});
        assertEquals(1, d.getSeriesCount());
        assertEquals("S1", d.getSeriesKey(0));
        
        
        d.addSeries("S1", new double[][] {{11.0}, {12.0}});
        assertEquals(1, d.getSeriesCount());
        assertEquals(12.0, d.getYValue(0, 0), EPSILON);
        
        
        boolean pass = false;
        try
        {
          d.addSeries(null, new double[][] {{1.0}, {2.0}});
        }
        catch (IllegalArgumentException e)
        {
          pass = true;
        }
        assertTrue(pass);
    }
    
    
    public DefaultXYDataset createSampleDataset1() {
        DefaultXYDataset d = new DefaultXYDataset();
        double[] x1 = new double[] {1.0, 2.0, 3.0};
        double[] y1 = new double[] {4.0, 5.0, 6.0};
        double[][] data1 = new double[][] {x1, y1};
        d.addSeries("S1", data1);
        
        double[] x2 = new double[] {1.0, 2.0, 3.0};
        double[] y2 = new double[] {4.0, 5.0, 6.0};
        double[][] data2 = new double[][] {x2, y2};
        d.addSeries("S2", data2);
        return d;
    }
    
}
