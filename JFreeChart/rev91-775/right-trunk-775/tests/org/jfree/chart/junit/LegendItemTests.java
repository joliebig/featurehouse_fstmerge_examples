

package org.jfree.chart.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.AttributedString;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.LegendItem;
import org.jfree.chart.util.GradientPaintTransformType;
import org.jfree.chart.util.StandardGradientPaintTransformer;


public class LegendItemTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(LegendItemTests.class);
    }

    
    public LegendItemTests(String name) {
        super(name);
    }

    
    public void testEquals() {
        
        LegendItem item1 = new LegendItem("Label", "Description", 
                "ToolTip", "URL", true, 
                new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), true, Color.red, 
                true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), 
                new BasicStroke(2.1f), Color.green);  
        LegendItem item2 = new LegendItem("Label", "Description", 
                "ToolTip", "URL", true, 
                new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green);  
        assertTrue(item1.equals(item2));  
        assertTrue(item2.equals(item1));  
        
        item1 = new LegendItem("Label2", "Description", "ToolTip", "URL",
                true, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), true, 
                Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description", "ToolTip", "URL", 
                true, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", true, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", true, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                true, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.red, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", "URL",
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", "URL", 
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, true, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.blue, new BasicStroke(1.2f), 
                true, new Line2D.Double(1.0, 2.0, 3.0, 4.0), 
                new BasicStroke(2.1f), Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", "URL",
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, false, Color.blue, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", "URL", 
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, false, Color.yellow, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", "URL", 
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, false, Color.yellow, new BasicStroke(1.2f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", "URL",
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, false, Color.yellow, new BasicStroke(2.1f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", "URL",
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, false, Color.yellow, new BasicStroke(2.1f), true, 
                new Line2D.Double(1.0, 2.0, 3.0, 4.0), new BasicStroke(2.1f), 
                Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.yellow, new BasicStroke(2.1f), 
                false, new Line2D.Double(1.0, 2.0, 3.0, 4.0), 
                new BasicStroke(2.1f), Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.yellow, new BasicStroke(2.1f),
                false, new Line2D.Double(1.0, 2.0, 3.0, 4.0), 
                new BasicStroke(2.1f),  Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.yellow, new BasicStroke(2.1f),
                false, new Line2D.Double(4.0, 3.0, 2.0, 1.0), 
                new BasicStroke(2.1f), Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.yellow, new BasicStroke(2.1f),
                false, new Line2D.Double(4.0, 3.0, 2.0, 1.0), 
                new BasicStroke(2.1f), Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.yellow, new BasicStroke(2.1f), 
                false, new Line2D.Double(4.0, 3.0, 2.0, 1.0), 
                new BasicStroke(3.3f), Color.green); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.yellow, new BasicStroke(2.1f), 
                false, new Line2D.Double(4.0, 3.0, 2.0, 1.0), 
                new BasicStroke(3.3f), Color.green); 
        assertTrue(item1.equals(item2));
        
        item1 = new LegendItem("Label2", "Description2", "ToolTip", "URL",
                false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), false, 
                Color.black, false, Color.yellow, new BasicStroke(2.1f), false, 
            new Line2D.Double(4.0, 3.0, 2.0, 1.0), new BasicStroke(3.3f), 
            Color.white
        ); 
        assertFalse(item1.equals(item2));
        item2 = new LegendItem("Label2", "Description2", "ToolTip", 
                "URL", false, new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0), 
                false, Color.black, false, Color.yellow, new BasicStroke(2.1f), 
                false, new Line2D.Double(4.0, 3.0, 2.0, 1.0), 
                new BasicStroke(3.3f), 
                Color.white); 
        assertTrue(item1.equals(item2));
        
        
        item1.setFillPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.CENTER_VERTICAL));
        assertFalse(item1.equals(item2));
        item2.setFillPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.CENTER_VERTICAL));
        assertTrue(item1.equals(item2));
    }

    
    public void testSerialization() {
        LegendItem item1 = new LegendItem("Item", "Description", 
                "ToolTip", "URL", 
                new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), Color.red); 
        LegendItem item2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(item1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            item2 = (LegendItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(item1, item2);
    }

    
    public void testSerialization2() {
        AttributedString as = new AttributedString("Test String");
        as.addAttribute(TextAttribute.FONT, new Font("Dialog", Font.PLAIN, 12));
        LegendItem item1 = new LegendItem(as, "Description", "ToolTip", "URL", 
                new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), Color.red); 
        LegendItem item2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(item1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            item2 = (LegendItem) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(item1, item2);
    }

    
    public void testCloning() {
        LegendItem item = new LegendItem("Item", "Description", 
                "ToolTip", "URL", 
                new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0), Color.red); 
        assertFalse(item instanceof Cloneable);
    }

}
