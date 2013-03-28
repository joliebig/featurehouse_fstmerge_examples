

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

import org.jfree.chart.labels.SymbolicXYItemLabelGenerator;


public class SymbolicXYItemLabelGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(SymbolicXYItemLabelGeneratorTests.class);
    }

    
    public SymbolicXYItemLabelGeneratorTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        SymbolicXYItemLabelGenerator g1 = new SymbolicXYItemLabelGenerator();
        SymbolicXYItemLabelGenerator g2 = new SymbolicXYItemLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g2.equals(g1));
    }

    
    public void testHashCode() {
        SymbolicXYItemLabelGenerator g1
                = new SymbolicXYItemLabelGenerator();
        SymbolicXYItemLabelGenerator g2
                = new SymbolicXYItemLabelGenerator();
        assertTrue(g1.equals(g2));
        assertTrue(g1.hashCode() == g2.hashCode());
    }

    
    public void testCloning() {
        SymbolicXYItemLabelGenerator g1 = new SymbolicXYItemLabelGenerator();
        SymbolicXYItemLabelGenerator g2 = null;
        try {
            g2 = (SymbolicXYItemLabelGenerator) g1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(g1 != g2);
        assertTrue(g1.getClass() == g2.getClass());
        assertTrue(g1.equals(g2));
    }

    
    public void testSerialization() {
        SymbolicXYItemLabelGenerator g1 = new SymbolicXYItemLabelGenerator();
        SymbolicXYItemLabelGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            g2 = (SymbolicXYItemLabelGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(g1, g2);
    }

}
