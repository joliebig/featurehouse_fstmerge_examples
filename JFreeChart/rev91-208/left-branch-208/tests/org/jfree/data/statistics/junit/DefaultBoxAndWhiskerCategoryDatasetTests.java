

package org.jfree.data.statistics.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;


public class DefaultBoxAndWhiskerCategoryDatasetTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultBoxAndWhiskerCategoryDatasetTests.class);
    }

    
    public DefaultBoxAndWhiskerCategoryDatasetTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        DefaultBoxAndWhiskerCategoryDataset d1 
                = new DefaultBoxAndWhiskerCategoryDataset();
        d1.add(new BoxAndWhiskerItem(new Double(1.0), new Double(2.0), 
                new Double(3.0), new Double(4.0), new Double(5.0), 
                new Double(6.0), new Double(7.0), new Double(8.0), 
                new ArrayList()), "ROW1", "COLUMN1");
        DefaultBoxAndWhiskerCategoryDataset d2 
                = new DefaultBoxAndWhiskerCategoryDataset();
        d2.add(new BoxAndWhiskerItem(new Double(1.0), new Double(2.0), 
                new Double(3.0), new Double(4.0), new Double(5.0), 
                new Double(6.0), new Double(7.0), new Double(8.0),
                new ArrayList()), "ROW1", "COLUMN1");
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));
    }

    
    public void testSerialization() {

        DefaultBoxAndWhiskerCategoryDataset d1 
                = new DefaultBoxAndWhiskerCategoryDataset();
        d1.add(new BoxAndWhiskerItem(new Double(1.0), new Double(2.0), 
                new Double(3.0), new Double(4.0), new Double(5.0), 
                new Double(6.0), new Double(7.0), new Double(8.0),
                new ArrayList()), "ROW1", "COLUMN1");
        DefaultBoxAndWhiskerCategoryDataset d2 = null;
        
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            d2 = (DefaultBoxAndWhiskerCategoryDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(d1, d2);

    }

    
    public void testCloning() {
        DefaultBoxAndWhiskerCategoryDataset d1 
                = new DefaultBoxAndWhiskerCategoryDataset();
        d1.add(new BoxAndWhiskerItem(new Double(1.0), new Double(2.0), 
                new Double(3.0), new Double(4.0), new Double(5.0), 
                new Double(6.0), new Double(7.0), new Double(8.0),
                new ArrayList()), "ROW1", "COLUMN1");
        DefaultBoxAndWhiskerCategoryDataset d2 = null;
        try {
            d2 = (DefaultBoxAndWhiskerCategoryDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
        
        
        d1.add(new BoxAndWhiskerItem(new Double(1.0), new Double(2.0), 
                new Double(3.0), new Double(4.0), new Double(5.0), 
                new Double(6.0), new Double(7.0), new Double(8.0),
                new ArrayList()), "ROW2", "COLUMN1");
        assertFalse(d1.equals(d2));
    }

    
    public void test1701822() {
        DefaultBoxAndWhiskerCategoryDataset dataset 
                = new DefaultBoxAndWhiskerCategoryDataset();
        try {
            dataset.add(new BoxAndWhiskerItem(new Double(1.0), new Double(2.0), 
                    new Double(3.0), new Double(4.0), new Double(5.0), 
                    new Double(6.0), null, new Double(8.0),
                    new ArrayList()), "ROW1", "COLUMN1");
            dataset.add(new BoxAndWhiskerItem(new Double(1.0), new Double(2.0), 
                    new Double(3.0), new Double(4.0), new Double(5.0), 
                    new Double(6.0), new Double(7.0), null,
                    new ArrayList()), "ROW1", "COLUMN2");
        }
        catch (NullPointerException e) {
            assertTrue(false);
        }
        
    }
}
