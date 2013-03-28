

package org.jfree.data.time.junit;

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
import org.jfree.data.time.Day;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;


public class TimePeriodValuesCollectionTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(TimePeriodValuesCollectionTests.class);
    }

    
    public TimePeriodValuesCollectionTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        
    }

    
    public void test1161340() {
        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        TimePeriodValues v1 = new TimePeriodValues("V1");
        v1.add(new Day(11, 3, 2005), 1.2);
        v1.add(new Day(12, 3, 2005), 3.4);
        dataset.addSeries(v1);
        assertEquals(1, dataset.getSeriesCount());
        dataset.removeSeries(v1);
        assertEquals(0, dataset.getSeriesCount());
        
        TimePeriodValues v2 = new TimePeriodValues("V2");
        v1.add(new Day(5, 3, 2005), 1.2);
        v1.add(new Day(6, 3, 2005), 3.4);
        dataset.addSeries(v2);
        assertEquals(1, dataset.getSeriesCount());
    }
    
    
    public void testEquals() {
        
        TimePeriodValuesCollection c1 = new TimePeriodValuesCollection();
        TimePeriodValuesCollection c2 = new TimePeriodValuesCollection();
        assertTrue(c1.equals(c2));
        
        c1.setXPosition(TimePeriodAnchor.END);
        assertFalse(c1.equals(c2));
        c2.setXPosition(TimePeriodAnchor.END);
        assertTrue(c1.equals(c2));
        
        TimePeriodValues v1 = new TimePeriodValues("Test");
        TimePeriodValues v2 = new TimePeriodValues("Test");
        
        c1.addSeries(v1);
        assertFalse(c1.equals(c2));
        c2.addSeries(v2);
        assertTrue(c1.equals(c2));
    }

    
    public void testSerialization() {
        TimePeriodValuesCollection c1 = new TimePeriodValuesCollection();
        TimePeriodValuesCollection c2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(c1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            c2 = (TimePeriodValuesCollection) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(c1, c2);
    }

    
    public void testGetSeries() {
        TimePeriodValuesCollection c1 = new TimePeriodValuesCollection();
        TimePeriodValues s1 = new TimePeriodValues("Series 1");
        c1.addSeries(s1);
        assertEquals("Series 1", c1.getSeries(0).getKey());
        
        boolean pass = false;
        try {
            c1.getSeries(-1);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
        
        pass = false;
        try {
            c1.getSeries(1);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }
    
    private static final double EPSILON = 0.0000000001;
    
    
    public void testGetDomainBoundsWithoutInterval() {
        
        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        Range r = dataset.getDomainBounds(false);
        assertNull(r);
        
        
        TimePeriodValues s1 = new TimePeriodValues("S1");
        s1.add(new SimpleTimePeriod(1000L, 2000L), 1.0);
        dataset.addSeries(s1);
        r = dataset.getDomainBounds(false);
        assertEquals(1500.0, r.getLowerBound(), EPSILON);
        assertEquals(1500.0, r.getUpperBound(), EPSILON);
        
        
        s1.add(new SimpleTimePeriod(1500L, 3000L), 2.0);
        r = dataset.getDomainBounds(false);
        assertEquals(1500.0, r.getLowerBound(), EPSILON);
        assertEquals(2250.0, r.getUpperBound(), EPSILON);  
    }

    
    public void testGetDomainBoundsWithInterval() {
        
        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        Range r = dataset.getDomainBounds(true);
        assertNull(r);
        
        
        TimePeriodValues s1 = new TimePeriodValues("S1");
        s1.add(new SimpleTimePeriod(1000L, 2000L), 1.0);
        dataset.addSeries(s1);
        r = dataset.getDomainBounds(true);
        assertEquals(1000.0, r.getLowerBound(), EPSILON);
        assertEquals(2000.0, r.getUpperBound(), EPSILON);
        
        
        s1.add(new SimpleTimePeriod(1500L, 3000L), 2.0);
        r = dataset.getDomainBounds(true);
        assertEquals(1000.0, r.getLowerBound(), EPSILON);
        assertEquals(3000.0, r.getUpperBound(), EPSILON);  
    }
}
