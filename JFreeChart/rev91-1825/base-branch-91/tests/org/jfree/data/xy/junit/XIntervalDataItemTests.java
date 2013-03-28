

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

import org.jfree.data.xy.XIntervalDataItem;


public class XIntervalDataItemTests extends TestCase {
    
    
    public static Test suite() {
        return new TestSuite(XIntervalDataItemTests.class);
    }

    
    public XIntervalDataItemTests(String name) {
        super(name);
    }

    private static final double EPSILON = 0.00000000001;
    
    
    public void testConstructor1() {
        XIntervalDataItem item1 = new XIntervalDataItem(1.0, 2.0, 3.0, 4.0);
        assertEquals(new Double(1.0), item1.getX());
        assertEquals(2.0, item1.getXLowValue(), EPSILON);
        assertEquals(3.0, item1.getXHighValue(), EPSILON);
        assertEquals(4.0, item1.getYValue(), EPSILON);
    }
    
    
    public void testEquals() {
        XIntervalDataItem item1 = new XIntervalDataItem(1.0, 2.0, 3.0, 4.0);
        XIntervalDataItem item2 = new XIntervalDataItem(1.0, 2.0, 3.0, 4.0);
        assertTrue(item1.equals(item2));
        assertTrue(item2.equals(item1));

        
        item1 = new XIntervalDataItem(1.1, 2.0, 3.0, 4.0);
        assertFalse(item1.equals(item2));
        item2 = new XIntervalDataItem(1.1, 2.0, 3.0, 4.0);
        assertTrue(item1.equals(item2));
        
        
        item1 = new XIntervalDataItem(1.1, 2.2, 3.0, 4.0);
        assertFalse(item1.equals(item2));
        item2 = new XIntervalDataItem(1.1, 2.2, 3.0, 4.0);
        assertTrue(item1.equals(item2));

        
        item1 = new XIntervalDataItem(1.1, 2.2, 3.3, 4.0);
        assertFalse(item1.equals(item2));
        item2 = new XIntervalDataItem(1.1, 2.2, 3.3, 4.0);
        assertTrue(item1.equals(item2));

        
        item1 = new XIntervalDataItem(1.1, 2.2, 3.3, 4.4);
        assertFalse(item1.equals(item2));
        item2 = new XIntervalDataItem(1.1, 2.2, 3.3, 4.4);
        assertTrue(item1.equals(item2));

    }

    
    public void testCloning() {
        XIntervalDataItem item1 = new XIntervalDataItem(1.0, 2.0, 3.0, 4.0);
        XIntervalDataItem item2 = null;
        try {
            item2 = (XIntervalDataItem) item1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(item1 != item2);
        assertTrue(item1.getClass() == item2.getClass());
        assertTrue(item1.equals(item2));
    }

    
    public void testSerialization() {
        XIntervalDataItem item1 = new XIntervalDataItem(1.0, 2.0, 3.0, 4.0);
        XIntervalDataItem item2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(item1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            item2 = (XIntervalDataItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(item1, item2);
    }

}
