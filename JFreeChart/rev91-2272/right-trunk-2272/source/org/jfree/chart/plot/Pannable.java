

package org.jfree.chart.plot;

import java.awt.geom.Point2D;

import org.jfree.chart.ChartPanel;


public interface Pannable {

    
    public PlotOrientation getOrientation();

    
    public boolean isDomainPannable();

    
    public boolean isRangePannable();

    
    public void panDomainAxes(double percent, PlotRenderingInfo info,
            Point2D source);

    
    public void panRangeAxes(double percent, PlotRenderingInfo info,
            Point2D source);

}
