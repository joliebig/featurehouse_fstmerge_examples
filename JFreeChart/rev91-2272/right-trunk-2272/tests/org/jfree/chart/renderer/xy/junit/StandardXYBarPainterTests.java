

package org.jfree.chart.renderer.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.util.PublicCloneable;


public class StandardXYBarPainterTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardXYBarPainterTests.class);
    }

    
    public StandardXYBarPainterTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardXYBarPainter p1 = new StandardXYBarPainter();
        StandardXYBarPainter p2 = new StandardXYBarPainter();
        assertEquals(p1, p2);
    }

    
    public void testHashcode() {
        StandardXYBarPainter p1 = new StandardXYBarPainter();
        StandardXYBarPainter p2 = new StandardXYBarPainter();
        assertTrue(p1.equals(p2));
        int h1 = p1.hashCode();
        int h2 = p2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        StandardXYBarPainter p1 = new StandardXYBarPainter();
        assertFalse(p1 instanceof Cloneable);
        assertFalse(p1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {
        StandardXYBarPainter p1 = new StandardXYBarPainter();
        StandardXYBarPainter p2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();
            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (StandardXYBarPainter) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);
    }

}

