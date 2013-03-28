

package org.jfree.chart.block.junit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Size2D;


public class BorderArrangementTests extends TestCase {

    private static final double EPSILON = 0.0000000001;
    
    
    public static Test suite() {
        return new TestSuite(BorderArrangementTests.class);
    }

    
    public BorderArrangementTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        BorderArrangement b1 = new BorderArrangement();
        BorderArrangement b2 = new BorderArrangement();
        assertTrue(b1.equals(b2));
        assertTrue(b2.equals(b1));

        b1.add(new EmptyBlock(99.0, 99.0), null);
        assertFalse(b1.equals(b2));
        b2.add(new EmptyBlock(99.0, 99.0), null);
        assertTrue(b1.equals(b2));

        b1.add(new EmptyBlock(1.0, 1.0), RectangleEdge.LEFT);
        assertFalse(b1.equals(b2));
        b2.add(new EmptyBlock(1.0, 1.0), RectangleEdge.LEFT);
        assertTrue(b1.equals(b2));

        b1.add(new EmptyBlock(2.0, 2.0), RectangleEdge.RIGHT);
        assertFalse(b1.equals(b2));
        b2.add(new EmptyBlock(2.0, 2.0), RectangleEdge.RIGHT);
        assertTrue(b1.equals(b2));

        b1.add(new EmptyBlock(3.0, 3.0), RectangleEdge.TOP);
        assertFalse(b1.equals(b2));
        b2.add(new EmptyBlock(3.0, 3.0), RectangleEdge.TOP);
        assertTrue(b1.equals(b2));

        b1.add(new EmptyBlock(4.0, 4.0), RectangleEdge.BOTTOM);
        assertFalse(b1.equals(b2));
        b2.add(new EmptyBlock(4.0, 4.0), RectangleEdge.BOTTOM);
        assertTrue(b1.equals(b2));
    }

    
    public void testCloning() {
        BorderArrangement b1 = new BorderArrangement();
        assertFalse(b1 instanceof Cloneable);
    }

    
    public void testSerialization() {
        BorderArrangement b1 = new BorderArrangement();
        BorderArrangement b2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(b1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            b2 = (BorderArrangement) in.readObject();
            in.close();
        }
        catch (Exception e) {
            fail(e.toString());
        }
        assertEquals(b1, b2);
    }
    
    
    public void testSizing() {
        BlockContainer container = new BlockContainer(new BorderArrangement());
        BufferedImage image = new BufferedImage(
            200, 100, BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2 = image.createGraphics();
        
        
        
        Size2D size = container.arrange(g2);
        assertEquals(0.0, size.width, EPSILON);
        assertEquals(0.0, size.height, EPSILON);
        
        
        
        container.add(new EmptyBlock(123.4, 567.8));
        size = container.arrange(g2);
        assertEquals(123.4, size.width, EPSILON);
        assertEquals(567.8, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.RIGHT);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.RIGHT);
        size = container.arrange(g2);
        assertEquals(22.3, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        container.clear();
        Block rb = new EmptyBlock(12.3, 15.6);
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(rb, RectangleEdge.RIGHT);
        size = container.arrange(g2);
        assertEquals(22.3, size.width, EPSILON);
        assertEquals(20.0, size.height, EPSILON);
        assertEquals(20.0, rb.getBounds().getHeight(), EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2);
        assertEquals(22.3, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        container.clear();
        Block lb = new EmptyBlock(12.3, 15.6);
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(lb, RectangleEdge.LEFT);
        size = container.arrange(g2);
        assertEquals(22.3, size.width, EPSILON);
        assertEquals(20.0, size.height, EPSILON);
        assertEquals(20.0, lb.getBounds().getHeight(), EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2);
        assertEquals(22.3, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        container.add(new EmptyBlock(5.4, 3.2), RectangleEdge.RIGHT);
        size = container.arrange(g2);
        assertEquals(27.7, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(31.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(31.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(31.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2);
        assertEquals(21.0, size.width, EPSILON);
        assertEquals(14.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
                
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.RIGHT);
        size = container.arrange(g2);
        assertEquals(33.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2);
        assertEquals(33.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2);
        assertEquals(33.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2);
        assertEquals(21.0, size.width, EPSILON);
        assertEquals(12.0, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(12.3, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(21.0, size.width, EPSILON);
        assertEquals(77.9, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(21.0, size.width, EPSILON);
        assertEquals(77.9, size.height, EPSILON);
                
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2);
        assertEquals(16.0, size.width, EPSILON);
        assertEquals(16.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.LEFT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2);
        assertEquals(21.0, size.width, EPSILON);
        assertEquals(77.9, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2);
        assertEquals(14.0, size.width, EPSILON);
        assertEquals(16.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        size = container.arrange(g2);
        assertEquals(12.0, size.width, EPSILON);
        assertEquals(14.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2);
        assertEquals(21.0, size.width, EPSILON);
        assertEquals(16.0, size.height, EPSILON);

    }
    
    
    public void testSizingWithWidthConstraint() {
        RectangleConstraint constraint = new RectangleConstraint(
            10.0, new Range(10.0, 10.0), LengthConstraintType.FIXED,
            0.0, new Range(0.0, 0.0), LengthConstraintType.NONE
        );
                
        BlockContainer container = new BlockContainer(new BorderArrangement());
        BufferedImage image = new BufferedImage(
            200, 100, BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g2 = image.createGraphics();
        
        
        
        container.add(new EmptyBlock(5.0, 6.0));
        Size2D size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(6.0, size.height, EPSILON);
        
        container.clear();
        container.add(new EmptyBlock(15.0, 16.0));
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(16.0, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.RIGHT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(7.0, 20.0));
        container.add(new EmptyBlock(8.0, 45.6), RectangleEdge.RIGHT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        container.add(new EmptyBlock(5.4, 3.2), RectangleEdge.RIGHT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(14.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(45.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0));
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
                
        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.RIGHT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.TOP);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.LEFT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(12.0, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(65.6, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3));
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(77.9, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(77.9, size.height, EPSILON);
                
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(16.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(21.0, 12.3), RectangleEdge.LEFT);
        container.add(new EmptyBlock(10.0, 20.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(12.3, 45.6), RectangleEdge.BOTTOM);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(77.9, size.height, EPSILON);

        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(16.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(14.0, size.height, EPSILON);
        
        
        
        container.clear();
        container.add(new EmptyBlock(1.0, 2.0), RectangleEdge.TOP);
        container.add(new EmptyBlock(3.0, 4.0), RectangleEdge.BOTTOM);
        container.add(new EmptyBlock(5.0, 6.0), RectangleEdge.LEFT);
        container.add(new EmptyBlock(7.0, 8.0), RectangleEdge.RIGHT);
        container.add(new EmptyBlock(9.0, 10.0));
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(16.0, size.height, EPSILON);

        
        
        container.clear();
        size = container.arrange(g2, constraint);
        assertEquals(10.0, size.width, EPSILON);
        assertEquals(0.0, size.height, EPSILON);
        
    }
}

