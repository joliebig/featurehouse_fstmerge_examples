

package org.jfree.chart.event;

import org.jfree.chart.plot.Plot;


public class PlotChangeEvent extends ChartChangeEvent {

    
    private Plot plot;

    
    public PlotChangeEvent(Plot plot) {
        super(plot);
        this.plot = plot;
    }

    
    public Plot getPlot() {
        return this.plot;
    }

}
