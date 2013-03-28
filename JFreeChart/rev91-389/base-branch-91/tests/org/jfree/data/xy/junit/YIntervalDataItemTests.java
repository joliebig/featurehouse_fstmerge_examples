

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

import org.jfree.data.xy.YIntervalDataItem;


public class YIntervalDataItemTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(YIntervalDataItemTests.class);
    }

    
    public YIntervalDataItemTests(String name) {
        super(name);
    }

    private static final double EPSILON = 0.00000000001;
    
    
    public void testConstructor1() {
        YIntervalDataItem item1 = new YIntervalDataItem(1.0, 2.0, 3.0, 4.0);
        assertEquals(new Double(1.0), item1.getX());
        assertEquals(2.0, item1.getYValue(), EPSILON);
        assertEquals(3.0, item1.getYLowValue(), EPSILON);
        assertEquals(4.0, item1.getYHighValue(), EPSILON);
    }
    
    
    public void testEquals() {
        YIntervalDataItem item1 = new YIntervalDataItem(1.0, 2.0, 1.5, 2.5);
        YIntervalDataItem item2 = new YIntervalDataItem(1.0, 2.0, 1.5, 2.5);
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));

        
        item1 = new YIntervalDataItem(1.1, 2.0, 1.5, 2.5);
        assertFalse(item1.equals(item2));
        item2 = new YIntervalDataItem(1.1, 2.0, 1.5, 2.5);
        assertTrue(item1.equals(item2));
        
        
        item1 = new YIntervalDataItem(1.1, 2.2, 1.5, 2.5);
        assertFalse(item1.equals(item2));
        item2 = new YIntervalDataItem(1.1, 2.2, 1.5, 2.5);
        assertTrue(item1.equals(item2));

        
        item1 = new YIntervalDataItem(1.1, 2.2, 1.55, 2.5);
        assertFalse(item1.equals(item2));
        item2 = new YIntervalDataItem(1.1, 2.2, 1.55, 2.5);
        assertTrue(item1.equals(item2));

        
        item1 = new YIntervalDataItem(1.1, 2.2, 1.55, 2.55);
        assertFalse(item1.equals(item2));
        item2 = new YIntervalDataItem(1.1, 2.2, 1.55, 2.55);
        assertTrue(item1.equals(item2));
    }

    
    public void testCloning() {
        YIntervalDataItem item1 = new YIntervalDataItem(1.0, 2.0, 1.5, 2.5);
        YIntervalDataItem item2 = null;
        try {
            item2 = (YIntervalDataItem) item1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(item1 != item2);
        assertTrue(item1.getClass() == item2.getClass());
        assertTrue(item1.equals(item2));
    }

    
    public void testSerialization() {
        YIntervalDataItem item1 = new YIntervalDataItem(1.0, 2.0, 1.5, 2.5);
        YIntervalDataItem item2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(item1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            item2 = (YIntervalDataItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(item1, item2);
    }

}
