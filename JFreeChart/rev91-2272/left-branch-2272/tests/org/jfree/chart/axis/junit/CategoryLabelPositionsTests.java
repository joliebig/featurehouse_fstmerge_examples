

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

import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;


public class CategoryLabelPositionsTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CategoryLabelPositionsTests.class);
    }

    
    public CategoryLabelPositionsTests(String name) {
        super(name);
    }

    private static final RectangleAnchor RA_TOP = RectangleAnchor.TOP;
    private static final RectangleAnchor RA_BOTTOM = RectangleAnchor.BOTTOM;

    
    public void testEquals() {
        CategoryLabelPositions p1 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        CategoryLabelPositions p2 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertEquals(p1, p2);

        p1 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertTrue(!p1.equals(p2));
        p2 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertTrue(p1.equals(p2));

        p1 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM, TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertTrue(!p1.equals(p2));
        p2 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertTrue(p1.equals(p2));

        p1 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertTrue(!p1.equals(p2));
        p2 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertTrue(p1.equals(p2));

        p1 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER));
        assertTrue(!p1.equals(p2));
        p2 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER),
                new CategoryLabelPosition(RA_BOTTOM,
                        TextBlockAnchor.TOP_CENTER));
        assertTrue(p1.equals(p2));
    }

    
    public void testHashCode() {
        CategoryLabelPositions p1 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        CategoryLabelPositions p2 = new CategoryLabelPositions(
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER),
                new CategoryLabelPosition(RA_TOP, TextBlockAnchor.CENTER));
        assertTrue(p1.equals(p2));
        int h1 = p1.hashCode();
        int h2 = p2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testSerialization() {

        CategoryLabelPositions p1 = CategoryLabelPositions.STANDARD;
        CategoryLabelPositions p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (CategoryLabelPositions) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(p1, p2);
    }

}
