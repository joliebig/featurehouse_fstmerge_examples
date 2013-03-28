

package org.jfree.chart.junit;

import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.CaretListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;


public class ChartPanelTests extends TestCase
        implements ChartChangeListener, ChartMouseListener {

    private List chartChangeEvents = new java.util.ArrayList();

    
    public void chartChanged(ChartChangeEvent event) {
        this.chartChangeEvents.add(event);
    }

    
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

    
    public void test2502355_zoom() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoom(new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0));
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_zoomInBoth() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomInBoth(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_zoomOutBoth() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomOutBoth(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_restoreAutoBounds() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.restoreAutoBounds();
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_zoomInDomain() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomInDomain(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_zoomInRange() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomInRange(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_zoomOutDomain() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomOutDomain(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_zoomOutRange() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.zoomOutRange(1.0, 2.0);
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_restoreAutoDomainBounds() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.restoreAutoDomainBounds();
        assertEquals(1, this.chartChangeEvents.size());
    }

    
    public void test2502355_restoreAutoRangeBounds() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        JFreeChart chart = ChartFactory.createXYLineChart("TestChart", "X",
                "Y", dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeAxis(1, new NumberAxis("X2"));
        ChartPanel panel = new ChartPanel(chart);
        chart.addChangeListener(this);
        this.chartChangeEvents.clear();
        panel.restoreAutoRangeBounds();
        assertEquals(1, this.chartChangeEvents.size());
    }

}
