

package org.jfree.chart.axis.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.Second;
import org.jfree.data.time.Year;


public class PeriodAxisTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(PeriodAxisTests.class);
    }

    
    public PeriodAxisTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        PeriodAxis a1 = new PeriodAxis("Test");
        PeriodAxis a2 = new PeriodAxis("Test");
        assertTrue(a1.equals(a2));
        assertTrue(a2.equals(a1));

        a1.setFirst(new Year(2000));
        assertFalse(a1.equals(a2));
        a2.setFirst(new Year(2000));
        assertTrue(a1.equals(a2));

        a1.setLast(new Year(2004));
        assertFalse(a1.equals(a2));
        a2.setLast(new Year(2004));
        assertTrue(a1.equals(a2));

        a1.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        assertFalse(a1.equals(a2));
        a2.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        assertTrue(a1.equals(a2));

        a1.setAutoRangeTimePeriodClass(Quarter.class);
        assertFalse(a1.equals(a2));
        a2.setAutoRangeTimePeriodClass(Quarter.class);
        assertTrue(a1.equals(a2));

        PeriodAxisLabelInfo info[] = new PeriodAxisLabelInfo[1];
        info[0] = new PeriodAxisLabelInfo(
            Month.class, new SimpleDateFormat("MMM")
        );

        a1.setLabelInfo(info);
        assertFalse(a1.equals(a2));
        a2.setLabelInfo(info);
        assertTrue(a1.equals(a2));

        a1.setMajorTickTimePeriodClass(Minute.class);
        assertFalse(a1.equals(a2));
        a2.setMajorTickTimePeriodClass(Minute.class);
        assertTrue(a1.equals(a2));

        a1.setMinorTickMarksVisible(!a1.isMinorTickMarksVisible());
        assertFalse(a1.equals(a2));
        a2.setMinorTickMarksVisible(a1.isMinorTickMarksVisible());
        assertTrue(a1.equals(a2));

        a1.setMinorTickTimePeriodClass(Minute.class);
        assertFalse(a1.equals(a2));
        a2.setMinorTickTimePeriodClass(Minute.class);
        assertTrue(a1.equals(a2));

        Stroke s = new BasicStroke(1.23f);
        a1.setMinorTickMarkStroke(s);
        assertFalse(a1.equals(a2));
        a2.setMinorTickMarkStroke(s);
        assertTrue(a1.equals(a2));

        a1.setMinorTickMarkPaint(Color.blue);
        assertFalse(a1.equals(a2));
        a2.setMinorTickMarkPaint(Color.blue);
        assertTrue(a1.equals(a2));

    }

    
    public void testHashCode() {
        PeriodAxis a1 = new PeriodAxis("Test");
        PeriodAxis a2 = new PeriodAxis("Test");
        assertTrue(a1.equals(a2));
        int h1 = a1.hashCode();
        int h2 = a2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        PeriodAxis a1 = new PeriodAxis("Test");
        PeriodAxis a2 = null;
        try {
            a2 = (PeriodAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));

        
        a1.setLabel("New Label");
        assertFalse(a1.equals(a2));
        a2.setLabel("New Label");
        assertTrue(a1.equals(a2));

        a1.setFirst(new Year(1920));
        assertFalse(a1.equals(a2));
        a2.setFirst(new Year(1920));
        assertTrue(a1.equals(a2));

        a1.setLast(new Year(2020));
        assertFalse(a1.equals(a2));
        a2.setLast(new Year(2020));
        assertTrue(a1.equals(a2));

        PeriodAxisLabelInfo[] info = new PeriodAxisLabelInfo[2];
        info[0] = new PeriodAxisLabelInfo(Day.class, new SimpleDateFormat("d"));
        info[1] = new PeriodAxisLabelInfo(Year.class,
                new SimpleDateFormat("yyyy"));
        a1.setLabelInfo(info);
        assertFalse(a1.equals(a2));
        a2.setLabelInfo(info);
        assertTrue(a1.equals(a2));

        a1.setAutoRangeTimePeriodClass(Second.class);
        assertFalse(a1.equals(a2));
        a2.setAutoRangeTimePeriodClass(Second.class);
        assertTrue(a1.equals(a2));

        a1.setTimeZone(new SimpleTimeZone(123, "Bogus"));
        assertFalse(a1.equals(a2));
        a2.setTimeZone(new SimpleTimeZone(123, "Bogus"));
        assertTrue(a1.equals(a2));

    }

    
    public void testSerialization() {
        PeriodAxis a1 = new PeriodAxis("Test Axis");
        PeriodAxis a2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (PeriodAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        boolean b = a1.equals(a2);
        assertTrue(b);
    }

}
