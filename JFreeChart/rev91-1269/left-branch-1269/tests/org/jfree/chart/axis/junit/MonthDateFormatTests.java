

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.MonthDateFormat;


public class MonthDateFormatTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(MonthDateFormatTests.class);
    }

    
    public MonthDateFormatTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        MonthDateFormat mf1 = new MonthDateFormat();
        MonthDateFormat mf2 = new MonthDateFormat();
        assertTrue(mf1.equals(mf2));
        assertTrue(mf2.equals(mf1));

        boolean[] showYear1 = new boolean [12];
        showYear1[0] = true;
        boolean[] showYear2 = new boolean [12];
        showYear1[1] = true;

        
        mf1 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.US, 1,
            showYear1, new SimpleDateFormat("yy"));
        assertFalse(mf1.equals(mf2));
        mf2 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.US, 1,
            showYear1, new SimpleDateFormat("yy"));
        assertTrue(mf1.equals(mf2));

        
        mf1 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 1,
            showYear1, new SimpleDateFormat("yy"));
        assertFalse(mf1.equals(mf2));
        mf2 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 1,
            showYear1, new SimpleDateFormat("yy"));
        assertTrue(mf1.equals(mf2));

        
        mf1 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 2,
            showYear1, new SimpleDateFormat("yy"));
        assertFalse(mf1.equals(mf2));
        mf2 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 2,
            showYear1, new SimpleDateFormat("yy"));
        assertTrue(mf1.equals(mf2));

        
        mf1 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 2,
            showYear2, new SimpleDateFormat("yy"));
        assertFalse(mf1.equals(mf2));
        mf2 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 2,
            showYear2, new SimpleDateFormat("yy"));
        assertTrue(mf1.equals(mf2));

        
        mf1 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 2,
            showYear2, new SimpleDateFormat("yyyy"));
        assertFalse(mf1.equals(mf2));
        mf2 = new MonthDateFormat(TimeZone.getTimeZone("PST"), Locale.FRANCE, 2,
            showYear2, new SimpleDateFormat("yyyy"));
        assertTrue(mf1.equals(mf2));

    }

    
    public void testHashCode() {
        MonthDateFormat mf1 = new MonthDateFormat();
        MonthDateFormat mf2 = new MonthDateFormat();
        assertTrue(mf1.equals(mf2));
        int h1 = mf1.hashCode();
        int h2 = mf2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        MonthDateFormat mf1 = new MonthDateFormat();
        MonthDateFormat mf2 = null;
        mf2 = (MonthDateFormat) mf1.clone();
        assertTrue(mf1 != mf2);
        assertTrue(mf1.getClass() == mf2.getClass());
        assertTrue(mf1.equals(mf2));
    }

    
    public void testSerialization() {
        MonthDateFormat mf1 = new MonthDateFormat();
        MonthDateFormat mf2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(mf1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            mf2 = (MonthDateFormat) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertTrue(mf1.equals(mf2));
    }

}
