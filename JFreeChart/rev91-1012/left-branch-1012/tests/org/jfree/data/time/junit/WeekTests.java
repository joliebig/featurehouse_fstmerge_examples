

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

import org.jfree.data.time.Week;
import org.jfree.data.time.Year;


public class WeekTests extends TestCase {

    
    private Week w1Y1900;

    
    private Week w2Y1900;

    
    private Week w51Y9999;

    
    private Week w52Y9999;

    
    public static Test suite() {
        return new TestSuite(WeekTests.class);
    }

    
    public WeekTests(String name) {
        super(name);
    }

    
    protected void setUp() {
        this.w1Y1900 = new Week(1, 1900);
        this.w2Y1900 = new Week(2, 1900);
        this.w51Y9999 = new Week(51, 9999);
        this.w52Y9999 = new Week(52, 9999);
    }

    
    public void testEquals() {
        Week w1 = new Week(1, 2002);
        Week w2 = new Week(1, 2002);
        assertTrue(w1.equals(w2));
        assertTrue(w2.equals(w1));
        
        w1 = new Week(2, 2002);
        assertFalse(w1.equals(w2));
        w2 = new Week(2, 2002);
        assertTrue(w1.equals(w2));
        
        w1 = new Week(2, 2003);
        assertFalse(w1.equals(w2));
        w2 = new Week(2, 2003);
        assertTrue(w1.equals(w2));
    }

    
    public void testW1Y1900Previous() {
        Week previous = (Week) this.w1Y1900.previous();
        assertNull(previous);
    }

    
    public void testW1Y1900Next() {
        Week next = (Week) this.w1Y1900.next();
        assertEquals(this.w2Y1900, next);
    }

    
    public void testW52Y9999Previous() {
        Week previous = (Week) this.w52Y9999.previous();
        assertEquals(this.w51Y9999, previous);
    }

    
    public void testW52Y9999Next() {
        Week next = (Week) this.w52Y9999.next();
        assertNull(next);
    }

    
    public void testSerialization() {

        Week w1 = new Week(24, 1999);
        Week w2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(w1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            w2 = (Week) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(w1, w2);

    }
    
    
    public void testHashcode() {
        Week w1 = new Week(2, 2003);
        Week w2 = new Week(2, 2003);
        assertTrue(w1.equals(w2));
        int h1 = w1.hashCode();
        int h2 = w2.hashCode();
        assertEquals(h1, h2);
    }
    
    
    public void testNotCloneable() {
        Week w = new Week(1, 1999);
        assertFalse(w instanceof Cloneable);
    }
    
    
    public void testWeek12005() {
        Week w1 = new Week(1, 2005);
        Calendar c1 = Calendar.getInstance(
                TimeZone.getTimeZone("Europe/London"), Locale.UK);
        c1.setMinimalDaysInFirstWeek(4);  
        assertEquals(1104710400000L, w1.getFirstMillisecond(c1));
        assertEquals(1105315199999L, w1.getLastMillisecond(c1));
        Calendar c2 = Calendar.getInstance(
                TimeZone.getTimeZone("Europe/Paris"), Locale.FRANCE);
        c2.setMinimalDaysInFirstWeek(4);  
        assertEquals(1104706800000L, w1.getFirstMillisecond(c2));
        assertEquals(1105311599999L, w1.getLastMillisecond(c2));
        Calendar c3 = Calendar.getInstance(
                TimeZone.getTimeZone("America/New_York"), Locale.US);
        assertEquals(1104037200000L, w1.getFirstMillisecond(c3));
        assertEquals(1104641999999L, w1.getLastMillisecond(c3));   
    }

    
    public void testWeek532005() {
        Week w1 = new Week(53, 2004);
        Calendar c1 = Calendar.getInstance(
                TimeZone.getTimeZone("Europe/London"), Locale.UK);
        c1.setMinimalDaysInFirstWeek(4);  
        assertEquals(1104105600000L, w1.getFirstMillisecond(c1));
        assertEquals(1104710399999L, w1.getLastMillisecond(c1));
        Calendar c2 = Calendar.getInstance(
                TimeZone.getTimeZone("Europe/Paris"), Locale.FRANCE);
        c2.setMinimalDaysInFirstWeek(4);  
        assertEquals(1104102000000L, w1.getFirstMillisecond(c2));
        assertEquals(1104706799999L, w1.getLastMillisecond(c2));
        w1 = new Week(53, 2005);
        Calendar c3 = Calendar.getInstance(
                TimeZone.getTimeZone("America/New_York"), Locale.US);
        assertEquals(1135486800000L, w1.getFirstMillisecond(c3));
        assertEquals(1136091599999L, w1.getLastMillisecond(c3));   
    }

    
    public void testBug1448828() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        try {
            Week w = new Week(new Date(1136109830000l), 
                    TimeZone.getTimeZone("GMT"));
            assertEquals(2005, w.getYearValue());
            assertEquals(52, w.getWeek());
        }
        finally {
            Locale.setDefault(saved);
        }
    }
    
    
    public void testBug1498805() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        try {
            TimeZone zone = TimeZone.getTimeZone("GMT");
            GregorianCalendar gc = new GregorianCalendar(zone);
            gc.set(2005, Calendar.JANUARY, 1, 12, 0, 0);
            Week w = new Week(gc.getTime(), zone);
            assertEquals(53, w.getWeek());
            assertEquals(new Year(2004), w.getYear());
        }
        finally {
            Locale.setDefault(saved);
        }
    }
    
    
    public void testGetFirstMillisecond() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.UK);
        TimeZone savedZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        Week w = new Week(3, 1970);
        assertEquals(946800000L, w.getFirstMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }
    
    
    public void testGetFirstMillisecondWithTimeZone() {
        Week w = new Week(47, 1950);
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.US);
        try {
            TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
            assertEquals(-603302400000L, w.getFirstMillisecond(zone));
        }
        finally {
            Locale.setDefault(saved);
        }
        
        
        boolean pass = false;
        try {
            w.getFirstMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }        
        assertTrue(pass); 
    }
    
    
    public void testGetFirstMillisecondWithCalendar() {
        Week w = new Week(1, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(978307200000L, w.getFirstMillisecond(calendar));
        
        
        boolean pass = false;
        try {
            w.getFirstMillisecond((Calendar) null);
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
        Week w = new Week(31, 1970);
        assertEquals(18485999999L, w.getLastMillisecond());
        Locale.setDefault(saved);
        TimeZone.setDefault(savedZone);
    }
    
    
    public void testGetLastMillisecondWithTimeZone() {
        Week w = new Week(2, 1950);
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.US);
        try {
            TimeZone zone = TimeZone.getTimeZone("America/Los_Angeles");
            assertEquals(-629913600001L, w.getLastMillisecond(zone));
        }
        finally {
            Locale.setDefault(saved);
        }

        
        boolean pass = false;
        try {
            w.getLastMillisecond((TimeZone) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);       
    }
    
    
    public void testGetLastMillisecondWithCalendar() {
        Week w = new Week(52, 2001);
        GregorianCalendar calendar = new GregorianCalendar(Locale.GERMANY);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Frankfurt"));
        assertEquals(1009756799999L, w.getLastMillisecond(calendar));
        
        
        boolean pass = false;
        try {
            w.getLastMillisecond((Calendar) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);
    } 
    
    
    public void testGetSerialIndex() {
        Week w = new Week(1, 2000);
        assertEquals(106001L, w.getSerialIndex());
        w = new Week(1, 1900);
        assertEquals(100701L, w.getSerialIndex());
    }
    
    
    public void testNext() {
        Week w = new Week(12, 2000);
        w = (Week) w.next();
        assertEquals(new Year(2000), w.getYear());
        assertEquals(13, w.getWeek());
        w = new Week(53, 9999);
        assertNull(w.next());
    }
    
    
    public void testGetStart() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.JANUARY, 16, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Week w = new Week(3, 2006);
        assertEquals(cal.getTime(), w.getStart());
        Locale.setDefault(saved);        
    }
    
    
    public void testGetEnd() {
        Locale saved = Locale.getDefault();
        Locale.setDefault(Locale.ITALY);
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.set(2006, Calendar.JANUARY, 8, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Week w = new Week(1, 2006);
        assertEquals(cal.getTime(), w.getEnd());
        Locale.setDefault(saved);                
    }
    
    
    public void testConstructor() {
        Locale savedLocale = Locale.getDefault();
        TimeZone savedZone = TimeZone.getDefault();
        Locale.setDefault(new Locale("da", "DK"));
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Copenhagen"));
        GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance(
                TimeZone.getDefault(), Locale.getDefault());
        
        
        assertEquals(Calendar.MONDAY, cal.getFirstDayOfWeek());
        cal.set(2007, Calendar.AUGUST, 26, 1, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date t = cal.getTime();
        Week w = new Week(t, TimeZone.getTimeZone("Europe/Copenhagen"));
        assertEquals(34, w.getWeek());

        Locale.setDefault(Locale.US);
        TimeZone.setDefault(TimeZone.getTimeZone("US/Detroit"));
        cal = (GregorianCalendar) Calendar.getInstance(TimeZone.getDefault());
        
        assertEquals(Calendar.SUNDAY, cal.getFirstDayOfWeek());
        cal.set(2007, Calendar.AUGUST, 26, 1, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        t = cal.getTime();
        w = new Week(t, TimeZone.getTimeZone("Europe/Copenhagen"));
        assertEquals(35, w.getWeek());
        w = new Week(t, TimeZone.getTimeZone("Europe/Copenhagen"), 
                new Locale("da", "DK"));
        assertEquals(34, w.getWeek());

        Locale.setDefault(savedLocale);
        TimeZone.setDefault(savedZone);
    }
    
}
