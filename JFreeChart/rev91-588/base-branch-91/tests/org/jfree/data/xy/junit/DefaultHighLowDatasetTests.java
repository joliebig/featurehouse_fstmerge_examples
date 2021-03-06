

package org.jfree.data.xy.junit;

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

import org.jfree.data.xy.DefaultHighLowDataset;


public class DefaultHighLowDatasetTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultHighLowDatasetTests.class);
    }

    
    public DefaultHighLowDatasetTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DefaultHighLowDataset d1 = new DefaultHighLowDataset("Series 1", 
                new Date[0], new double[0], new double[0], new double[0], 
                new double[0], new double[0]);
        DefaultHighLowDataset d2 = new DefaultHighLowDataset("Series 1", 
                new Date[0], new double[0], new double[0], new double[0], 
                new double[0], new double[0]);
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));
        
        d1 = new DefaultHighLowDataset("Series 2", 
                new Date[0], new double[0], new double[0], new double[0], 
                new double[0], new double[0]);
        assertFalse(d1.equals(d2));
        d2 = new DefaultHighLowDataset("Series 2", 
                new Date[0], new double[0], new double[0], new double[0], 
                new double[0], new double[0]);
        assertTrue(d1.equals(d2));

        d1 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[1], new double[1], 
                new double[1], new double[1], new double[1]);
        assertFalse(d1.equals(d2));
        d2 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[1], new double[1], 
                new double[1], new double[1], new double[1]);
        assertTrue(d1.equals(d2));
    
        d1 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, new double[1], 
                new double[1], new double[1], new double[1]);
        assertFalse(d1.equals(d2));
        d2 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, new double[1], 
                new double[1], new double[1], new double[1]);
        assertTrue(d1.equals(d2));

        d1 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[1], new double[1], 
                new double[1]);
        assertFalse(d1.equals(d2));
        d2 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[1], new double[1], 
                new double[1]);
        assertTrue(d1.equals(d2));
    
        d1 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[1], 
                new double[1]);
        assertFalse(d1.equals(d2));
        d2 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[1], 
                new double[1]);
        assertTrue(d1.equals(d2));

        d1 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[] {7.8}, 
                new double[1]);
        assertFalse(d1.equals(d2));
        d2 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[] {7.8}, 
                new double[1]);
        assertTrue(d1.equals(d2));

        d1 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[] {7.8}, 
                new double[] {99.9});
        assertFalse(d1.equals(d2));
        d2 = new DefaultHighLowDataset("Series 2", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[] {7.8}, 
                new double[] {99.9});
        assertTrue(d1.equals(d2));
    
    }

    
    public void testCloning() {
        DefaultHighLowDataset d1 = new DefaultHighLowDataset("Series 1", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[] {7.8}, 
                new double[] {99.9});
        DefaultHighLowDataset d2 = null;
        try {
            d2 = (DefaultHighLowDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
    }

    
    public void testSerialization() {
        DefaultHighLowDataset d1 = new DefaultHighLowDataset("Series 1", 
                new Date[] {new Date(123L)}, new double[] {1.2}, 
                new double[] {3.4}, new double[] {5.6}, new double[] {7.8}, 
                new double[] {99.9});
        DefaultHighLowDataset d2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            d2 = (DefaultHighLowDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(d1, d2);
    }

}
