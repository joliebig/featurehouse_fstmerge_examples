

package org.jfree.chart.block.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.block.EmptyBlock;


public class EmptyBlockTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(EmptyBlockTests.class);
    }

    
    public EmptyBlockTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        EmptyBlock b1 = new EmptyBlock(1.0, 2.0);
        EmptyBlock b2 = new EmptyBlock(1.0, 2.0);
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b2));

        b1 = new EmptyBlock(1.1, 2.0);
        assertFalse(b1.equals(b2));
        b2 = new EmptyBlock(1.1, 2.0);
        assertTrue(b1.equals(b2));

        b1 = new EmptyBlock(1.1, 2.2);
        assertFalse(b1.equals(b2));
        b2 = new EmptyBlock(1.1, 2.2);
        assertTrue(b1.equals(b2));
    }

    
    public void testCloning() {
        EmptyBlock b1 = new EmptyBlock(1.0, 2.0);
        EmptyBlock b2 = null;

        try {
            b2 = (EmptyBlock) b1.clone();
        }
        catch (CloneNotSupportedException e) {
            fail(e.toString());
        }
        assertTrue(b1 != b2);
        assertTrue(b1.getClass() == b2.getClass());
        assertTrue(b1.equals(b2));
    }

    
    public void testSerialization() {
        EmptyBlock b1 = new EmptyBlock(1.0, 2.0);
        EmptyBlock b2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(b1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            b2 = (EmptyBlock) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(b1, b2);
    }

}
