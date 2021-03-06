

package org.jfree.chart.renderer.xy.junit;

import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.util.PublicCloneable;


public class CandlestickRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(CandlestickRendererTests.class);
    }

    
    public CandlestickRendererTests(String name) {
        super(name);
    }

    private static final double EPSILON = 0.0000000001;

    
    public void testConstructor() {
        CandlestickRenderer r1 = new CandlestickRenderer();

        
        assertEquals(Color.green, r1.getUpPaint());
        assertEquals(Color.red, r1.getDownPaint());
        assertFalse(r1.getUseOutlinePaint());
        assertTrue(r1.getDrawVolume());
        assertEquals(Color.gray, r1.getVolumePaint());
        assertEquals(-1.0, r1.getCandleWidth(), EPSILON);
    }

    
    public void testEquals() {
        CandlestickRenderer r1 = new CandlestickRenderer();
        CandlestickRenderer r2 = new CandlestickRenderer();
        assertEquals(r1, r2);

        
        r1.setUpPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.white));
        assertFalse(r1.equals(r2));
        r2.setUpPaint(new GradientPaint(1.0f, 2.0f, Color.red, 3.0f, 4.0f,
                Color.white));
        assertTrue(r1.equals(r2));

        
        r1.setDownPaint(new GradientPaint(5.0f, 6.0f, Color.green, 7.0f, 8.0f,
                Color.yellow));
        assertFalse(r1.equals(r2));
        r2.setDownPaint(new GradientPaint(5.0f, 6.0f, Color.green, 7.0f, 8.0f,
                Color.yellow));
        assertTrue(r1.equals(r2));

        
        r1.setDrawVolume(false);
        assertFalse(r1.equals(r2));
        r2.setDrawVolume(false);
        assertTrue(r1.equals(r2));

        
        r1.setCandleWidth(3.3);
        assertFalse(r1.equals(r2));
        r2.setCandleWidth(3.3);
        assertTrue(r1.equals(r2));

        
        r1.setMaxCandleWidthInMilliseconds(123);
        assertFalse(r1.equals(r2));
        r2.setMaxCandleWidthInMilliseconds(123);
        assertTrue(r1.equals(r2));

        
        r1.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        assertFalse(r1.equals(r2));
        r2.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST);
        assertTrue(r1.equals(r2));

        
        r1.setAutoWidthFactor(0.22);
        assertFalse(r1.equals(r2));
        r2.setAutoWidthFactor(0.22);
        assertTrue(r1.equals(r2));

        
        r1.setAutoWidthGap(1.1);
        assertFalse(r1.equals(r2));
        r2.setAutoWidthGap(1.1);
        assertTrue(r1.equals(r2));

        r1.setUseOutlinePaint(true);
        assertFalse(r1.equals(r2));
        r2.setUseOutlinePaint(true);
        assertTrue(r1.equals(r2));

        r1.setVolumePaint(Color.blue);
        assertFalse(r1.equals(r2));
        r2.setVolumePaint(Color.blue);
        assertTrue(r1.equals(r2));
    }

    
    public void testHashcode() {
        CandlestickRenderer r1 = new CandlestickRenderer();
        CandlestickRenderer r2 = new CandlestickRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    
    public void testCloning() {
        CandlestickRenderer r1 = new CandlestickRenderer();
        CandlestickRenderer r2 = null;
        try {
            r2 = (CandlestickRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    
    public void testPublicCloneable() {
        CandlestickRenderer r1 = new CandlestickRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    
    public void testSerialization() {

        CandlestickRenderer r1 = new CandlestickRenderer();
        CandlestickRenderer r2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(r1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                    new ByteArrayInputStream(buffer.toByteArray()));
            r2 = (CandlestickRenderer) in.readObject();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(r1, r2);

    }

    
    public void testFindRangeBounds() {
        CandlestickRenderer renderer = new CandlestickRenderer();

        OHLCDataItem item1 = new OHLCDataItem(new Date(1L), 2.0, 4.0, 1.0, 3.0,
                100);
        OHLCDataset dataset = new DefaultOHLCDataset("S1",
                new OHLCDataItem[] {item1});
        Range range = renderer.findRangeBounds(dataset);
        assertEquals(new Range(1.0, 4.0), range);

        OHLCDataItem item2 = new OHLCDataItem(new Date(1L), -1.0, 3.0, -1.0,
                3.0, 100);
        dataset = new DefaultOHLCDataset("S1", new OHLCDataItem[] {item1,
                item2});
        range = renderer.findRangeBounds(dataset);
        assertEquals(new Range(-1.0, 4.0), range);

        
        dataset = new DefaultOHLCDataset("S1", new OHLCDataItem[] {});
        range = renderer.findRangeBounds(dataset);
        assertNull(range);

        
        range = renderer.findRangeBounds(null);
        assertNull(range);
    }

}
