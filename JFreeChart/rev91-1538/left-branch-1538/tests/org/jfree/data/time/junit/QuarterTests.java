

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

import org.jfree.data.time.Quarter;
import org.jfree.data.time.TimePeriodFormatException;
import org.jfree.data.time.Year;


public class QuarterTests extends TestCase {

    
    private Quarter q1Y1900;

    
    private Quarter q2Y1900;

    
    private Quarter q3Y9999;

    
    private Quarter q4Y9999;

    
    public static Test suite() {
        return new TestSuite(QuarterTests.class);
    }

    
    public QuarterTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        this.q1Y1900 = new Quarter(1, 1900);
        this.q2Y1900 = new Quarter(2, 1900);
        this.q3Y9999 = new Quarter(3, 9999);
        this.q4Y9999 = new Quarter(4, 9999);
    }

    
    public void testEqualsSelf() {
        Quarter quarter = new Quarter();
        assertTrue(quarter.equals(quarter));
    }

    
    public void testEquals() {
        Quarter q1 = new Quarter(2, 2002);
        Quarter q2 = new Quarter(2, 2002);
        assertTrue(q1.equals(q2));
    }

    
    public void testDateConstructor1() {

        TimeZone zone = TimeZone.getTimeZone("GMT");
        Quarter q1 = new Quarter(new Date(1017619199999L), zone);
        Quarter q2 = new Quarter(new Date(1017619200000L), zone);

        assertEquals(1, q1.getQuarter());
        assertEquals(1017619199999L, q1.getLastMillisecond(zone));

        assertEquals(2, q2.getQuarter());
        assertEquals(1017619200000L, q2.getFirstMillisecond(zone));

    }

    
    public void testDateConstructor2() {

        TimeZone zone = TimeZone.getTimeZone("Europe/Istanbul");
        Quarter q1 = new Quarter(new Date(1017608399999L), zone);
        Quarter q2 = new Quarter(new Date(1017608400000L), zone);

        assertEquals(1, q1.getQuarter());
        assertEquals(1017608399999L, q1.getLastMillisecond(zone));

        assertEquals(2, q2.getQuarter());
        assertEquals(1017608400000L, q2.getFirstMillisecond(zone));

    }

    
    public void testQ1Y1900Previous() {
        Quarter previous = (Quarter) this.q1Y1900.previous();
        assertNull(previous);
    }

    
    public void testQ1Y1900Next() {
        Quarter next = (Quarter) this.q1Y1900.next();
        assertEquals(this.q2Y1900, next);
    }

    
    public void testQ4Y9999Previous() {
        Quarter previous = (Quarter) this.q4Y9999.previous();
        assertEquals(this.q3Y9999, previous);
    }

    
    public void testQ4Y9999Next() {
        Quarter next = (Quarter) this.q4Y9999.next();
        assertNull(next);
    }

    
    public void testParseQuarter() {

        Quarter quarter = null;

        
        try {
            quarter = Quarter.parseQuarter("Q1-2000");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        assertEquals(1, quarter.getQuarter());
        assertEquals(2000, quarter.getYear().getYear());

        
        try {
            quarter = Quarter.parseQuarter("2001-Q2");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        assertEquals(2, quarter.getQuarter());
        assertEquals(2001, quarter.getYear().getYear());

        
        try {
            quarter = Quarter.parseQuarter("Q3, 2002");
        }
        catch (TimePeriodFormatException e) {
            quarter = new Quarter(1, 1900);
        }
        assertEquals(3, quarter.getQuarter());
        assertEquals(2002, quarter.getYear().getYear());

    }

    
    public void testSerialization() {

        Quarter q1 = new Quarter(4, 1999);
        Quarter q2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(q1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            q2 = (Quarter) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(q1, q2);

    }

    
    public void testHashcode() {
        Quarter q1 = new Quarter(2, 2003);
        Quarter q2 = new Quarter(2, 2003);
        assertTrue(q1.equals(q2));
        int h1 = q1.hashCode();
        int h2 = q2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testNotCloneable() {
        Quarter q = new Quarter(2, 2003);
        assertFalse(q instanceof Cloneable);
    }

    
    public void testConstructor() {
        boolean pass = false;
        try {
             new Quarter(0, 2005);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);

        pass = false;
        try {
             new Quarter(5, 2005);
        }
        catch (IllegalArgumentException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetFirstMillisecond() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Quarter q = new Quarter(3, 1970);
        assertEquals(15634800000L, q.getFirstMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }

    
    public void testGetFirstMillisecondWithTimeZone() {
        Quarter q = new Quarter(2, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-623347200000L, q.getFirstMillisecond(zone));

        
        boolean pass = false;
        try {
            q.getFirstMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetFirstMillisecondWithCalendar() {
        Quarter q = new Quarter(1, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(978307200000L, q.getFirstMillisecond(calendar));

        
        boolean pass = false;
        try {
            q.getFirstMillisecond((Calendar) null);
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
        Quarter q = new Quarter(3, 1970);
        assertEquals(23583599999L, q.getLastMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }

    
    public void testGetLastMillisecondWithTimeZone() {
        Quarter q = new Quarter(2, 1950);
        TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
        assertEquals(-615488400001L, q.getLastMillisecond(zone));

        
        boolean pass = false;
        try {
            q.getLastMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetLastMillisecondWithCalendar() {
        Quarter q = new Quarter(3, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(1001894399999L, q.getLastMillisecond(calendar));

        
        boolean pass = false;
        try {
            q.getLastMillisecond((Calendar) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void testGetSerialIndex() {
        Quarter q = new Quarter(1, 2000);
        assertEquals(8001L, q.getSerialIndex());
        q = new Quarter(1, 1900);
        assertEquals(7601L, q.getSerialIndex());
    }

    
    public void testNext() {
        Quarter q = new Quarter(1, 2000);
        q = (Quarter) q.next();
        assertEquals(new Year(2000), q.getYear());
        assertEquals(2, q.getQuarter());
        q = new Quarter(4, 9999);
        assertNull(q.next());
    }

    
    public void testGetStart() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.JULY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Quarter q = new Quarter(3, 2006);
        assertEquals(cal.getTime(), q.getStart());
        Locale.setDefault(saved);
    }

    
    public void testGetEnd() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.MARCH, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Quarter q = new Quarter(1, 2006);
        assertEquals(cal.getTime(), q.getEnd());
        Locale.setDefault(saved);
    }
}
