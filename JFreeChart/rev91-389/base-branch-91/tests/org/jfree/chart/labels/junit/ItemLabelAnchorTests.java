

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

import org.jfree.chart.labels.ItemLabelAnchor;


public class ItemLabelAnchorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ItemLabelAnchorTests.class);
    }

    
    public ItemLabelAnchorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertTrue(ItemLabelAnchor.INSIDE1.equals(ItemLabelAnchor.INSIDE1));
        assertFalse(ItemLabelAnchor.INSIDE1.equals(ItemLabelAnchor.INSIDE2));
    }
    
    
    public void testSerialization() {

        ItemLabelAnchor a1 = ItemLabelAnchor.INSIDE1;
        ItemLabelAnchor a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            a2 = (ItemLabelAnchor) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertTrue(a1 == a2); 

    }

}
