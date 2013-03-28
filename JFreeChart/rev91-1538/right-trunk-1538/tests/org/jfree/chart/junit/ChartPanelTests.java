

package org.jfree.chart.junit;

import java.util.EventListener;

import javax.swing.event.CaretListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;


public class ChartPanelTests extends TestCase implements ChartMouseListener {

    
    public static Test suite() {
        return new TestSuite(ChartPanelTests.class);
    }

    
    public ChartPanelTests(String name) {
        super(name);
    }

    
    public void testConstructor1() {
        ChartPanel panel = new ChartPanel(null);
        assertEquals(null, panel.getChart());
    }

    
    public void testSetChart() {
        JFreeChart chart = new JFreeChart(new XYPlot());
        ChartPanel panel = new ChartPanel(chart);
        panel.setChart(null);
        assertEquals(null, panel.getChart());
    }

    
    public void testGetListeners() {
        ChartPanel p = new ChartPanel(null);
        p.addChartMouseListener(this);
        EventListener[] listeners = p.getListeners(ChartMouseListener.class);
        assertEquals(1, listeners.length);
        assertEquals(this, listeners[0]);
        
        listeners = p.getListeners(CaretListener.class);
        assertEquals(0, listeners.length);
        p.removeChartMouseListener(this);
        listeners = p.getListeners(ChartMouseListener.class);
        assertEquals(0, listeners.length);

        
        boolean pass = false;
        try {
            listeners = p.getListeners((Class) null);
        }
        catch (NullPointerException e) {
            pass = true;
        }
        assertTrue(pass);

        
        pass = false;
        try {
            listeners = p.getListeners(Integer.class);
        }
        catch (ClassCastException e) {
            pass = true;
        }
        assertTrue(pass);
    }

    
    public void chartMouseClicked(ChartMouseEvent event) {
        
    }

    
    public void chartMouseMoved(ChartMouseEvent event) {
        
    }

}
