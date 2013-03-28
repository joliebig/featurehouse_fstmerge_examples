

package org.jfree.chart.renderer.category.junit;

import java.text.NumberFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.urls.StandardCategoryURLGenerator;


public class AbstractCategoryItemRendererTests extends TestCase {

    
    public static Test suite() {
        return new TestSuite(AbstractCategoryItemRendererTests.class);
    }
    
    
    public void testEquals() {
        BarRenderer r1 = new BarRenderer();
        BarRenderer r2 = new BarRenderer();
        assertEquals(r1, r2);
        
        
        
        
        r1.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        assertFalse(r1.equals(r2));
        r2.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesToolTipGenerator(1, new StandardCategoryToolTipGenerator());
        assertFalse(r1.equals(r2));
        r2.setSeriesToolTipGenerator(1, new StandardCategoryToolTipGenerator());
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{2}", 
                NumberFormat.getInstance()));
        assertFalse(r1.equals(r2));
        r2.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{2}", 
                NumberFormat.getInstance()));
        assertTrue(r1.equals(r2));

        
        r1.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        assertFalse(r1.equals(r2));
        r2.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesItemLabelGenerator(1, 
                new StandardCategoryItemLabelGenerator());
        assertFalse(r1.equals(r2));
        r2.setSeriesItemLabelGenerator(1, 
                new StandardCategoryItemLabelGenerator());
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", NumberFormat.getInstance()));
        assertFalse(r1.equals(r2));
        r2.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", NumberFormat.getInstance()));
        assertTrue(r1.equals(r2));
    
        
        r1.setItemURLGenerator(new StandardCategoryURLGenerator());
        assertFalse(r1.equals(r2));
        r2.setItemURLGenerator(new StandardCategoryURLGenerator());
        assertTrue(r1.equals(r2));
        
        
        r1.setSeriesItemURLGenerator(1, new StandardCategoryURLGenerator());
        assertFalse(r1.equals(r2));
        r2.setSeriesItemURLGenerator(1, new StandardCategoryURLGenerator());
        assertTrue(r1.equals(r2));
        
        
        r1.setBaseItemURLGenerator(new StandardCategoryURLGenerator(
                "abc.html"));
        assertFalse(r1.equals(r2));
        r2.setBaseItemURLGenerator(new StandardCategoryURLGenerator(
                "abc.html"));
        assertTrue(r1.equals(r2));
        
        
        r1.setLegendItemLabelGenerator(new StandardCategorySeriesLabelGenerator(
                "XYZ"));
        assertFalse(r1.equals(r2));
        r2.setLegendItemLabelGenerator(new StandardCategorySeriesLabelGenerator(
                "XYZ"));
        assertTrue(r1.equals(r2));
        
        
        r1.setLegendItemToolTipGenerator(
                new StandardCategorySeriesLabelGenerator("ToolTip"));
        assertFalse(r1.equals(r2));
        r2.setLegendItemToolTipGenerator(
                new StandardCategorySeriesLabelGenerator("ToolTip"));
        assertTrue(r1.equals(r2));

        
        r1.setLegendItemURLGenerator(
                new StandardCategorySeriesLabelGenerator("URL"));
        assertFalse(r1.equals(r2));
        r2.setLegendItemURLGenerator(
                new StandardCategorySeriesLabelGenerator("URL"));
        assertTrue(r1.equals(r2));
    }

    
    public void testCloning1() {
        AbstractCategoryItemRenderer r1 = new BarRenderer();
        r1.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        AbstractCategoryItemRenderer r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        r1 = new BarRenderer();
        r1.setSeriesItemLabelGenerator(0, 
                new StandardCategoryItemLabelGenerator());
        r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        r1 = new BarRenderer();
        r1.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }
    
    
    public void testCloning2() {
        BarRenderer r1 = new BarRenderer();
        r1.setItemLabelGenerator(new IntervalCategoryItemLabelGenerator());
        BarRenderer r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        r1 = new BarRenderer();
        r1.setSeriesItemLabelGenerator(0, 
                new IntervalCategoryItemLabelGenerator());
        r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        r1 = new BarRenderer();
        r1.setBaseItemLabelGenerator(new IntervalCategoryItemLabelGenerator());
        r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }
    
    
    public void testCloning_LegendItemLabelGenerator() {
        StandardCategorySeriesLabelGenerator generator 
                = new StandardCategorySeriesLabelGenerator("Series {0}");
        BarRenderer r1 = new BarRenderer();
        r1.setLegendItemLabelGenerator(generator);
        BarRenderer r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        
        assertTrue(r1.getLegendItemLabelGenerator() 
                != r2.getLegendItemLabelGenerator());
    }

    
    public void testCloning_LegendItemToolTipGenerator() {
        StandardCategorySeriesLabelGenerator generator 
                = new StandardCategorySeriesLabelGenerator("Series {0}");
        BarRenderer r1 = new BarRenderer();
        r1.setLegendItemToolTipGenerator(generator);
        BarRenderer r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        
        assertTrue(r1.getLegendItemToolTipGenerator() 
                != r2.getLegendItemToolTipGenerator());
    }

    
    public void testCloning_LegendItemURLGenerator() {
        StandardCategorySeriesLabelGenerator generator 
                = new StandardCategorySeriesLabelGenerator("Series {0}");
        BarRenderer r1 = new BarRenderer();
        r1.setLegendItemURLGenerator(generator);
        BarRenderer r2 = null;
        try {
            r2 = (BarRenderer) r1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
        
        
        assertTrue(r1.getLegendItemURLGenerator() 
                != r2.getLegendItemURLGenerator());
    }

}
    
