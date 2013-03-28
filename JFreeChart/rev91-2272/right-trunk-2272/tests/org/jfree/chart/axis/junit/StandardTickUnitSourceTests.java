

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.StandardTickUnitSource;


public class StandardTickUnitSourceTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardTickUnitSourceTests.class);
    }

    
    public StandardTickUnitSourceTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        StandardTickUnitSource t1 = new StandardTickUnitSource();
        StandardTickUnitSource t2 = new StandardTickUnitSource();
        assertTrue(t1.equals(t2));
    }

    
    public void testSerialization() {

        StandardTickUnitSource t1 = new StandardTickUnitSource();
        StandardTickUnitSource t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            t2 = (StandardTickUnitSource) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(t1, t2);

    }

}
