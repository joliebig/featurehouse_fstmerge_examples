

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

import org.jfree.chart.labels.StandardContourToolTipGenerator;


public class StandardContourToolTipGeneratorTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(StandardContourToolTipGeneratorTests.class);
    }

    
    public StandardContourToolTipGeneratorTests(String name) {
        super(name);
    }

    
    public void testSerialization() {

        StandardContourToolTipGenerator g1
            = new StandardContourToolTipGenerator();
        StandardContourToolTipGenerator g2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(g1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            g2 = (StandardContourToolTipGenerator) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(g1, g2);

    }

}
