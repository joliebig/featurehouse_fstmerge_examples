

package org.jfree.data.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.DomainOrder;


public class DomainOrderTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(DomainOrderTests.class);
    }

    
    public DomainOrderTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertEquals(DomainOrder.NONE, DomainOrder.NONE);
        assertEquals(DomainOrder.ASCENDING, DomainOrder.ASCENDING);
        assertEquals(DomainOrder.DESCENDING, DomainOrder.DESCENDING);
        assertFalse(DomainOrder.NONE.equals(DomainOrder.ASCENDING));
        assertFalse(DomainOrder.NONE.equals(DomainOrder.DESCENDING));
        assertFalse(DomainOrder.NONE.equals(null));
        assertFalse(DomainOrder.ASCENDING.equals(DomainOrder.NONE));
        assertFalse(DomainOrder.ASCENDING.equals(DomainOrder.DESCENDING));
        assertFalse(DomainOrder.ASCENDING.equals(null));
        assertFalse(DomainOrder.DESCENDING.equals(DomainOrder.NONE));
        assertFalse(DomainOrder.DESCENDING.equals(DomainOrder.ASCENDING));
        assertFalse(DomainOrder.DESCENDING.equals(null));
    }

    
    public void testHashCode() {
        DomainOrder d1 = DomainOrder.ASCENDING;
        DomainOrder d2 = DomainOrder.ASCENDING;
        assertTrue(d1.equals(d2));
        int h1 = d1.hashCode();
        int h2 = d2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testSerialization() {
        DomainOrder d1 = DomainOrder.ASCENDING;
        DomainOrder d2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(d1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            d2 = (DomainOrder) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(d1, d2);
        boolean same = d1 == d2;
        assertEquals(true, same);
    }

}
