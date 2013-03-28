

package org.jfree.experimental.chart.plot.dial.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.experimental.chart.plot.dial.AbstractDialLayer;
import org.jfree.experimental.chart.plot.dial.DialCap;


public class AbstractDialLayerTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(AbstractDialLayerTests.class);
    }

    
    public AbstractDialLayerTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DialCap c1 = new DialCap();
        DialCap c2 = new DialCap();
        assertTrue(c1.equals(c2));
        
        
        c1.setVisible(false);
        assertFalse(c1.equals(c2));
        c2.setVisible(false);
        assertTrue(c1.equals(c2));
    }

    
    public void testCloning() {
        
        DialCap c1 = new DialCap();
        DialCap c2 = null;
        try {
            c2 = (DialCap) c1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(c1 != c2);
        assertTrue(c1.getClass() == c2.getClass());
        assertTrue(c1.equals(c2));
        
        
        MyDialLayerChangeListener l1 = new MyDialLayerChangeListener();
        c1.addChangeListener(l1);
        assertTrue(c1.hasListener(l1));
        assertFalse(c2.hasListener(l1));
    }

    
    public void testSerialization() {
        
        DialCap c1 = new DialCap();
        DialCap c2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(c1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            c2 = (DialCap) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(c1, c2);

        
        MyDialLayerChangeListener l1 = new MyDialLayerChangeListener();
        c1.addChangeListener(l1);
        assertTrue(c1.hasListener(l1));
        assertFalse(c2.hasListener(l1));
    }

}
