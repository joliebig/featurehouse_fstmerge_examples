

package org.jfree.chart.plot.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.plot.ContourPlot;


public class ContourPlotTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(ContourPlotTests.class);
    }

    
    public ContourPlotTests(String name) {
        super(name);
    }

    
    public void testSerialization() {

        ContourPlot p1 = new ContourPlot(null, null, null, null);
        ContourPlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            p2 = (ContourPlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2);

    }

}
