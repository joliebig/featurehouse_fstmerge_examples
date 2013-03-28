

package org.jfree.chart.renderer;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.xy.XYDataset;


public interface PolarItemRenderer {

    
    public void drawSeries(Graphics2D g2,
                           Rectangle2D dataArea,
                           PlotRenderingInfo info,
                           PolarPlot plot,
                           XYDataset dataset,
                           int seriesIndex);

    
    public void drawAngularGridLines(Graphics2D g2,
                                     PolarPlot plot,
                                     List ticks,
                                     Rectangle2D dataArea);

    
    public void drawRadialGridLines(Graphics2D g2,
                                    PolarPlot plot,
                                    ValueAxis radialAxis,
                                    List ticks,
                                    Rectangle2D dataArea);

    
    public LegendItem getLegendItem(int series);

    
    public PolarPlot getPlot();

    
    public void setPlot(PolarPlot plot);

    
    public void addChangeListener(RendererChangeListener listener);

    
    public void removeChangeListener(RendererChangeListener listener);


}
