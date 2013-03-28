

package org.jfree.chart.axis.junit;

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

import org.jfree.chart.axis.DateTick;
import org.jfree.ui.TextAnchor;


public class DateTickTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DateTickTests.class);
    }

    
    public DateTickTests(String name) {
        super(name);
    }

    
    public void testEquals() {

        Date d1 = new Date(0L);
        Date d2 = new Date(1L);
        String l1 = "Label 1";
        String l2 = "Label 2";
        TextAnchor ta1 = TextAnchor.CENTER;
        TextAnchor ta2 = TextAnchor.BASELINE_LEFT;

        DateTick t1 = new DateTick(d1, l1, ta1, ta1, Math.PI / 2.0);
        DateTick t2 = new DateTick(d1, l1, ta1, ta1, Math.PI / 2.0);
        assertTrue(t1.equals(t2));

        t1 = new DateTick(d2, l1, ta1, ta1, Math.PI / 2.0);
        assertFalse(t1.equals(t2));
        t2 = new DateTick(d2, l1, ta1, ta1, Math.PI / 2.0);
        assertTrue(t1.equals(t2));

        t1 = new DateTick(d1, l2, ta1, ta1, Math.PI / 2.0);
        assertFalse(t1.equals(t2));
        t2 = new DateTick(d1, l2, ta1, ta1, Math.PI / 2.0);
        assertTrue(t1.equals(t2));

        t1 = new DateTick(d1, l1, ta2, ta1, Math.PI / 2.0);
        assertFalse(t1.equals(t2));
        t2 = new DateTick(d1, l1, ta2, ta1, Math.PI / 2.0);
        assertTrue(t1.equals(t2));

        t1 = new DateTick(d1, l1, ta1, ta2, Math.PI / 2.0);
        assertFalse(t1.equals(t2));
        t2 = new DateTick(d1, l1, ta1, ta2, Math.PI / 2.0);
        assertTrue(t1.equals(t2));

        t1 = new DateTick(d1, l1, ta1, ta1, Math.PI / 3.0);
        assertFalse(t1.equals(t2));
        t2 = new DateTick(d1, l1, ta1, ta1, Math.PI / 3.0);
        assertTrue(t1.equals(t2));

    }

    
    public void testHashCode() {
        Date d1 = new Date(0L);
        String l1 = "Label 1";
        TextAnchor ta1 = TextAnchor.CENTER;

        DateTick t1 = new DateTick(d1, l1, ta1, ta1, Math.PI / 2.0);
        DateTick t2 = new DateTick(d1, l1, ta1, ta1, Math.PI / 2.0);
        assertTrue(t1.equals(t2));
        int h1 = t1.hashCode();
        int h2 = t2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        DateTick t1 = new DateTick(new Date(0L), "Label", TextAnchor.CENTER,
        		TextAnchor.CENTER, 10.0);
        DateTick t2 = null;
        try {
            t2 = (DateTick) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {

        DateTick t1 = new DateTick(new Date(0L), "Label", TextAnchor.CENTER,
        		TextAnchor.CENTER, 10.0);
        DateTick t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            t2 = (DateTick) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(t1, t2);

    }

}
