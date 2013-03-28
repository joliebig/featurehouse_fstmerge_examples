

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
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.xy.DefaultWindDataset;


public class DefaultWindDatasetTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultWindDatasetTests.class);
    }

    
    public DefaultWindDatasetTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DefaultWindDataset d1 = new DefaultWindDataset();
        DefaultWindDataset d2 = new DefaultWindDataset();
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));

        d1 = createSampleDataset1();
        assertFalse(d1.equals(d2));
        d2 = createSampleDataset1();
        assertTrue(d1.equals(d2));
    }

    
    public void testCloning() {
        DefaultWindDataset d1 = new DefaultWindDataset();
        DefaultWindDataset d2 = null;
        try {
            d2 = (DefaultWindDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));

        
        d1 = createSampleDataset1();
        d2 = null;
        try {
            d2 = (DefaultWindDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
    }

    
    public void testPublicCloneable() {
        DefaultWindDataset d1 = new DefaultWindDataset();
        assertTrue(d1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {
        DefaultWindDataset d1 = new DefaultWindDataset();
        DefaultWindDataset d2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            d2 = (DefaultWindDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(d1, d2);

        
        d1 = createSampleDataset1();
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            d2 = (DefaultWindDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(d1, d2);

    }

    
    public void testGetSeriesKey() {
        DefaultWindDataset d = createSampleDataset1();
        assertEquals("Series 1", d.getSeriesKey(0));
        assertEquals("Series 2", d.getSeriesKey(1));

        
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
        DefaultWindDataset d = createSampleDataset1();
        assertEquals(0, d.indexOf("Series 1"));
        assertEquals(1, d.indexOf("Series 2"));
        assertEquals(-1, d.indexOf("Green Eggs and Ham"));
        assertEquals(-1, d.indexOf(null));
    }

    
    public DefaultWindDataset createSampleDataset1() {
        Day t = new Day(1, 4, 2006);
        Object[] item1 = createItem(t, 3, 7);
        Object[] item2 = createItem(t.next(), 4, 8);
        Object[] item3 = createItem(t.next(), 5, 9);
        Object[][] series1 = new Object[][] {item1, item2, item3};
        Object[] item1b = createItem(t, 6, 10);
        Object[] item2b = createItem(t.next(), 7, 11);
        Object[] item3b = createItem(t.next(), 8, 12);
        Object[][] series2 = new Object[][] {item1b, item2b, item3b};
        Object[][][] data = new Object[][][] {series1, series2};
        return new DefaultWindDataset(data);
    }

    
    private Object[] createItem(RegularTimePeriod t, int dir, int force) {
        return new Object[] {new Long(t.getMiddleMillisecond()),
                new Integer(dir), new Integer(force)};
    }
}
