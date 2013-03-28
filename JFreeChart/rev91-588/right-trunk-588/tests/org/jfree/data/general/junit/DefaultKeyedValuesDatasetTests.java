

package org.jfree.data.general.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.general.DefaultKeyedValuesDataset;
import org.jfree.data.general.KeyedValuesDataset;


public class DefaultKeyedValuesDatasetTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DefaultKeyedValuesDatasetTests.class);
    }

    
    public DefaultKeyedValuesDatasetTests(String name) {
        super(name);
    }

    
    public void testCloning() {
        DefaultKeyedValuesDataset d1 = new DefaultKeyedValuesDataset();
        d1.setValue("V1", new Integer(1));
        d1.setValue("V2", null);
        d1.setValue("V3", new Integer(3));
        DefaultKeyedValuesDataset d2 = null;
        try {
            d2 = (DefaultKeyedValuesDataset) d1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));
    }

    
    public void testSerialization() {

        DefaultKeyedValuesDataset d1 = new DefaultKeyedValuesDataset();
        d1.setValue("C1", new Double(234.2));
        d1.setValue("C2", null);
        d1.setValue("C3", new Double(345.9));
        d1.setValue("C4", new Double(452.7));

        KeyedValuesDataset d2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (KeyedValuesDataset) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);

    }

}
