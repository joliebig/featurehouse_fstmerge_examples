

package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;


public interface CategoryAnnotation extends Annotation {

    
    public void draw(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea,
                     CategoryAxis domainAxis, ValueAxis rangeAxis,
                     int rendererIndex, PlotRenderingInfo info);

}
