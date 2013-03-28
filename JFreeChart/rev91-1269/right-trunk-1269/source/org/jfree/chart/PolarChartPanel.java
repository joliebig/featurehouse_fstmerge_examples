

package org.jfree.chart;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PolarPlot;


public class PolarChartPanel extends ChartPanel {

    
    
    

    
    private static final String POLAR_ZOOM_IN_ACTION_COMMAND = "Polar Zoom In";

    
    private static final String POLAR_ZOOM_OUT_ACTION_COMMAND
        = "Polar Zoom Out";

    
    private static final String POLAR_AUTO_RANGE_ACTION_COMMAND
        = "Polar Auto Range";

    
    
    

    
    
    
    
    public PolarChartPanel(JFreeChart chart) {
        this(chart, true);
    }

    
    public PolarChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
        checkChart(chart);
        setMinimumDrawWidth(200);
        setMinimumDrawHeight(200);
        setMaximumDrawWidth(2000);
        setMaximumDrawHeight(2000);
    }

    
    
    
    
    public void setChart(JFreeChart chart) {
        checkChart(chart);
        super.setChart(chart);
    }

    
    protected JPopupMenu createPopupMenu(boolean properties,
                                         boolean save,
                                         boolean print,
                                         boolean zoom) {

       JPopupMenu result = super.createPopupMenu(properties, save, print, zoom);
       int zoomInIndex  = getPopupMenuItem(result, "Zoom In");
       int zoomOutIndex = getPopupMenuItem(result, "Zoom Out");
       int autoIndex     = getPopupMenuItem(result, "Auto Range");
       if (zoom) {
           JMenuItem zoomIn = new JMenuItem("Zoom In");
           zoomIn.setActionCommand(POLAR_ZOOM_IN_ACTION_COMMAND);
           zoomIn.addActionListener(this);

           JMenuItem zoomOut = new JMenuItem("Zoom Out");
           zoomOut.setActionCommand(POLAR_ZOOM_OUT_ACTION_COMMAND);
           zoomOut.addActionListener(this);

           JMenuItem auto = new JMenuItem("Auto Range");
           auto.setActionCommand(POLAR_AUTO_RANGE_ACTION_COMMAND);
           auto.addActionListener(this);

           if (zoomInIndex != -1) {
               result.remove(zoomInIndex);
           }
           else {
               zoomInIndex = result.getComponentCount() - 1;
           }
           result.add(zoomIn, zoomInIndex);
           if (zoomOutIndex != -1) {
               result.remove(zoomOutIndex);
           }
           else {
               zoomOutIndex = zoomInIndex + 1;
           }
           result.add(zoomOut, zoomOutIndex);
           if (autoIndex != -1) {
               result.remove(autoIndex);
           }
           else {
               autoIndex = zoomOutIndex + 1;
           }
           result.add(auto, autoIndex);
       }
       return result;
    }

    
    public void actionPerformed(ActionEvent event) {
       String command = event.getActionCommand();

       if (command.equals(POLAR_ZOOM_IN_ACTION_COMMAND)) {
           PolarPlot plot = (PolarPlot) getChart().getPlot();
           plot.zoom(0.5);
       }
       else if (command.equals(POLAR_ZOOM_OUT_ACTION_COMMAND)) {
           PolarPlot plot = (PolarPlot) getChart().getPlot();
           plot.zoom(2.0);
       }
       else if (command.equals(POLAR_AUTO_RANGE_ACTION_COMMAND)) {
           PolarPlot plot = (PolarPlot) getChart().getPlot();
           plot.getAxis().setAutoRange(true);
       }
       else {
           super.actionPerformed(event);
       }
    }

    
    
    

    
    
    

    
    private void checkChart(JFreeChart chart) {
        Plot plot = chart.getPlot();
        if (!(plot instanceof PolarPlot)) {
            throw new IllegalArgumentException("plot is not a PolarPlot");
       }
    }

    
    private int getPopupMenuItem(JPopupMenu menu, String text) {
        int index = -1;
        for (int i = 0; (index == -1) && (i < menu.getComponentCount()); i++) {
            Component comp = menu.getComponent(i);
            if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                if (text.equals(item.getText())) {
                    index = i;
                }
            }
       }
       return index;
    }

}
