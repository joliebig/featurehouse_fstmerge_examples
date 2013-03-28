

package org.jfree.chart.plot.junit;

import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;


public class MyPlotChangeListener implements PlotChangeListener {
   
    private PlotChangeEvent event;
    
    
    public MyPlotChangeListener() {
        this.event = null;
    }
    
    
    public PlotChangeEvent getEvent() {
        return this.event;   
    }
    
    
    public void setEvent(PlotChangeEvent e) {
        this.event = e;   
    }
    
    
    public void plotChanged(PlotChangeEvent e) {
        this.event = e;
    }
    
}
