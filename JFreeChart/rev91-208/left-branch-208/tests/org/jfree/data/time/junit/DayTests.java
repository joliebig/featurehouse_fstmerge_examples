

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Day;
import org.jfree.date.MonthConstants;


public class DayTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DayTests.class);
    }

    
    public DayTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        
    }

    
    public void testEqualsSelf() {
        Day day = new Day();
        assertTrue(day.equals(day));
    }

    
    public void testEquals() {
        Day day1 = new Day(29, MonthConstants.MARCH, 2002);
        Day day2 = new Day(29, MonthConstants.MARCH, 2002);
        assertTrue(day1.equals(day2));
    }

    
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Day d1 = new Day(new Date(1078099199999L), zone);
        Day d2 = new Day(new Date(1078099200000L), zone);

        assertEquals(MonthConstants.FEBRUARY, d1.getMonth());
        assertEquals(1078099199999L, d1.getLastMillisecond(zone));

        assertEquals(MonthConstants.MARCH, d2.getMonth());
        assertEquals(1078099200000L, d2.getFirstMillisecond(zone));

    }

    
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Europe/Helsinki");
        Day d1 = new Day(new Date(1078091999999L), zone);
        Day d2 = new Day(new Date(1078092000000L), zone);

        assertEquals(MonthConstants.FEBRUARY, d1.getMonth());
        assertEquals(1078091999999L, d1.getLastMillisecond(zone));

        assertEquals(MonthConstants.MARCH, d2.getMonth());
        assertEquals(1078092000000L, d2.getFirstMillisecond(zone));

    }

    
    public void test1Jan1900Previous() {

        Day jan1st1900 = new Day(1, MonthConstants.JANUARY, 1900);
        Day previous = (Day) jan1st1900.previous();
        assertNull(previous);

    }

    
    public void test1Jan1900Next() {

        Day jan1st1900 = new Day(1, MonthConstants.JANUARY, 1900);
        Day next = (Day) jan1st1900.next();
        assertEquals(2, next.getDayOfMonth());

    }

    
    public void test31Dec9999Previous() {

        Day dec31st9999 = new Day(31, MonthConstants.DECEMBER, 9999);
        Day previous = (Day) dec31st9999.previous();
        assertEquals(30, previous.getDayOfMonth());

    }

    
    public void test31Dec9999Next() {

        Day dec31st9999 = new Day(31, MonthConstants.DECEMBER, 9999);
        Day next = (Day) dec31st9999.next();
        assertNull(next);

    }

    
    public void testParseDay() throws ParseException {

        GregorianCalendar gc = new GregorianCalendar(2001, 12, 31);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date reference = format.parse("31/12/2001");
        if (reference.equals(gc.getTime())) {
            
            Day d = Day.parseDay("31/12/2001");
            assertEquals(37256, d.getSerialDate().toSerial());
        }

        
        Day d = Day.parseDay("2001-12-31");
        assertEquals(37256, d.getSerialDate().toSerial());

    }

    
    public void testSerialization() {

        Day d1 = new Day(15, 4, 2000);
        Day d2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (Day) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);

    }
    
    
    public void testHashcode() {
        Day d1 = new Day(1, 2, 2003);
        Day d2 = new Day(1, 2, 2003);
        assertTrue(d1.equals(d2));
        int h1 = d1.hashCode();
        int h2 = d2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testNotCloneable() {
        Day d = new Day(1, 2, 2003);
        assertFalse(d instanceof Cloneable);
    }
    
    
    public void testGetSerialIndex() {
        Day d = new Day(1, 1, 1900);
        assertEquals(2, d.getSerialIndex());
        d = new Day(15, 4, 2000);
        assertEquals(36631, d.getSerialIndex());
    }
    
    
    public void testGetFirstMillisecond() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Day d = new Day(1, 3, 1970);
        assertEquals(5094000000L, d.getFirstMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }
    
    
    public void testGetFirstMillisecondWithTimeZone() {
        Day d = new Day(26, 4, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-621187200000L, d.getFirstMillisecond(zone));
        
        
        boolean pass = false;
        try {
            d.getFirstMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);            
    }
    
    
    public void testGetFirstMillisecondWithCalendar() {
        Day d = new Day(1, 12, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(1007164800000L, d.getFirstMillisecond(calendar));
        
        
        boolean pass = false;
        try {
            d.getFirstMillisecond((Calendar) null);
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
        Day d = new Day(1, 1, 1970);
        assertEquals(82799999L, d.getLastMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }
    
    
    public void testGetLastMillisecondWithTimeZone() {
        Day d = new Day(1, 2, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-628358400001L, d.getLastMillisecond(zone));
        
        
        boolean pass = false;
        try {
            d.getLastMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);            
    }
    
    
    public void testGetLastMillisecondWithCalendar() {

        Day d = new Day(4, 5, 2001);
        Calendar calendar = Calendar.getInstance(
                TimeZone.getTimeZone("Europe/London"), Locale.UK);
        assertEquals(989017199999L, d.getLastMillisecond(calendar));
        
        
        boolean pass = false;
        try {
            d.getLastMillisecond((Calendar) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    } 
    
    
    public void testNext() {
        Day d = new Day(25, 12, 2000);
        d = (Day) d.next();
        assertEquals(2000, d.getYear());
        assertEquals(12, d.getMonth());
        assertEquals(26, d.getDayOfMonth());
        d = new Day(31, 12, 9999);
        assertNull(d.next());
    }
    
    
    public void testGetStart() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.NOVEMBER, 3, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Day d = new Day(3, 11, 2006);
        assertEquals(cal.getTime(), d.getStart());
        Locale.setDefault(saved);        
    }
    
    
    public void testGetEnd() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(1900, Calendar.JANUARY, 1, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Day d = new Day(1, 1, 1900);
        assertEquals(cal.getTime(), d.getEnd());
        Locale.setDefault(saved);                
    }

}
