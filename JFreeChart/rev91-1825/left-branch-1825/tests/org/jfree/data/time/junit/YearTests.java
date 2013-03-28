

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.TimePeriodFormatException;
import org.jfree.data.time.Year;


public class YearTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(YearTests.class);
    }

    
    public YearTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        
    }

    
    public void testEqualsSelf() {
        Year year = new Year();
        assertTrue(year.equals(year));
    }

    
    public void testEquals() {
        Year year1 = new Year(2002);
        Year year2 = new Year(2002);
        assertTrue(year1.equals(year2));

        year1 = new Year(1999);
        assertFalse(year1.equals(year2));
        year2 = new Year(1999);
        assertTrue(year1.equals(year2));
    }

    
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Date d1 = new Date(1009843199999L);
        Date d2 = new Date(1009843200000L);
        Year y1 = new Year(d1, zone);
        Year y2 = new Year(d2, zone);

        assertEquals(2001, y1.getYear());
        assertEquals(1009843199999L, y1.getLastMillisecond(zone));

        assertEquals(2002, y2.getYear());
        assertEquals(1009843200000L, y2.getFirstMillisecond(zone));

    }

    
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        Year y1 = new Year(new Date(1009871999999L), zone);
        Year y2 = new Year(new Date(1009872000000L), zone);

        assertEquals(2001, y1.getYear());
        assertEquals(1009871999999L, y1.getLastMillisecond(zone));

        assertEquals(2002, y2.getYear());
        assertEquals(1009872000000L, y2.getFirstMillisecond(zone));

    }

    
    public void testMinuss9999Previous() {
        Year current = new Year(-9999);
        Year previous = (Year) current.previous();
        assertNull(previous);
    }

    
    public void test1900Next() {
        Year current = new Year(1900);
        Year next = (Year) current.next();
        assertEquals(1901, next.getYear());
    }

    
    public void test9999Previous() {
        Year current = new Year(9999);
        Year previous = (Year) current.previous();
        assertEquals(9998, previous.getYear());
    }

    
    public void test9999Next() {
        Year current = new Year(9999);
        Year next = (Year) current.next();
        assertNull(next);
    }

    
    public void testParseYear() {

        Year year = null;

        
        try {
            year = Year.parseYear("2000");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        assertEquals(2000, year.getYear());

        
        try {
            year = Year.parseYear(" 2001 ");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        assertEquals(2001, year.getYear());

        
        try {
            year = Year.parseYear("99");
        }
        catch (TimePeriodFormatException e) {
            year = new Year(1900);
        }
        assertEquals(99, year.getYear());

    }

    
    public void testSerialization() {

        Year y1 = new Year(1999);
        Year y2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(y1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            y2 = (Year) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(y1, y2);

    }

    
    public void testNotCloneable() {
        Year y = new Year(1999);
        assertFalse(y instanceof Cloneable);
    }

    
    public void testHashcode() {
        Year y1 = new Year(1988);
        Year y2 = new Year(1988);
        assertTrue(y1.equals(y2));
        int h1 = y1.hashCode();
        int h2 = y2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testGetFirstMillisecond() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Year y = new Year(1970);
        
        assertEquals(-3600000L, y.getFirstMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }

    
    public void testGetFirstMillisecondWithTimeZone() {
        Year y = new Year(1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-631123200000L, y.getFirstMillisecond(zone));

        
        boolean pass = false;
        try {
            y.getFirstMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetFirstMillisecondWithCalendar() {
        Year y = new Year(2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(978307200000L, y.getFirstMillisecond(calendar));

        
        boolean pass = false;
        try {
            y.getFirstMillisecond((Calendar) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetLastMillisecond() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Year y = new Year(1970);
        
        assertEquals(31532399999L, y.getLastMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }

    
    public void testGetLastMillisecondWithTimeZone() {
        Year y = new Year(1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-599587200001L, y.getLastMillisecond(zone));

        
        boolean pass = false;
        try {
            y.getLastMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetLastMillisecondWithCalendar() {
        Year y = new Year(2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(1009843199999L, y.getLastMillisecond(calendar));

        
        boolean pass = false;
        try {
            y.getLastMillisecond((Calendar) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetSerialIndex() {
        Year y = new Year(2000);
        assertEquals(2000L, y.getSerialIndex());
    }

    
    public void testNext() {
        Year y = new Year(2000);
        y = (Year) y.next();
        assertEquals(2001, y.getYear());
        y = new Year(9999);
        assertNull(y.next());
    }

    
    public void testGetStart() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Year y = new Year(2006);
        assertEquals(cal.getTime(), y.getStart());
        Locale.setDefault(saved);
    }

    
    public void testGetEnd() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.DECEMBER, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Year y = new Year(2006);
        assertEquals(cal.getTime(), y.getEnd());
        Locale.setDefault(saved);
    }

}
