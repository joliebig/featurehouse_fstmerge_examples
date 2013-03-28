

package org.jfree.chart.renderer.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.AreaRendererEndType;


public class AreaRendererEndTypeTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(AreaRendererEndTypeTests.class);
    }

    
    public AreaRendererEndTypeTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        assertEquals(AreaRendererEndType.LEVEL, AreaRendererEndType.LEVEL);
        assertEquals(AreaRendererEndType.TAPER, AreaRendererEndType.TAPER);
        assertEquals(
            AreaRendererEndType.TRUNCATE, AreaRendererEndType.TRUNCATE
        );
    }

    
    public void testSerialization() {

        AreaRendererEndType t1 = AreaRendererEndType.TAPER;
        AreaRendererEndType t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (AreaRendererEndType) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(t1, t2);
        boolean same = t1 == t2;
        assertEquals(true, same);
    }

}
