

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

import org.jfree.data.time.Month;
import org.jfree.data.time.TimePeriodFormatException;
import org.jfree.data.time.Year;
import org.jfree.date.MonthConstants;


public class MonthTests extends TestCase {

    
    private Month jan1900;

    
    private Month feb1900;

    
    private Month nov9999;

    
    private Month dec9999;

    
    public static Test suite() {
        return new TestSuite(MonthTests.class);
    }

    
    public MonthTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        this.jan1900 = new Month(MonthConstants.JANUARY, 1900);
        this.feb1900 = new Month(MonthConstants.FEBRUARY, 1900);
        this.nov9999 = new Month(MonthConstants.NOVEMBER, 9999);
        this.dec9999 = new Month(MonthConstants.DECEMBER, 9999);
    }

    
    public void testEqualsSelf() {
        Month month = new Month();
        assertTrue(month.equals(month));
    }

    
    public void testEquals() {
        Month m1 = new Month(MonthConstants.MAY, 2002);
        Month m2 = new Month(MonthConstants.MAY, 2002);
        assertTrue(m1.equals(m2));
    }

    
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Month m1 = new Month(new Date(951868799999L), zone);
        Month m2 = new Month(new Date(951868800000L), zone);

        assertEquals(MonthConstants.FEBRUARY, m1.getMonth());
        assertEquals(951868799999L, m1.getLastMillisecond(zone));

        assertEquals(MonthConstants.MARCH, m2.getMonth());
        assertEquals(951868800000L, m2.getFirstMillisecond(zone));

    }

    
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Pacific/Auckland");
        Month m1 = new Month(new Date(951821999999L), zone);
        Month m2 = new Month(new Date(951822000000L), zone);

        assertEquals(MonthConstants.FEBRUARY, m1.getMonth());
        assertEquals(951821999999L, m1.getLastMillisecond(zone));

        assertEquals(MonthConstants.MARCH, m2.getMonth());
        assertEquals(951822000000L, m2.getFirstMillisecond(zone));

    }

    
    public void testJan1900Previous() {
        Month previous = (Month) this.jan1900.previous();
        assertNull(previous);
    }

    
    public void testJan1900Next() {
        Month next = (Month) this.jan1900.next();
        assertEquals(this.feb1900, next);
    }

    
    public void testDec9999Previous() {
        Month previous = (Month) this.dec9999.previous();
        assertEquals(this.nov9999, previous);
    }

    
    public void testDec9999Next() {
        Month next = (Month) this.dec9999.next();
        assertNull(next);
    }

    
    public void testParseMonth() {

        Month month = null;

        
        try {
            month = Month.parseMonth("1990-01");
        }
        catch (TimePeriodFormatException e) {
            month = new Month(1, 1900);
        }
        assertEquals(1, month.getMonth());
        assertEquals(1990, month.getYear().getYear());

        
        try {
            month = Month.parseMonth("02-1991");
        }
        catch (TimePeriodFormatException e) {
            month = new Month(1, 1900);
        }
        assertEquals(2, month.getMonth());
        assertEquals(1991, month.getYear().getYear());

        
        try {
            month = Month.parseMonth("March 1993");
        }
        catch (TimePeriodFormatException e) {
            month = new Month(1, 1900);
        }
        assertEquals(3, month.getMonth());
        assertEquals(1993, month.getYear().getYear());

    }

    
    public void testSerialization() {

        Month m1 = new Month(12, 1999);
        Month m2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (Month) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(m1, m2);

    }

    
    public void testHashcode() {
        Month m1 = new Month(2, 2003);
        Month m2 = new Month(2, 2003);
        assertTrue(m1.equals(m2));
        int h1 = m1.hashCode();
        int h2 = m2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testNotCloneable() {
        Month m = new Month(2, 2003);
        assertFalse(m instanceof Cloneable);
    }

    
    public void testGetFirstMillisecond() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Month m = new Month(3, 1970);
        assertEquals(5094000000L, m.getFirstMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }

    
    public void testGetFirstMillisecondWithTimeZone() {
        Month m = new Month(2, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-628444800000L, m.getFirstMillisecond(zone));

        
        boolean pass = false;
        try {
            m.getFirstMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetFirstMillisecondWithCalendar() {
        Month m = new Month(1, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(978307200000L, m.getFirstMillisecond(calendar));

        
        boolean pass = false;
        try {
            m.getFirstMillisecond((Calendar) null);
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
        Month m = new Month(3, 1970);
        assertEquals(7772399999L, m.getLastMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }

    
    public void testGetLastMillisecondWithTimeZone() {
        Month m = new Month(2, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-626025600001L, m.getLastMillisecond(zone));

        
        boolean pass = false;
        try {
            m.getLastMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetLastMillisecondWithCalendar() {
        Month m = new Month(3, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(986083199999L, m.getLastMillisecond(calendar));

        
        boolean pass = false;
        try {
            m.getLastMillisecond((Calendar) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetSerialIndex() {
        Month m = new Month(1, 2000);
        assertEquals(24001L, m.getSerialIndex());
        m = new Month(1, 1900);
        assertEquals(22801L, m.getSerialIndex());
    }

    
    public void testNext() {
        Month m = new Month(12, 2000);
        m = (Month) m.next();
        assertEquals(new Year(2001), m.getYear());
        assertEquals(1, m.getMonth());
        m = new Month(12, 9999);
        assertNull(m.next());
    }

    
    public void testGetStart() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.MARCH, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Month m = new Month(3, 2006);
        assertEquals(cal.getTime(), m.getStart());
        Locale.setDefault(saved);
    }

    
    public void testGetEnd() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.JANUARY, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Month m = new Month(1, 2006);
        assertEquals(cal.getTime(), m.getEnd());
        Locale.setDefault(saved);
    }

}
