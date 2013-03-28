

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

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;


public class YIntervalSeriesCollectionTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(YIntervalSeriesCollectionTests.class);
    }

    
    public YIntervalSeriesCollectionTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        YIntervalSeriesCollection c1 = new YIntervalSeriesCollection();
        YIntervalSeriesCollection c2 = new YIntervalSeriesCollection();
        assertEquals(c1, c2);

        
        YIntervalSeries s1 = new YIntervalSeries("Series");
        s1.add(1.0, 1.1, 1.2, 1.3);
        c1.addSeries(s1);
        assertFalse(c1.equals(c2));
        YIntervalSeries s2 = new YIntervalSeries("Series");
        s2.add(1.0, 1.1, 1.2, 1.3);
        c2.addSeries(s2);
        assertTrue(c1.equals(c2));

        
        c1.addSeries(new YIntervalSeries("Empty Series"));
        assertFalse(c1.equals(c2));
        c2.addSeries(new YIntervalSeries("Empty Series"));
        assertTrue(c1.equals(c2));
    }

    
    public void testCloning() {
        YIntervalSeriesCollection c1 = new YIntervalSeriesCollection();
        YIntervalSeries s1 = new YIntervalSeries("Series");
        s1.add(1.0, 1.1, 1.2, 1.3);
        c1.addSeries(s1);
        YIntervalSeriesCollection c2 = null;
        try {
            c2 = (YIntervalSeriesCollection) c1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(c1 != c2);
        assertTrue(c1.getClass() == c2.getClass());
        assertTrue(c1.equals(c2));

        
        s1.setDescription("XYZ");
        assertFalse(c1.equals(c2));
    }

    
    public void testPublicCloneable() {
        YIntervalSeriesCollection c1 = new YIntervalSeriesCollection();
        assertTrue(c1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {
        YIntervalSeriesCollection c1 = new YIntervalSeriesCollection();
        YIntervalSeries s1 = new YIntervalSeries("Series");
        s1.add(1.0, 1.1, 1.2, 1.3);
        YIntervalSeriesCollection c2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(c1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            c2 = (YIntervalSeriesCollection) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(c1, c2);
    }

    
    public void testRemoveSeries() {
        YIntervalSeriesCollection c = new YIntervalSeriesCollection();
        YIntervalSeries s1 = new YIntervalSeries("s1");
        c.addSeries(s1);
        c.removeSeries(0);
        assertEquals(0, c.getSeriesCount());
        c.addSeries(s1);

        boolean pass = false;
        try {
            c.removeSeries(-1);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);

        pass = false;
        try {
            c.removeSeries(1);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void test1170825() {
        YIntervalSeries s1 = new YIntervalSeries("Series1");
        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
        dataset.addSeries(s1);
        try {
             dataset.getSeries(1);
        }
        catch (IllegalArgumentException e) {
            
        }
        catch (IndexOutOfBoundsException e) {
            assertTrue(false);  
        }
    }

}
