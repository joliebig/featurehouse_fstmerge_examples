

package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;


public class HighLowRenderer extends AbstractXYItemRenderer
                             implements XYItemRenderer,
                                        Cloneable,
                                        PublicCloneable,
                                        Serializable {
    
    
    private static final long serialVersionUID = -8135673815876552516L;
    
    
    private boolean drawOpenTicks;

    
    private boolean drawCloseTicks;
    
    
    private transient Paint openTickPaint;
    
    
    private transient Paint closeTickPaint;

    
    public HighLowRenderer() {
        super();
        this.drawOpenTicks = true;
        this.drawCloseTicks = true;
    }

    
    public boolean getDrawOpenTicks() {
        return this.drawOpenTicks;
    }
    
    
    public void setDrawOpenTicks(boolean draw) {
        this.drawOpenTicks = draw;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public boolean getDrawCloseTicks() {
        return this.drawCloseTicks;
    }
    
    
    public void setDrawCloseTicks(boolean draw) {
        this.drawCloseTicks = draw;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public Paint getOpenTickPaint() {
        return this.openTickPaint;
    }
    
    
    public void setOpenTickPaint(Paint paint) {
        this.openTickPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    
    public Paint getCloseTickPaint() {
        return this.closeTickPaint;
    }
    
    
    public void setCloseTickPaint(Paint paint) {
        this.closeTickPaint = paint;
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
        if (!domainAxis.getRange().contains(x)) {
            return;    
        }
        double xx = domainAxis.valueToJava2D(x, dataArea, 
                plot.getDomainAxisEdge());
        
        
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge location = plot.getRangeAxisEdge();

        Paint itemPaint = getItemPaint(series, item);
        Stroke itemStroke = getItemStroke(series, item);
        g2.setPaint(itemPaint);
        g2.setStroke(itemStroke);
        
        if (dataset instanceof OHLCDataset) {
            OHLCDataset hld = (OHLCDataset) dataset;
            
            double yHigh = hld.getHighValue(series, item);
            double yLow = hld.getLowValue(series, item);
            if (!Double.isNaN(yHigh) && !Double.isNaN(yLow)) {
                double yyHigh = rangeAxis.valueToJava2D(yHigh, dataArea, 
                        location);
                double yyLow = rangeAxis.valueToJava2D(yLow, dataArea, 
                        location);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.draw(new Line2D.Double(yyLow, xx, yyHigh, xx));
                    entityArea = new Rectangle2D.Double(Math.min(yyLow, yyHigh),
                            xx - 1.0, Math.abs(yyHigh - yyLow), 2.0);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    g2.draw(new Line2D.Double(xx, yyLow, xx, yyHigh));   
                    entityArea = new Rectangle2D.Double(xx - 1.0, 
                            Math.min(yyLow, yyHigh), 2.0,  
                            Math.abs(yyHigh - yyLow));
                }
            }
            
            double delta = 2.0;
            if (domainAxis.isInverted()) {
                delta = -delta;
            }
            if (getDrawOpenTicks()) {
                double yOpen = hld.getOpenValue(series, item);
                if (!Double.isNaN(yOpen)) {
                    double yyOpen = rangeAxis.valueToJava2D(yOpen, dataArea, 
                            location);
                    if (this.openTickPaint != null) {
                        g2.setPaint(this.openTickPaint);
                    }
                    else {
                        g2.setPaint(itemPaint);
                    }
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        g2.draw(new Line2D.Double(yyOpen, xx + delta, yyOpen, 
                                xx));   
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(xx - delta, yyOpen, xx, 
                                yyOpen));   
                    }
                }
            }
            
            if (getDrawCloseTicks()) {
                double yClose = hld.getCloseValue(series, item);
                if (!Double.isNaN(yClose)) {
                    double yyClose = rangeAxis.valueToJava2D(
                        yClose, dataArea, location);
                    if (this.closeTickPaint != null) {
                        g2.setPaint(this.closeTickPaint);
                    }
                    else {
                        g2.setPaint(itemPaint);
                    }
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        g2.draw(new Line2D.Double(yyClose, xx, yyClose, 
                                xx - delta));   
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(xx, yyClose, xx + delta, 
                                yyClose));   
                    }
                }
            }
  
        }
        else {
            
            
            if (item > 0) {
                double x0 = dataset.getXValue(series, item - 1);
                double y0 = dataset.getYValue(series, item - 1);
                double y = dataset.getYValue(series, item);
                if (Double.isNaN(x0) || Double.isNaN(y0) || Double.isNaN(y)) {
                    return;
                }
                double xx0 = domainAxis.valueToJava2D(x0, dataArea, 
                        plot.getDomainAxisEdge());
                double yy0 = rangeAxis.valueToJava2D(y0, dataArea, location);
                double yy = rangeAxis.valueToJava2D(y, dataArea, location);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    g2.draw(new Line2D.Double(yy0, xx0, yy, xx));
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    g2.draw(new Line2D.Double(xx0, yy0, xx, yy));
                }
            }
        }
        
        
        if (entities != null) {
            String tip = null;
            XYToolTipGenerator generator = getToolTipGenerator(series, item);
            if (generator != null) {
                tip = generator.generateToolTip(dataset, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(dataset, series, item);
            }
            XYItemEntity entity = new XYItemEntity(entityArea, dataset, 
                    series, item, tip, url);
            entities.add(entity);
        }

    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HighLowRenderer)) {
            return false;
        }
        HighLowRenderer that = (HighLowRenderer) obj;
        if (this.drawOpenTicks != that.drawOpenTicks) {
            return false;
        }
        if (this.drawCloseTicks != that.drawCloseTicks) {
            return false;
        }
        if (!PaintUtilities.equal(this.openTickPaint, that.openTickPaint)) {
            return false;
        }
        if (!PaintUtilities.equal(this.closeTickPaint, that.closeTickPaint)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }
    
    
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.openTickPaint = SerialUtilities.readPaint(stream);
        this.closeTickPaint = SerialUtilities.readPaint(stream);
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.openTickPaint, stream);
        SerialUtilities.writePaint(this.closeTickPaint, stream);
    }

}
