

package org.jfree.chart.entity.junit;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.entity.TickLabelEntity;


public class TickLabelEntityTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(TickLabelEntityTests.class);
    }

    
    public TickLabelEntityTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        TickLabelEntity e1 = new TickLabelEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL"
        ); 
        TickLabelEntity e2 = new TickLabelEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL"
        ); 
        assertTrue(e1.equals(e2));  
        
        e1.setArea(new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0));
        assertFalse(e1.equals(e2));
        e2.setArea(new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0));
        assertTrue(e1.equals(e2));  

        e1.setToolTipText("New ToolTip");
        assertFalse(e1.equals(e2));
        e2.setToolTipText("New ToolTip");
        assertTrue(e1.equals(e2));  

        e1.setURLText("New URL");
        assertFalse(e1.equals(e2));
        e2.setURLText("New URL");
        assertTrue(e1.equals(e2));  
    }

    
    public void testCloning() {
        TickLabelEntity e1 = new TickLabelEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL"
        ); 
        TickLabelEntity e2 = null;
        try {
            e2 = (TickLabelEntity) e1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(e1 != e2);
        assertTrue(e1.getClass() == e2.getClass());
        assertTrue(e1.equals(e2));
    }

    
    public void testSerialization() {
        TickLabelEntity e1 = new TickLabelEntity(
            new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), "ToolTip", "URL"
        ); 
        TickLabelEntity e2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(e1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            e2 = (TickLabelEntity) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(e1, e2);
    }

}
