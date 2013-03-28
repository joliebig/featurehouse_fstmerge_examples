

package org.jfree.chart.plot;

import java.awt.geom.Point2D;

import org.jfree.chart.ChartPanel;


public interface Zoomable {

    
    public boolean isDomainZoomable();
    
    
    public boolean isRangeZoomable();

    
    public PlotOrientation getOrientation();
    
    
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, 
                               Point2D source);

    
    public void zoomDomainAxes(double lowerPercent, double upperPercent, 
                               PlotRenderingInfo state, Point2D source);

    
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, 
                              Point2D source);

    
    public void zoomRangeAxes(double lowerPercent, double upperPercent, 
                              PlotRenderingInfo state, Point2D source);

}
