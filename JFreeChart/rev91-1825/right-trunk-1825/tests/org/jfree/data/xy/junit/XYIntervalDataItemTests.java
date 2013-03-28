

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.xy.XYIntervalDataItem;


public class XYIntervalDataItemTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(XYIntervalDataItemTests.class);
    }

    
    public XYIntervalDataItemTests(String name) {
        super(name);
    }

    private static final double EPSILON = 0.000000001;
    
    public void testConstructor1() {
        XYIntervalDataItem item1 = new XYIntervalDataItem(1.0, 0.5, 1.5, 2.0,
                1.9, 2.1);
        assertEquals(new Double(1.0), item1.getX());
        assertEquals(0.5, item1.getXLowValue(), EPSILON);
        assertEquals(1.5, item1.getXHighValue(), EPSILON);
        assertEquals(2.0, item1.getYValue(), EPSILON);
        assertEquals(1.9, item1.getYLowValue(), EPSILON);
        assertEquals(2.1, item1.getYHighValue(), EPSILON);
    }

    
    public void testEquals() {
        XYIntervalDataItem item1 = new XYIntervalDataItem(1.0, 0.5, 1.5, 2.0,
                1.9, 2.1);
        XYIntervalDataItem item2 = new XYIntervalDataItem(1.0, 0.5, 1.5, 2.0,
                1.9, 2.1);
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));

        
        item1 = new XYIntervalDataItem(1.1, 0.5, 1.5, 2.0, 1.9, 2.1);
        assertFalse(item1.equals(item2));
        item2 = new XYIntervalDataItem(1.1, 0.5, 1.5, 2.0, 1.9, 2.1);
        assertTrue(item1.equals(item2));

        
        item1 = new XYIntervalDataItem(1.1, 0.55, 1.5, 2.0, 1.9, 2.1);
        assertFalse(item1.equals(item2));
        item2 = new XYIntervalDataItem(1.1, 0.55, 1.5, 2.0, 1.9, 2.1);
        assertTrue(item1.equals(item2));

        
        item1 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.0, 1.9, 2.1);
        assertFalse(item1.equals(item2));
        item2 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.0, 1.9, 2.1);
        assertTrue(item1.equals(item2));

        
        item1 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.2, 1.9, 2.1);
        assertFalse(item1.equals(item2));
        item2 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.2, 1.9, 2.1);
        assertTrue(item1.equals(item2));

        
        item1 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.2, 1.99, 2.1);
        assertFalse(item1.equals(item2));
        item2 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.2, 1.99, 2.1);
        assertTrue(item1.equals(item2));

        
        item1 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.2, 1.99, 2.11);
        assertFalse(item1.equals(item2));
        item2 = new XYIntervalDataItem(1.1, 0.55, 1.55, 2.2, 1.99, 2.11);
        assertTrue(item1.equals(item2));
    }

    
    public void testCloning() {
        XYIntervalDataItem item1 = new XYIntervalDataItem(1.0, 0.5, 1.5, 2.0,
                1.9, 2.1);
        XYIntervalDataItem item2 = null;
        try {
            item2 = (XYIntervalDataItem) item1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(item1 != item2);
        assertTrue(item1.getClass() == item2.getClass());
        assertTrue(item1.equals(item2));
    }

    
    public void testSerialization() {
        XYIntervalDataItem item1 = new XYIntervalDataItem(1.0, 0.5, 1.5, 2.0,
                1.9, 2.1);
        XYIntervalDataItem item2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(item1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            item2 = (XYIntervalDataItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(item1, item2);
    }

}
