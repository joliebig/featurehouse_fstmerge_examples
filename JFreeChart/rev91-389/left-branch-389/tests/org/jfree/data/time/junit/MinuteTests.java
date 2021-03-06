

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

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.date.MonthConstants;


public class MinuteTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(MinuteTests.class);
    }

    
    public MinuteTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        
    }

    
    public void testEqualsSelf() {
        Minute minute = new Minute();
        assertTrue(minute.equals(minute));
    }

    
    public void testEquals() {
        Day day1 = new Day(29, MonthConstants.MARCH, 2002);
        Hour hour1 = new Hour(15, day1);
        Minute minute1 = new Minute(15, hour1);
        Day day2 = new Day(29, MonthConstants.MARCH, 2002);
        Hour hour2 = new Hour(15, day2);
        Minute minute2 = new Minute(15, hour2);
        assertTrue(minute1.equals(minute2));
    }

    
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Minute m1 = new Minute(new Date(1016729699999L), zone);
        Minute m2 = new Minute(new Date(1016729700000L), zone);

        assertEquals(54, m1.getMinute());
        assertEquals(1016729699999L, m1.getLastMillisecond(zone));

        assertEquals(55, m2.getMinute());
        assertEquals(1016729700000L, m2.getFirstMillisecond(zone));

    }

    
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Asia/Singapore");
        Minute m1 = new Minute(new Date(1016700899999L), zone);
        Minute m2 = new Minute(new Date(1016700900000L), zone);

        assertEquals(54, m1.getMinute());
        assertEquals(1016700899999L, m1.getLastMillisecond(zone));

        assertEquals(55, m2.getMinute());
        assertEquals(1016700900000L, m2.getFirstMillisecond(zone));

    }

    
    public void testSerialization() {

        Minute m1 = new Minute();
        Minute m2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (Minute) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(m1, m2);

    }
    
    
    public void testHashcode() {
        Minute m1 = new Minute(45, 5, 1, 2, 2003);
        Minute m2 = new Minute(45, 5, 1, 2, 2003);
        assertTrue(m1.equals(m2));
        int h1 = m1.hashCode();
        int h2 = m2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testNotCloneable() {
        Minute m = new Minute(45, 5, 1, 2, 2003);
        assertFalse(m instanceof Cloneable);
    }

    
    public void testGetFirstMillisecond() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Minute m = new Minute(43, 15, 1, 4, 2006);
        assertEquals(1143902580000L, m.getFirstMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }
    
    
    public void testGetFirstMillisecondWithTimeZone() {
        Minute m = new Minute(59, 15, 1, 4, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-623289660000L, m.getFirstMillisecond(zone));
        
        
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
        Minute m = new Minute(40, 2, 15, 4, 2000);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));        
        assertEquals(955766400000L, m.getFirstMillisecond(calendar));
        
        
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
        Minute m = new Minute(1, 1, 1, 1, 1970);
        assertEquals(119999L, m.getLastMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }
    
    
    public void testGetLastMillisecondWithTimeZone() {
        Minute m = new Minute(1, 2, 7, 7, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-614962680001L, m.getLastMillisecond(zone));
        
        
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
        Minute m = new Minute(45, 21, 21, 4, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(987889559999L, m.getLastMillisecond(calendar));
        
        
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
        Minute m = new Minute(1, 1, 1, 1, 2000);
        assertEquals(52597501L, m.getSerialIndex());
        m = new Minute(1, 1, 1, 1, 1900);
        assertEquals(2941L, m.getSerialIndex());
    }
    
    
    public void testNext() {
        Minute m = new Minute(30, 1, 12, 12, 2000);
        m = (Minute) m.next();
        assertEquals(2000, m.getHour().getYear());
        assertEquals(12, m.getHour().getMonth());
        assertEquals(12, m.getHour().getDayOfMonth());
        assertEquals(1, m.getHour().getHour());
        assertEquals(31, m.getMinute());
        m = new Minute(59, 23, 31, 12, 9999);
        assertNull(m.next());
    }
    
    
    public void testGetStart() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.JANUARY, 16, 3, 47, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Minute m = new Minute(47, 3, 16, 1, 2006);
        assertEquals(cal.getTime(), m.getStart());
        Locale.setDefault(saved);       
        TimeZone.setDefault(savedZone);
    }
    
    
    public void testGetEnd() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.JANUARY, 16, 3, 47, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Minute m = new Minute(47, 3, 16, 1, 2006);
        assertEquals(cal.getTime(), m.getEnd());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }
    
    
    public void test1611872() {
        Minute m1 = new Minute(0, 10, 15, 4, 2000);
        Minute m2 = (Minute) m1.previous();
        assertEquals(m2, new Minute(59, 9, 15, 4, 2000));
    }
 
}
