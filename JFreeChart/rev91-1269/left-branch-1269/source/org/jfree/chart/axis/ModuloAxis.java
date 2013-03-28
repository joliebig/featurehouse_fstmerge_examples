

package org.jfree.chart.axis;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;


public class ModuloAxis extends NumberAxis {
    
    
    private Range fixedRange;
    
    
    private double displayStart;
    
    
    private double displayEnd;
    
    
    public ModuloAxis(String label, Range fixedRange) {
        super(label);
        this.fixedRange = fixedRange;
        this.displayStart = 270.0;
        this.displayEnd = 90.0;
    }

    
    public double getDisplayStart() {
        return this.displayStart;
    }

    
    public double getDisplayEnd() {
        return this.displayEnd;
    }
    
    
    public void setDisplayRange(double start, double end) {
        this.displayStart = mapValueToFixedRange(start);
        this.displayEnd = mapValueToFixedRange(end);
        if (this.displayStart < this.displayEnd) {
            setRange(this.displayStart, this.displayEnd);
        }
        else {
            setRange(this.displayStart, this.fixedRange.getUpperBound() 
                  + (this.displayEnd - this.fixedRange.getLowerBound()));
        }
        notifyListeners(new AxisChangeEvent(this));        
    }
    
    
    protected void autoAdjustRange() {
        setRange(this.fixedRange, false, false);
    }
    
    
    public double valueToJava2D(double value, Rectangle2D area, 
                                RectangleEdge edge) {
        double result = 0.0;
        double v = mapValueToFixedRange(value);
        if (this.displayStart < this.displayEnd) {  
            result = trans(v, area, edge);
        }
        else {  
            double cutoff = (this.displayStart + this.displayEnd) / 2.0;
            double length1 = this.fixedRange.getUpperBound() 
                             - this.displayStart;
            double length2 = this.displayEnd - this.fixedRange.getLowerBound();
            if (v > cutoff) {
                result = transStart(v, area, edge, length1, length2);
            }
            else {
                result = transEnd(v, area, edge, length1, length2);
            }
        }
        return result;
    }

    
    private double trans(double value, Rectangle2D area, RectangleEdge edge) {
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getX() + area.getWidth();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getMaxY() - area.getHeight();
        }
        if (isInverted()) {
            return max - ((value - this.displayStart) 
                   / (this.displayEnd - this.displayStart)) * (max - min);
        }
        else {
            return min + ((value - this.displayStart) 
                   / (this.displayEnd - this.displayStart)) * (max - min);
        }

    }

    
    private double transStart(double value, Rectangle2D area, 
                              RectangleEdge edge,
                              double length1, double length2) {
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getX() + area.getWidth() * length1 / (length1 + length2);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getMaxY() - area.getHeight() * length1 
                  / (length1 + length2);
        }
        if (isInverted()) {
            return max - ((value - this.displayStart) 
                / (this.fixedRange.getUpperBound() - this.displayStart)) 
                * (max - min);
        }
        else {
            return min + ((value - this.displayStart) 
                / (this.fixedRange.getUpperBound() - this.displayStart)) 
                * (max - min);
        }

    }
    
    
    private double transEnd(double value, Rectangle2D area, RectangleEdge edge,
                            double length1, double length2) {
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            max = area.getMaxX();
            min = area.getMaxX() - area.getWidth() * length2 
                  / (length1 + length2);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            max = area.getMinY();
            min = area.getMinY() + area.getHeight() * length2 
                  / (length1 + length2);
        }
        if (isInverted()) {
            return max - ((value - this.fixedRange.getLowerBound()) 
                    / (this.displayEnd - this.fixedRange.getLowerBound())) 
                    * (max - min);
        }
        else {
            return min + ((value - this.fixedRange.getLowerBound()) 
                    / (this.displayEnd - this.fixedRange.getLowerBound())) 
                    * (max - min);
        }

    }

    
    private double mapValueToFixedRange(double value) {
        double lower = this.fixedRange.getLowerBound();
        double length = this.fixedRange.getLength();
        if (value < lower) {
            return lower + length + ((value - lower) % length);
        }
        else {
            return lower + ((value - lower) % length);
        }
    }
    
    
    public double java2DToValue(double java2DValue, Rectangle2D area, 
                                RectangleEdge edge) {
        double result = 0.0;
        if (this.displayStart < this.displayEnd) {  
            result = super.java2DToValue(java2DValue, area, edge);
        }
        else {  
            
        }
        return result;
    }
    
    
    private double getDisplayLength() {
        if (this.displayStart < this.displayEnd) {
            return (this.displayEnd - this.displayStart);
        }
        else {
            return (this.fixedRange.getUpperBound() - this.displayStart)
                + (this.displayEnd - this.fixedRange.getLowerBound());
        }
    }
    
    
    private double getDisplayCentralValue() {
        return mapValueToFixedRange(
            this.displayStart + (getDisplayLength() / 2)
        );    
    }
    
    
    public void resizeRange(double percent) {
        resizeRange(percent, getDisplayCentralValue());
    }

    
    public void resizeRange(double percent, double anchorValue) {

        if (percent > 0.0) {
            double halfLength = getDisplayLength() * percent / 2;
            setDisplayRange(anchorValue - halfLength, anchorValue + halfLength);
        }
        else {
            setAutoRange(true);
        }

    } 
    
    
    public double lengthToJava2D(double length, Rectangle2D area, 
                                 RectangleEdge edge) {
        double axisLength = 0.0;
        if (this.displayEnd > this.displayStart) {
            axisLength = this.displayEnd - this.displayStart;
        }
        else {
            axisLength = (this.fixedRange.getUpperBound() - this.displayStart) 
                + (this.displayEnd - this.fixedRange.getLowerBound());
        }
        double areaLength = 0.0;
        if (RectangleEdge.isLeftOrRight(edge)) {
            areaLength = area.getHeight();
        }
        else {
            areaLength = area.getWidth();
        }
        return (length / axisLength) * areaLength;
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ModuloAxis)) {
            return false;
        }
        ModuloAxis that = (ModuloAxis) obj;
        if (this.displayStart != that.displayStart) {
            return false;
        }
        if (this.displayEnd != that.displayEnd) {
            return false;
        }
        if (!this.fixedRange.equals(that.fixedRange)) {
            return false;
        }
        return super.equals(obj);
    }
    
}
