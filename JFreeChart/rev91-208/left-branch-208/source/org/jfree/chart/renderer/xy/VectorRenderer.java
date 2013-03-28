

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.VectorXYDataset;
import org.jfree.data.xy.XYDataset;


public class VectorRenderer extends AbstractXYItemRenderer 
        implements XYItemRenderer, Cloneable, Serializable {
    
    private double baseLength = 0.10;
    
    private double headLength = 0.14;
    
    
    
    public VectorRenderer() {
    }
    
    
    public Range findDomainBounds(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");   
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        double lvalue;
        double uvalue;
        if (dataset instanceof VectorXYDataset) {
            VectorXYDataset vdataset = (VectorXYDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double delta = vdataset.getVectorXValue(series, item);
                    if (delta < 0.0) {
                        uvalue = vdataset.getXValue(series, item);
                        lvalue = uvalue + delta;
                    }
                    else {
                        lvalue = vdataset.getXValue(series, item);
                        uvalue = lvalue + delta;
                    }
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        else {
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    lvalue = dataset.getXValue(series, item);
                    uvalue = lvalue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");   
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        double lvalue;
        double uvalue;
        if (dataset instanceof VectorXYDataset) {
            VectorXYDataset vdataset = (VectorXYDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double delta = vdataset.getVectorYValue(series, item);
                    if (delta < 0.0) {
                        uvalue = vdataset.getYValue(series, item);
                        lvalue = uvalue + delta;
                    }
                    else {
                        lvalue = vdataset.getYValue(series, item);
                        uvalue = lvalue + delta;
                    }
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        else {
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    lvalue = dataset.getYValue(series, item);
                    uvalue = lvalue;
                    minimum = Math.min(minimum, lvalue);
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum > maximum) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    
    public void drawItem(Graphics2D g2, XYItemRendererState state, 
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, 
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, 
            int series, int item, CrosshairState crosshairState, int pass) {
        
        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        double dx = 0.0;
        double dy = 0.0;
        if (dataset instanceof VectorXYDataset) {
            dx = ((VectorXYDataset) dataset).getVectorXValue(series, item);
            dy = ((VectorXYDataset) dataset).getVectorYValue(series, item);
        }
        double xx0 = domainAxis.valueToJava2D(x, dataArea, 
                plot.getDomainAxisEdge());
        double yy0 = rangeAxis.valueToJava2D(y, dataArea, 
                plot.getRangeAxisEdge());
        double xx1 = domainAxis.valueToJava2D(x + dx, dataArea, 
                plot.getDomainAxisEdge());
        double yy1 = rangeAxis.valueToJava2D(y + dy, dataArea, 
                plot.getRangeAxisEdge());
        Line2D line;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.equals(PlotOrientation.HORIZONTAL)) {
            line = new Line2D.Double(yy0, xx0, yy1, xx1);
        }
        else {
            line = new Line2D.Double(xx0, yy0, xx1, yy1);
        }
        g2.setPaint(getItemPaint(series, item));
        g2.setStroke(getItemStroke(series, item));
        g2.draw(line);
        
        
        double dxx = (xx1 - xx0);
        double dyy = (yy1 - yy0);
        double bx = xx0 + (1.0 - this.baseLength) * dxx;
        double by = yy0 + (1.0 - this.baseLength) * dyy;
        
        double cx = xx0 + (1.0 - this.headLength) * dxx;
        double cy = yy0 + (1.0 - this.headLength) * dyy;
 
        double angle = 0.0;
        if (dxx != 0.0) {
            angle = Math.PI / 2.0 - Math.atan(dyy / dxx);
        }
        double deltaX = 2.0 * Math.cos(angle);
        double deltaY = 2.0 * Math.sin(angle);
        
        double leftx = cx + deltaX;
        double lefty = cy - deltaY;
        double rightx = cx - deltaX;
        double righty = cy + deltaY;
        
        GeneralPath p = new GeneralPath();
        p.moveTo((float) xx1, (float) yy1);
        p.lineTo((float) rightx, (float) righty);
        p.lineTo((float) bx, (float) by);
        p.lineTo((float) leftx, (float) lefty);
        p.closePath();
        g2.draw(p);
        
        
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VectorRenderer)) {
            return false;
        }
        VectorRenderer that = (VectorRenderer) obj;
        if (this.baseLength != that.baseLength) {
            return false;
        }
        if (this.headLength != that.headLength) {
            return false;
        }
        return super.equals(obj);
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
