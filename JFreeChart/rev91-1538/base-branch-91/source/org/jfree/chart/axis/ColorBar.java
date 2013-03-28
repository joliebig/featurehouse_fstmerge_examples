

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.plot.ColorPalette;
import org.jfree.chart.plot.ContourPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.RainbowPalette;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.ui.RectangleEdge;


public class ColorBar implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -2101776212647268103L;
    
    
    public static final int DEFAULT_COLORBAR_THICKNESS = 0;

    
    public static final double DEFAULT_COLORBAR_THICKNESS_PERCENT = 0.10;

    
    public static final int DEFAULT_OUTERGAP = 2;

    
    private ValueAxis axis;
    
    
    private int colorBarThickness = DEFAULT_COLORBAR_THICKNESS;

    
    private double colorBarThicknessPercent 
            = DEFAULT_COLORBAR_THICKNESS_PERCENT;

    
    private ColorPalette colorPalette = null;

    
    private int colorBarLength = 0; 

    
    private int outerGap;

    
    public ColorBar(String label) {
   
        NumberAxis a = new NumberAxis(label);
        a.setAutoRangeIncludesZero(false);
        this.axis = a;
        this.axis.setLowerMargin(0.0);
        this.axis.setUpperMargin(0.0);

        this.colorPalette = new RainbowPalette();
        this.colorBarThickness = DEFAULT_COLORBAR_THICKNESS;
        this.colorBarThicknessPercent = DEFAULT_COLORBAR_THICKNESS_PERCENT;
        this.outerGap = DEFAULT_OUTERGAP;
        this.colorPalette.setMinZ(this.axis.getRange().getLowerBound());
        this.colorPalette.setMaxZ(this.axis.getRange().getUpperBound());

    }

    
    public void configure(ContourPlot plot) {
        double minZ = plot.getDataset().getMinZValue();
        double maxZ = plot.getDataset().getMaxZValue();
        setMinimumValue(minZ);
        setMaximumValue(maxZ);
    }
    
    
    public ValueAxis getAxis() {
        return this.axis;
    }
    
    
    public void setAxis(ValueAxis axis) {
        this.axis = axis;
    }
    
    
    public void autoAdjustRange() {
        this.axis.autoAdjustRange();
        this.colorPalette.setMinZ(this.axis.getLowerBound());
        this.colorPalette.setMaxZ(this.axis.getUpperBound());
    }

    
    public double draw(Graphics2D g2, double cursor,
                       Rectangle2D plotArea, Rectangle2D dataArea, 
                       Rectangle2D reservedArea, RectangleEdge edge) {


        Rectangle2D colorBarArea = null;
        
        double thickness = calculateBarThickness(dataArea, edge);
        if (this.colorBarThickness > 0) {
            thickness = this.colorBarThickness;  
        }

        double length = 0.0;
        if (RectangleEdge.isLeftOrRight(edge)) {
            length = dataArea.getHeight();
        }
        else {
            length = dataArea.getWidth();
        }
        
        if (this.colorBarLength > 0) {
            length = this.colorBarLength;
        }

        if (edge == RectangleEdge.BOTTOM) {
            colorBarArea = new Rectangle2D.Double(dataArea.getX(), 
                    plotArea.getMaxY() + this.outerGap, length, thickness);
        }
        else if (edge == RectangleEdge.TOP) {
            colorBarArea = new Rectangle2D.Double(dataArea.getX(), 
                    reservedArea.getMinY() + this.outerGap, length, thickness);
        }
        else if (edge == RectangleEdge.LEFT) {
            colorBarArea = new Rectangle2D.Double(plotArea.getX() - thickness 
                    - this.outerGap, dataArea.getMinY(), thickness, length);
        }
        else if (edge == RectangleEdge.RIGHT) {
            colorBarArea = new Rectangle2D.Double(plotArea.getMaxX() 
                    + this.outerGap, dataArea.getMinY(), thickness, length);
        }
        
        
        this.axis.refreshTicks(g2, new AxisState(), colorBarArea, edge);

        drawColorBar(g2, colorBarArea, edge);

        AxisState state = null;
        if (edge == RectangleEdge.TOP) {
            cursor = colorBarArea.getMinY();
            state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, 
                    RectangleEdge.TOP, null);
        } 
        else if (edge == RectangleEdge.BOTTOM) {
            cursor = colorBarArea.getMaxY();
            state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, 
                    RectangleEdge.BOTTOM, null);
        } 
        else if (edge == RectangleEdge.LEFT) {
            cursor = colorBarArea.getMinX();
            state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, 
                    RectangleEdge.LEFT, null);
        } 
        else if (edge == RectangleEdge.RIGHT) {
            cursor = colorBarArea.getMaxX();
            state = this.axis.draw(g2, cursor, reservedArea, colorBarArea, 
                    RectangleEdge.RIGHT, null);
        }
        return state.getCursor();
        
    }

    
    public void drawColorBar(Graphics2D g2, Rectangle2D colorBarArea, 
                             RectangleEdge edge) {

        Object antiAlias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_OFF);

        
        

        Stroke strokeSaved = g2.getStroke();
        g2.setStroke(new BasicStroke(1.0f));

        if (RectangleEdge.isTopOrBottom(edge)) {
            double y1 = colorBarArea.getY();
            double y2 = colorBarArea.getMaxY();
            double xx = colorBarArea.getX();
            Line2D line = new Line2D.Double();
            while (xx <= colorBarArea.getMaxX()) {
                double value = this.axis.java2DToValue(xx, colorBarArea, edge);
                line.setLine(xx, y1, xx, y2);
                g2.setPaint(getPaint(value));
                g2.draw(line);
                xx += 1;
            }
        }
        else {
            double y1 = colorBarArea.getX();
            double y2 = colorBarArea.getMaxX();
            double xx = colorBarArea.getY();
            Line2D line = new Line2D.Double();
            while (xx <= colorBarArea.getMaxY()) {
                double value = this.axis.java2DToValue(xx, colorBarArea, edge);
                line.setLine(y1, xx, y2, xx);
                g2.setPaint(getPaint(value));
                g2.draw(line);
                xx += 1;
            }            
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
        g2.setStroke(strokeSaved);

    }

    
    public ColorPalette getColorPalette() {
        return this.colorPalette;
    }

    
    public Paint getPaint(double value) {
        return this.colorPalette.getPaint(value);
    }

    
    public void setColorPalette(ColorPalette palette) {
        this.colorPalette = palette;
    }

    
    public void setMaximumValue(double value) {
        this.colorPalette.setMaxZ(value);
        this.axis.setUpperBound(value);
    }

    
    public void setMinimumValue(double value) {
        this.colorPalette.setMinZ(value);
        this.axis.setLowerBound(value);
    }

    
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, 
                                  Rectangle2D plotArea,
                                  Rectangle2D dataArea, RectangleEdge edge, 
                                  AxisSpace space) {

        AxisSpace result = this.axis.reserveSpace(g2, plot, plotArea, edge, 
                space);
        double thickness = calculateBarThickness(dataArea, edge);
        result.add(thickness + 2 * this.outerGap, edge);
        return result;

    }
    
    
    private double calculateBarThickness(Rectangle2D plotArea, 
                                         RectangleEdge edge) {
        double result = 0.0;
        if (RectangleEdge.isLeftOrRight(edge)) {
            result = plotArea.getWidth() * this.colorBarThicknessPercent;
        }
        else {
            result = plotArea.getHeight() * this.colorBarThicknessPercent;
        }
        return result;  
    }

    
    public Object clone() throws CloneNotSupportedException {
    
        ColorBar clone = (ColorBar) super.clone();
        clone.axis = (ValueAxis) this.axis.clone();
        return clone;
            
    }
    
    
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColorBar)) {
            return false;   
        }
        ColorBar that = (ColorBar) obj;
        if (!this.axis.equals(that.axis)) {
            return false;
        }
        if (this.colorBarThickness != that.colorBarThickness) {
            return false;
        }
        if (this.colorBarThicknessPercent != that.colorBarThicknessPercent) {
            return false;
        }
        if (!this.colorPalette.equals(that.colorPalette)) {
            return false;
        }
        if (this.colorBarLength != that.colorBarLength) {
            return false;
        }
        if (this.outerGap != that.outerGap) {
            return false;
        }
        return true;
        
    }
    
    
    public int hashCode() {
        return this.axis.hashCode();
    }
    
}
