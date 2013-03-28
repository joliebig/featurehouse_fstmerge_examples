

package org.jfree.chart.renderer.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.Stroke;
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

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.text.TextAnchor;


public class AbstractRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(AbstractRendererTests.class);
    }

    
    public AbstractRendererTests(String name) {
        super(name);
    }
    
    
    public void testEquals() {
        
        BarRenderer r1 = new BarRenderer();
        BarRenderer r2 = new BarRenderer();
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));
        
        
        r1.setSeriesVisible(2, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisible(2, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseSeriesVisible(false);
        assertFalse(r1.equals(r2));
        r2.setBaseSeriesVisible(false);
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesVisibleInLegend(1, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisibleInLegend(1, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseSeriesVisibleInLegend(false);
        assertFalse(r1.equals(r2));
        r2.setBaseSeriesVisibleInLegend(false);
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.white));
        assertFalse(r1.equals(r2));
        r2.setSeriesPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.white));
        assertTrue(r1.equals(r2));
        
        
        r1.setBasePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setBasePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesFillPaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setSeriesFillPaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseFillPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setBaseFillPaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesOutlinePaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setSeriesOutlinePaint(0, new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertFalse(r1.equals(r2));
        r2.setBaseOutlinePaint(new GradientPaint(1.0f, 2.0f, Color.blue, 
                3.0f, 4.0f, Color.red));
        assertTrue(r1.equals(r2));
        
        
        Stroke s = new BasicStroke(3.21f);
        r1.setSeriesStroke(1, s);
        assertFalse(r1.equals(r2));
        r2.setSeriesStroke(1, s);
        assertTrue(r1.equals(r2));

        
        r1.setBaseStroke(s);
        assertFalse(r1.equals(r2));
        r2.setBaseStroke(s);
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesOutlineStroke(0, s);
        assertFalse(r1.equals(r2));
        r2.setSeriesOutlineStroke(0, s);
        assertTrue(r1.equals(r2));

        
        r1.setBaseOutlineStroke(s);
        assertFalse(r1.equals(r2));
        r2.setBaseOutlineStroke(s);
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesShape(1, new Rectangle(1, 2, 3, 4));
        assertFalse(r1.equals(r2));
        r2.setSeriesShape(1, new Rectangle(1, 2, 3, 4));
        assertTrue(r1.equals(r2));

        
        r1.setBaseShape(new Rectangle(1, 2, 3, 4));
        assertFalse(r1.equals(r2));
        r2.setBaseShape(new Rectangle(1, 2, 3, 4));
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesItemLabelsVisible(1, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelsVisible(1, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseItemLabelsVisible(true);
        assertFalse(r1.equals(r2));
        r2.setBaseItemLabelsVisible(true);
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesItemLabelFont(1, new Font("Serif", Font.BOLD, 9));
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelFont(1, new Font("Serif", Font.BOLD, 9));
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 10));
        assertFalse(r1.equals(r2));
        r2.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 10));
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesItemLabelPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelPaint(0, new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertTrue(r1.equals(r2));

        
        r1.setBaseItemLabelPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertFalse(r1.equals(r2));
        r2.setBaseItemLabelPaint(new GradientPaint(1.0f, 2.0f, Color.red, 
                3.0f, 4.0f, Color.gray));
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition());
        assertFalse(r1.equals(r2));
        r2.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition());
        assertTrue(r1.equals(r2));

        
        r1.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertFalse(r1.equals(r2));
        r2.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesNegativeItemLabelPosition(1, new ItemLabelPosition(
                ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertFalse(r1.equals(r2));
        r2.setSeriesNegativeItemLabelPosition(1, new ItemLabelPosition(
                ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertTrue(r1.equals(r2));

        
        r1.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertFalse(r1.equals(r2));
        r2.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.INSIDE10, TextAnchor.BASELINE_RIGHT));
        assertTrue(r1.equals(r2));

        
        r1.setItemLabelAnchorOffset(3.0);
        assertFalse(r1.equals(r2));
        r2.setItemLabelAnchorOffset(3.0);
        assertTrue(r1.equals(r2));

        
        r1.setSeriesCreateEntities(0, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesCreateEntities(0, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseCreateEntities(false);
        assertFalse(r1.equals(r2));
        r2.setBaseCreateEntities(false);
        assertTrue(r1.equals(r2));

    }
    
    
    public void testCloning() {
        LineAndShapeRenderer r1 = new LineAndShapeRenderer();
        Rectangle2D baseShape = new Rectangle2D.Double(11.0, 12.0, 13.0, 14.0);
        r1.setBaseShape(baseShape);
        
        LineAndShapeRenderer r2 = null;
        try {
            r2 = (LineAndShapeRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        r1.setSeriesVisible(0, Boolean.FALSE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisible(0, Boolean.FALSE);
        assertTrue(r1.equals(r2));
        
        r1.setSeriesVisibleInLegend(0, Boolean.FALSE);
        assertFalse(r1.equals(r2));
        r2.setSeriesVisibleInLegend(0, Boolean.FALSE);
        assertTrue(r1.equals(r2));
        
        r1.setSeriesPaint(0, Color.black);
        assertFalse(r1.equals(r2));
        r2.setSeriesPaint(0, Color.black);
        assertTrue(r1.equals(r2));
        
        r1.setSeriesFillPaint(0, Color.yellow);
        assertFalse(r1.equals(r2));
        r2.setSeriesFillPaint(0, Color.yellow);
        assertTrue(r1.equals(r2));

        r1.setSeriesOutlinePaint(0, Color.yellow);
        assertFalse(r1.equals(r2));
        r2.setSeriesOutlinePaint(0, Color.yellow);
        assertTrue(r1.equals(r2));
        
        r1.setSeriesStroke(0, new BasicStroke(2.2f));
        assertFalse(r1.equals(r2));
        r2.setSeriesStroke(0, new BasicStroke(2.2f));
        assertTrue(r1.equals(r2));
    
        r1.setSeriesOutlineStroke(0, new BasicStroke(2.2f));
        assertFalse(r1.equals(r2));
        r2.setSeriesOutlineStroke(0, new BasicStroke(2.2f));
        assertTrue(r1.equals(r2));
        
        baseShape.setRect(4.0, 3.0, 2.0, 1.0);
        assertFalse(r1.equals(r2));
        r2.setBaseShape(new Rectangle2D.Double(4.0, 3.0, 2.0, 1.0));
        assertTrue(r1.equals(r2));
        
        r1.setSeriesShape(0, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));
        assertFalse(r1.equals(r2));
        r2.setSeriesShape(0, new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));
        assertTrue(r1.equals(r2));
        
        r1.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        assertTrue(r1.equals(r2));
        
        r1.setSeriesItemLabelPaint(0, Color.red);
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelPaint(0, Color.red);
        assertTrue(r1.equals(r2));
        
        r1.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition());
        assertFalse(r1.equals(r2));
        r2.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition());
        assertTrue(r1.equals(r2));

        r1.setSeriesNegativeItemLabelPosition(0, new ItemLabelPosition());
        assertFalse(r1.equals(r2));
        r2.setSeriesNegativeItemLabelPosition(0, new ItemLabelPosition());
        assertTrue(r1.equals(r2));
        
        r1.setSeriesCreateEntities(0, Boolean.FALSE);
        assertFalse(r1.equals(r2));
        r2.setSeriesCreateEntities(0, Boolean.FALSE);
        assertTrue(r1.equals(r2));
    }

    
    static class MyRendererChangeListener implements RendererChangeListener {

        
        public RendererChangeEvent lastEvent;
        
        
        public MyRendererChangeListener() {
            this.lastEvent = null;
        }
        public void rendererChanged(RendererChangeEvent event) {
            this.lastEvent = event;
        }
    }
    
    
    public void testCloning2() {
        LineAndShapeRenderer r1 = new LineAndShapeRenderer();
        r1.setBasePaint(Color.blue);
        LineAndShapeRenderer r2 = null;
        try {
            r2 = (LineAndShapeRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        MyRendererChangeListener listener = new MyRendererChangeListener();
        r2.addChangeListener(listener);
        r2.setBasePaint(Color.red);
        assertTrue(listener.lastEvent.getRenderer() == r2);
        assertFalse(r1.hasListener(listener));
    }

    
    public void testEventNotification() {
        
        RendererChangeDetector detector = new RendererChangeDetector();
        BarRenderer r1 = new BarRenderer();  
                                             
        r1.addChangeListener(detector);
        
        
        detector.setNotified(false);
        r1.setSeriesPaint(0, Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBasePaint(Color.red);
        assertTrue(detector.getNotified());

        
        detector.setNotified(false);
        r1.setSeriesOutlinePaint(0, Color.red);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseOutlinePaint(Color.red);
        assertTrue(detector.getNotified());
        
        
        detector.setNotified(false);
        r1.setSeriesStroke(0, new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        
        detector.setNotified(false);
        r1.setSeriesOutlineStroke(0, new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseOutlineStroke(new BasicStroke(1.0f));
        assertTrue(detector.getNotified());

        
        detector.setNotified(false);
        r1.setSeriesShape(0, new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseShape(new Rectangle2D.Float());
        assertTrue(detector.getNotified());

        
        detector.setNotified(false);
        r1.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelsVisible(true);
        assertTrue(detector.getNotified());
        
        
        detector.setNotified(false);
        r1.setSeriesItemLabelFont(0, new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelFont(new Font("Serif", Font.PLAIN, 12));
        assertTrue(detector.getNotified());
        
        
        detector.setNotified(false);
        r1.setSeriesItemLabelPaint(0, Color.blue);
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseItemLabelPaint(Color.blue);
        assertTrue(detector.getNotified());
        
        
        detector.setNotified(false);
        r1.setSeriesPositiveItemLabelPosition(0, new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        assertTrue(detector.getNotified());

        
        detector.setNotified(false);
        r1.setSeriesNegativeItemLabelPosition(0, new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        assertTrue(detector.getNotified());

        detector.setNotified(false);
        r1.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        assertTrue(detector.getNotified());

    }

    
    public void testSerialization() {

        BarRenderer r1 = new BarRenderer();  
        BarRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (BarRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);
        try {
            r2.notifyListeners(new RendererChangeEvent(r2));
        }
        catch (NullPointerException e) {
            assertTrue(false);  
        }

    }
    
    
    public void testAutoPopulateFlagDefaults() {
        BarRenderer r = new BarRenderer();
        assertEquals(true, r.getAutoPopulateSeriesPaint());
        assertEquals(false, r.getAutoPopulateSeriesFillPaint());
        assertEquals(false, r.getAutoPopulateSeriesOutlinePaint());
        assertEquals(false, r.getAutoPopulateSeriesStroke());
        assertEquals(false, r.getAutoPopulateSeriesOutlineStroke());
        assertEquals(true, r.getAutoPopulateSeriesShape());
    }
    
    
    public void testPaintLookup() {
        BarRenderer r = new BarRenderer();
        assertEquals(Color.blue, r.getBasePaint());
        
        
        r.setAutoPopulateSeriesPaint(false);
        assertEquals(Color.blue, r.lookupSeriesPaint(0));
        assertNull(r.getSeriesPaint(0));
        
        
        r.setAutoPopulateSeriesPaint(true);
         new CategoryPlot(null, new CategoryAxis(
                "Category"), new NumberAxis("Value"), r);
        assertEquals(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE[0], 
                r.lookupSeriesPaint(0));
        assertNotNull(r.getSeriesPaint(0));
    }

    
    public void testFillPaintLookup() {
        BarRenderer r = new BarRenderer();
        assertEquals(Color.white, r.getBaseFillPaint());
        
        
        r.setAutoPopulateSeriesFillPaint(false);
        assertEquals(Color.white, r.lookupSeriesFillPaint(0));
        assertNull(r.getSeriesFillPaint(0));
        
        
        r.setAutoPopulateSeriesFillPaint(true);
         new CategoryPlot(null, new CategoryAxis(
                "Category"), new NumberAxis("Value"), r);
        assertEquals(DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE[0], 
                r.lookupSeriesFillPaint(0));
        assertNotNull(r.getSeriesFillPaint(0));
    }

    
    public void testOutlinePaintLookup() {
        BarRenderer r = new BarRenderer();
        assertEquals(Color.gray, r.getBaseOutlinePaint());
        
        
        r.setAutoPopulateSeriesOutlinePaint(false);
        assertEquals(Color.gray, r.lookupSeriesOutlinePaint(0));
        assertNull(r.getSeriesOutlinePaint(0));
        
        
        r.setAutoPopulateSeriesOutlinePaint(true);
         new CategoryPlot(null, new CategoryAxis(
                "Category"), new NumberAxis("Value"), r);
        assertEquals(DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE[0], 
                r.lookupSeriesOutlinePaint(0));
        assertNotNull(r.getSeriesOutlinePaint(0));
    }

}
