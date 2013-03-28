

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.QuarterDateFormat;


public class QuarterDateFormatTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(QuarterDateFormatTests.class);
    }

    
    public QuarterDateFormatTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        QuarterDateFormat qf1 = new QuarterDateFormat(TimeZone.getTimeZone(
                "GMT"), new String[] {"1", "2", "3", "4"});
        QuarterDateFormat qf2 = new QuarterDateFormat(TimeZone.getTimeZone(
                "GMT"), new String[] {"1", "2", "3", "4"});
        assertTrue(qf1.equals(qf2));
        assertTrue(qf2.equals(qf1));
        
        qf1 = new QuarterDateFormat(TimeZone.getTimeZone("PST"), 
                new String[] {"1", "2", "3", "4"});
        assertFalse(qf1.equals(qf2));
        qf2 = new QuarterDateFormat(TimeZone.getTimeZone("PST"), 
                new String[] {"1", "2", "3", "4"});
        assertTrue(qf1.equals(qf2));
        
        qf1 = new QuarterDateFormat(TimeZone.getTimeZone("PST"), 
                new String[] {"A", "2", "3", "4"});
        assertFalse(qf1.equals(qf2));
        qf2 = new QuarterDateFormat(TimeZone.getTimeZone("PST"), 
                new String[] {"A", "2", "3", "4"});
        assertTrue(qf1.equals(qf2));

        qf1 = new QuarterDateFormat(TimeZone.getTimeZone("PST"), 
                new String[] {"A", "2", "3", "4"}, true);
        assertFalse(qf1.equals(qf2));
        qf2 = new QuarterDateFormat(TimeZone.getTimeZone("PST"), 
                new String[] {"A", "2", "3", "4"}, true);
        assertTrue(qf1.equals(qf2));
    }
   
    
    public void testHashCode() {
        QuarterDateFormat qf1 = new QuarterDateFormat(TimeZone.getTimeZone(
                "GMT"), new String[] {"1", "2", "3", "4"});
        QuarterDateFormat qf2 = new QuarterDateFormat(TimeZone.getTimeZone(
                "GMT"), new String[] {"1", "2", "3", "4"});
        assertTrue(qf1.equals(qf2));
        int h1 = qf1.hashCode();
        int h2 = qf2.hashCode();
        assertEquals(h1, h2);
    }    
    
    
    public void testCloning() {
        QuarterDateFormat qf1 = new QuarterDateFormat(TimeZone.getTimeZone(
                "GMT"), new String[] {"1", "2", "3", "4"});
        QuarterDateFormat qf2 = null;
        qf2 = (QuarterDateFormat) qf1.clone();
        assertTrue(qf1 != qf2);
        assertTrue(qf1.getClass() == qf2.getClass());
        assertTrue(qf1.equals(qf2));
    }

    
    public void testSerialization() {
        QuarterDateFormat qf1 = new QuarterDateFormat(TimeZone.getTimeZone(
                "GMT"), new String[] {"1", "2", "3", "4"});
        QuarterDateFormat qf2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(qf1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            qf2 = (QuarterDateFormat) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertTrue(qf1.equals(qf2));
    }

}
