

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.Year;
import org.jfree.date.MonthConstants;


public class TimePeriodValuesTests extends TestCase {

    
    private TimePeriodValues seriesA;

    
    private TimePeriodValues seriesB;

    
    private TimePeriodValues seriesC;

    
    public static Test suite() {
        return new TestSuite(TimePeriodValuesTests.class);
    }

    
    public TimePeriodValuesTests(String name) {
        super(name);
    }

    
    protected void setUp() {

        this.seriesA = new TimePeriodValues("Series A");
        try {
            this.seriesA.add(new Year(2000), new Integer(102000));
            this.seriesA.add(new Year(2001), new Integer(102001));
            this.seriesA.add(new Year(2002), new Integer(102002));
            this.seriesA.add(new Year(2003), new Integer(102003));
            this.seriesA.add(new Year(2004), new Integer(102004));
            this.seriesA.add(new Year(2005), new Integer(102005));
        }
        catch (SeriesException e) {
            System.err.println("Problem creating series.");
        }

        this.seriesB = new TimePeriodValues("Series B");
        try {
            this.seriesB.add(new Year(2006), new Integer(202006));
            this.seriesB.add(new Year(2007), new Integer(202007));
            this.seriesB.add(new Year(2008), new Integer(202008));
        }
        catch (SeriesException e) {
            System.err.println("Problem creating series.");
        }

        this.seriesC = new TimePeriodValues("Series C");
        try {
            this.seriesC.add(new Year(1999), new Integer(301999));
            this.seriesC.add(new Year(2000), new Integer(302000));
            this.seriesC.add(new Year(2002), new Integer(302002));
        }
        catch (SeriesException e) {
            System.err.println("Problem creating series.");
        }

    }

    
    public void testClone() {

        TimePeriodValues series = new TimePeriodValues("Test Series");

        RegularTimePeriod jan1st2002 = new Day(1, MonthConstants.JANUARY, 2002);
        try {
            series.add(jan1st2002, new Integer(42));
        }
        catch (SeriesException e) {
            System.err.println("Problem adding to collection.");
        }

        TimePeriodValues clone = null;
        try {
            clone = (TimePeriodValues) series.clone();
            clone.setKey("Clone Series");
            try {
                clone.update(0, new Integer(10));
            }
            catch (SeriesException e) {
                System.err.println("Problem updating series.");
            }
        }
        catch (CloneNotSupportedException e) {
            assertTrue(false);
        }

        int seriesValue = series.getValue(0).intValue();
        int cloneValue = clone.getValue(0).intValue();

        assertEquals(42, seriesValue);
        assertEquals(10, cloneValue);
        assertEquals("Test Series", series.getKey());
        assertEquals("Clone Series", clone.getKey());

    }

    
    public void testAddValue() {

        TimePeriodValues tpvs = new TimePeriodValues("Test");
        try {
            tpvs.add(new Year(1999), new Integer(1));
        }
        catch (SeriesException e) {
            System.err.println("Problem adding to series.");
        }

        int value = tpvs.getValue(0).intValue();
        assertEquals(1, value);

    }

    
    public void testSerialization() {

        TimePeriodValues s1 = new TimePeriodValues("A test");
        s1.add(new Year(2000), 13.75);
        s1.add(new Year(2001), 11.90);
        s1.add(new Year(2002), null);
        s1.add(new Year(2005), 19.32);
        s1.add(new Year(2007), 16.89);
        TimePeriodValues s2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            s2 = (TimePeriodValues) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(s1.equals(s2));

    }

    
    public void testEquals() {
        TimePeriodValues s1 = new TimePeriodValues("Time Series 1");
        TimePeriodValues s2 = new TimePeriodValues("Time Series 2");
        boolean b1 = s1.equals(s2);
        assertFalse("b1", b1);

        s2.setKey("Time Series 1");
        boolean b2 = s1.equals(s2);
        assertTrue("b2", b2);

        
        s1.setDomainDescription("XYZ");
        assertFalse(s1.equals(s2));
        s2.setDomainDescription("XYZ");
        assertTrue(s1.equals(s2));
        
        
        s1.setDomainDescription(null);
        assertFalse(s1.equals(s2));
        s2.setDomainDescription(null);
        assertTrue(s1.equals(s2));
        
        
        s1.setRangeDescription("XYZ");
        assertFalse(s1.equals(s2));
        s2.setRangeDescription("XYZ");
        assertTrue(s1.equals(s2));
        
        
        s1.setRangeDescription(null);
        assertFalse(s1.equals(s2));
        s2.setRangeDescription(null);
        assertTrue(s1.equals(s2));

        RegularTimePeriod p1 = new Day();
        RegularTimePeriod p2 = p1.next();
        s1.add(p1, 100.0);
        s1.add(p2, 200.0);
        boolean b3 = s1.equals(s2);
        assertFalse("b3", b3);

        s2.add(p1, 100.0);
        s2.add(p2, 200.0);
        boolean b4 = s1.equals(s2);
        assertTrue("b4", b4);

    }
    
    
    public void test1161329() {
        TimePeriodValues tpv = new TimePeriodValues("Test");
        RegularTimePeriod t = new Day();
        tpv.add(t, 1.0);
        t = t.next();
        tpv.add(t, 2.0);
        tpv.delete(0, 1);
        assertEquals(0, tpv.getItemCount());
        tpv.add(t, 2.0);
        assertEquals(1, tpv.getItemCount());
    }
    
    static final double EPSILON = 0.0000000001;
    
    
    public void testAdd() {
        TimePeriodValues tpv = new TimePeriodValues("Test");
        MySeriesChangeListener listener = new MySeriesChangeListener();
        tpv.addChangeListener(listener);
        tpv.add(new TimePeriodValue(new SimpleTimePeriod(new Date(1L), 
                new Date(3L)), 99.0));
        assertEquals(99.0, tpv.getValue(0).doubleValue(), EPSILON);
        assertEquals(tpv, listener.getLastEvent().getSource());
        
        
        boolean pass = false;
        try {
            tpv.add((TimePeriodValue) null);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }
    
    
    public void testGetMinStartIndex() {
        TimePeriodValues s = new TimePeriodValues("Test");
        assertEquals(-1, s.getMinStartIndex());
        s.add(new SimpleTimePeriod(100L, 200L), 1.0);
        assertEquals(0, s.getMinStartIndex());
        s.add(new SimpleTimePeriod(300L, 400L), 2.0);
        assertEquals(0, s.getMinStartIndex());
        s.add(new SimpleTimePeriod(0L, 50L), 3.0);
        assertEquals(2, s.getMinStartIndex());
    }
    
    
    public void testGetMaxStartIndex() {
        TimePeriodValues s = new TimePeriodValues("Test");
        assertEquals(-1, s.getMaxStartIndex());
        s.add(new SimpleTimePeriod(100L, 200L), 1.0);
        assertEquals(0, s.getMaxStartIndex());
        s.add(new SimpleTimePeriod(300L, 400L), 2.0);
        assertEquals(1, s.getMaxStartIndex());
        s.add(new SimpleTimePeriod(0L, 50L), 3.0);
        assertEquals(1, s.getMaxStartIndex());
    }

    
    public void testGetMinMiddleIndex() {
        TimePeriodValues s = new TimePeriodValues("Test");
        assertEquals(-1, s.getMinMiddleIndex());
        s.add(new SimpleTimePeriod(100L, 200L), 1.0);
        assertEquals(0, s.getMinMiddleIndex());
        s.add(new SimpleTimePeriod(300L, 400L), 2.0);
        assertEquals(0, s.getMinMiddleIndex());
        s.add(new SimpleTimePeriod(0L, 50L), 3.0);
        assertEquals(2, s.getMinMiddleIndex());
    }
    
    
    public void testGetMaxMiddleIndex() {
        TimePeriodValues s = new TimePeriodValues("Test");
        assertEquals(-1, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(100L, 200L), 1.0);
        assertEquals(0, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(300L, 400L), 2.0);
        assertEquals(1, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(0L, 50L), 3.0);
        assertEquals(1, s.getMaxMiddleIndex());
        s.add(new SimpleTimePeriod(150L, 200L), 4.0);
        assertEquals(1, s.getMaxMiddleIndex());
    }

    
    public void getMinEndIndex() {
        TimePeriodValues s = new TimePeriodValues("Test");
        assertEquals(-1, s.getMinEndIndex());
        s.add(new SimpleTimePeriod(100L, 200L), 1.0);
        assertEquals(0, s.getMinEndIndex());
        s.add(new SimpleTimePeriod(300L, 400L), 2.0);
        assertEquals(0, s.getMinEndIndex());
        s.add(new SimpleTimePeriod(0L, 50L), 3.0);
        assertEquals(2, s.getMinEndIndex());
    }
    
    
    public void getMaxEndIndex() {
        TimePeriodValues s = new TimePeriodValues("Test");
        assertEquals(-1, s.getMaxEndIndex());
        s.add(new SimpleTimePeriod(100L, 200L), 1.0);
        assertEquals(0, s.getMaxEndIndex());
        s.add(new SimpleTimePeriod(300L, 400L), 2.0);
        assertEquals(1, s.getMaxEndIndex());
        s.add(new SimpleTimePeriod(0L, 50L), 3.0);
        assertEquals(1, s.getMaxEndIndex());
    }

    
    static class MySeriesChangeListener implements SeriesChangeListener {
        
        SeriesChangeEvent lastEvent;
        
        
        public MySeriesChangeListener() {
            this.lastEvent = null;
        }
        
        
        public SeriesChangeEvent getLastEvent() {
            return this.lastEvent;
        }
        
        
        public void clearLastEvent() {
            this.lastEvent = null;
        }
        
        
        public void seriesChanged(SeriesChangeEvent event) {
            this.lastEvent = event;
        }
    }

}
