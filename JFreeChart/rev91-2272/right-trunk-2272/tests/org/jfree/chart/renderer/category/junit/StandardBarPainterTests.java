

package org.jfree.chart.renderer.category.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.util.PublicCloneable;


public class StandardBarPainterTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardBarPainterTests.class);
    }

    
    public StandardBarPainterTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardBarPainter p1 = new StandardBarPainter();
        StandardBarPainter p2 = new StandardBarPainter();
        assertEquals(p1, p2);
    }

    
    public void testHashcode() {
        StandardBarPainter p1 = new StandardBarPainter();
        StandardBarPainter p2 = new StandardBarPainter();
        assertTrue(p1.equals(p2));
        int h1 = p1.hashCode();
        int h2 = p2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        StandardBarPainter p1 = new StandardBarPainter();
        assertFalse(p1 instanceof Cloneable);
        assertFalse(p1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {
        StandardBarPainter p1 = new StandardBarPainter();
        StandardBarPainter p2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();
            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (StandardBarPainter) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);
    }

}
