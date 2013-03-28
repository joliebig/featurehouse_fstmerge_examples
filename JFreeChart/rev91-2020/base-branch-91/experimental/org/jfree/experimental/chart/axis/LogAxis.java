

package org.jfree.experimental.chart.axis;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTick;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;








public class LogAxis extends ValueAxis {

    
    private double base = 10.0;
    
    
    private double baseLog = Math.log(10.0);
    
    
    private double smallestValue = 1E-100;
    
    
    private NumberTickUnit tickUnit;
    
    
    private NumberFormat numberFormatOverride;

    
    private int minorTickCount; 
    
    
    public LogAxis() {
        this(null);    
    }
    
    
    public LogAxis(String label) {
        super(label, NumberAxis.createIntegerTickUnits());
        setDefaultAutoRange(new Range(0.01, 1.0));
        this.tickUnit = new NumberTickUnit(1.0);
        this.minorTickCount = 10;
        this.setTickMarksVisible(false);
    }
    
    
    public double getBase() {
        return this.base;
    }
    
    
    public void setBase(double base) {
        if (base <= 1.0) {
            throw new IllegalArgumentException("Requires 'base' > 1.0.");
        }
        this.base = base;
        this.baseLog = Math.log(base);
        notifyListeners(new AxisChangeEvent(this));
    }
    
    
    public double getSmallestValue() {
        return this.smallestValue;
    }
    
    
    public void setSmallestValue(double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException("Requires 'value' > 0.0.");
        }
        this.smallestValue = value;
    }
    
    
    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }
    
    
    public void setTickUnit(NumberTickUnit unit) {
        
        setTickUnit(unit, true, true);
    }

    
    public void setTickUnit(NumberTickUnit unit, boolean notify, 
                            boolean turnOffAutoSelect) {

        if (unit == null) {
            throw new IllegalArgumentException("Null 'unit' argument.");   
        }
        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }

    }
    
    
    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    
    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public int getMinorTickCount() {
        return this.minorTickCount;
    }
    
    
    public void setMinorTickCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Requires 'count' > 0.");
        }
        this.minorTickCount = count;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    
    public double calculateLog(double value) {
        return Math.log(value) / this.baseLog;  
    }
    
    
    public double calculateValue(double log) {
        return Math.pow(this.base, log);
    }
    
    
    public double java2DToValue(double java2DValue, Rectangle2D area, 
            RectangleEdge edge) {
        
        Range range = getRange();
        double axisMin = calculateLog(range.getLowerBound());
        double axisMax = calculateLog(range.getUpperBound());

        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        double log = 0.0;
        if (isInverted()) {
            log = axisMax - (java2DValue - min) / (max - min) 
                    * (axisMax - axisMin);
        }
        else {
            log = axisMin + (java2DValue - min) / (max - min) 
                    * (axisMax - axisMin);
        }
        return calculateValue(log);
    }

    
    public double valueToJava2D(double value, Rectangle2D area, 
            RectangleEdge edge) {
        
        Range range = getRange();
        double axisMin = calculateLog(range.getLowerBound());
        double axisMax = calculateLog(range.getUpperBound());
        value = calculateLog(value);
        
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            max = area.getMinY();
            min = area.getMaxY();
        }
        if (isInverted()) {
            return max 
                   - ((value - axisMin) / (axisMax - axisMin)) * (max - min);
        }
        else {
            return min 
                   + ((value - axisMin) / (axisMax - axisMin)) * (max - min);
        }
    }
    
    
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    
    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot == null) {
            return;  
        }

        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;

            Range r = vap.getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }
            
            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            double range = upper - lower;

            
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = Math.max(upper - fixedAutoRange, this.smallestValue);
            }
            else {
                
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                }

                


            }

            setRange(new Range(lower, upper), false, false);
        }

    }

    
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, 
            Rectangle2D dataArea, RectangleEdge edge, 
            PlotRenderingInfo plotState) {
        
        AxisState state = null;
        
        if (!isVisible()) {
            state = new AxisState(cursor);
            
            
            List ticks = refreshTicks(g2, state, dataArea, edge); 
            state.setTicks(ticks);
            return state;
        }
        state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
        return state;
    }

    
    public List refreshTicks(Graphics2D g2, AxisState state, 
            Rectangle2D dataArea, RectangleEdge edge) {

        List result = new java.util.ArrayList();
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;

    }

    
    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, 
            RectangleEdge edge) {
        Range range = getRange();
        List ticks = new ArrayList();
        double start = Math.floor(calculateLog(getLowerBound()));
        double end = Math.ceil(calculateLog(getUpperBound()));
        double current = start;
        while (current <= end) {
            double v = calculateValue(current);
            if (range.contains(v)) {
                ticks.add(new NumberTick(new Double(v), createTickLabel(v), 
                        TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
            }
            
            double next = Math.pow(this.base, current 
                    + this.tickUnit.getSize());
            for (int i = 1; i < this.minorTickCount; i++) {
                double minorV = v + i * ((next - v) / this.minorTickCount);
                if (range.contains(minorV)) {
                    ticks.add(new NumberTick(new Double(minorV), 
                        "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
            current = current + this.tickUnit.getSize();
        }
        return ticks;
    }
    
    
    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, 
            RectangleEdge edge) {
        Range range = getRange();
        List ticks = new ArrayList();
        double start = Math.floor(calculateLog(getLowerBound()));
        double end = Math.ceil(calculateLog(getUpperBound()));
        double current = start;
        while (current <= end) {
            double v = calculateValue(current);
            if (range.contains(v)) {
                ticks.add(new NumberTick(new Double(v), createTickLabel(v), 
                        TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0));
            }
            
            double next = Math.pow(this.base, current 
                    + this.tickUnit.getSize());
            for (int i = 1; i < this.minorTickCount; i++) {
                double minorV = v + i * ((next - v) / this.minorTickCount);
                if (range.contains(minorV)) {
                    ticks.add(new NumberTick(new Double(minorV), "", 
                            TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0));
                }
            }
            current = current + this.tickUnit.getSize();
        }
        return ticks;
    }

    
    private String createTickLabel(double value) {
        if (this.numberFormatOverride != null) {
            return this.numberFormatOverride.format(value);
        }
        else {
            return this.tickUnit.valueToString(value);
        }
    }

}
