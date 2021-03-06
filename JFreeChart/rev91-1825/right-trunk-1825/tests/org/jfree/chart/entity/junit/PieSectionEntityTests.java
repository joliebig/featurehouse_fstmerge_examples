

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

import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.data.general.DefaultPieDataset;


public class PieSectionEntityTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(PieSectionEntityTests.class);
    }

    
    public PieSectionEntityTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        PieSectionEntity e1 = new PieSectionEntity(new Rectangle2D.Double(
                1.0, 2.0, 3.0, 4.0), new DefaultPieDataset(), 1, 2, "Key",
                "ToolTip", "URL");
        PieSectionEntity e2 = new PieSectionEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0), new DefaultPieDataset(), 1, 2, "Key",
                "ToolTip", "URL");
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

        e1.setDataset(null);
        assertFalse(e1.equals(e2));
        e2.setDataset(null);
        assertTrue(e1.equals(e2));

        e1.setPieIndex(99);
        assertFalse(e1.equals(e2));
        e2.setPieIndex(99);
        assertTrue(e1.equals(e2));

        e1.setSectionIndex(66);
        assertFalse(e1.equals(e2));
        e2.setSectionIndex(66);
        assertTrue(e1.equals(e2));

        e1.setSectionKey("ABC");
        assertFalse(e1.equals(e2));
        e2.setSectionKey("ABC");
        assertTrue(e1.equals(e2));
    }

    
    public void testCloning() {
        PieSectionEntity e1 = new PieSectionEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0), new DefaultPieDataset(), 1, 2, "Key",
                "ToolTip", "URL");
        PieSectionEntity e2 = null;
        try {
            e2 = (PieSectionEntity) e1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(e1 != e2);
        assertTrue(e1.getClass() == e2.getClass());
        assertTrue(e1.equals(e2));
    }

    
    public void testSerialization() {
        PieSectionEntity e1 = new PieSectionEntity(new Rectangle2D.Double(1.0,
                2.0, 3.0, 4.0), new DefaultPieDataset(), 1, 2, "Key",
                "ToolTip", "URL");
        PieSectionEntity e2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(e1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(
                    buffer.toByteArray()));
            e2 = (PieSectionEntity) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(e1, e2);
    }

}
