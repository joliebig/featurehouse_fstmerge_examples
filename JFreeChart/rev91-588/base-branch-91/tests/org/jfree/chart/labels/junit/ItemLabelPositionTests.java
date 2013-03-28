

package org.jfree.chart.labels.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.ItemLabelPosition;


public class ItemLabelPositionTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ItemLabelPositionTests.class);
    }

    
    public ItemLabelPositionTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        ItemLabelPosition p1 = new ItemLabelPosition();
        ItemLabelPosition p2 = new ItemLabelPosition();
        assertEquals(p1, p2);
    }

    
    public void testSerialization() {

        ItemLabelPosition p1 = new ItemLabelPosition();
        ItemLabelPosition p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            p2 = (ItemLabelPosition) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2);

    }

}
