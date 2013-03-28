

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;


public class XYDotRenderer extends AbstractXYItemRenderer 
                           implements XYItemRenderer, 
                                      Cloneable,
                                      PublicCloneable,
                                      Serializable {

    
    private static final long serialVersionUID = -2764344339073566425L;
    
    
    private int dotWidth;
    
    
    private int dotHeight;
    
    
    public XYDotRenderer() {
        super();
        this.dotWidth = 1;
        this.dotHeight = 1;
    }

    
    public int getDotWidth() {
        return this.dotWidth;
    }
    
    
    public void setDotWidth(int w) {
        if (w < 1) {
            throw new IllegalArgumentException("Requires w > 0.");
        }
        this.dotWidth = w;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public int getDotHeight() {
        return this.dotHeight;
    }
    
    
    public void setDotHeight(int h) {
        if (h < 1) {
            throw new IllegalArgumentException("Requires h > 0.");
        }
        this.dotHeight = h;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double adjx = (this.dotWidth - 1) / 2.0;
        double adjy = (this.dotHeight - 1) / 2.0;
        if (!Double.isNaN(y)) {
            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX = domainAxis.valueToJava2D(x, dataArea, 
                    xAxisLocation) - adjx;
            double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation) 
                    - adjy;

            g2.setPaint(getItemPaint(series, item));
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.fillRect((int) transY, (int) transX, this.dotHeight, 
                        this.dotWidth);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                g2.fillRect((int) transX, (int) transY, this.dotWidth, 
                        this.dotHeight);
            }

            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x, y, domainAxisIndex, 
                    rangeAxisIndex, transX, transY, orientation);
        }

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDotRenderer)) {
            return false;
        }
        XYDotRenderer that = (XYDotRenderer) obj;
        if (this.dotWidth != that.dotWidth) {
            return false;
        }
        if (this.dotHeight != that.dotHeight) {
            return false;
        }
        return super.equals(obj);    
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
