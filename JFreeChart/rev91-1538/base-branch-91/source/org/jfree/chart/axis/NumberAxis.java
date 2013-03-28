

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ObjectUtilities;


public class NumberAxis extends ValueAxis implements Cloneable, Serializable {

    
    private static final long serialVersionUID = 2805933088476185789L;
    
    
    public static final boolean DEFAULT_AUTO_RANGE_INCLUDES_ZERO = true;

    
    public static final boolean DEFAULT_AUTO_RANGE_STICKY_ZERO = true;

    
    public static final NumberTickUnit DEFAULT_TICK_UNIT = new NumberTickUnit(
            1.0, new DecimalFormat("0"));

    
    public static final boolean DEFAULT_VERTICAL_TICK_LABELS = false;

    
    private RangeType rangeType;
    
    
    private boolean autoRangeIncludesZero;

    
    private boolean autoRangeStickyZero;

    
    private NumberTickUnit tickUnit;

    
    private NumberFormat numberFormatOverride;

    
    private MarkerAxisBand markerBand;

    
    public NumberAxis() {
        this(null);    
    }
    
    
    public NumberAxis(String label) {
        super(label, NumberAxis.createStandardTickUnits());
        this.rangeType = RangeType.FULL;
        this.autoRangeIncludesZero = DEFAULT_AUTO_RANGE_INCLUDES_ZERO;
        this.autoRangeStickyZero = DEFAULT_AUTO_RANGE_STICKY_ZERO;
        this.tickUnit = DEFAULT_TICK_UNIT;
        this.numberFormatOverride = null;
        this.markerBand = null;
    }
    
    
    public RangeType getRangeType() {
        return this.rangeType;   
    }
    
    
    public void setRangeType(RangeType rangeType) {
        if (rangeType == null) {
            throw new IllegalArgumentException("Null 'rangeType' argument.");   
        }
        this.rangeType = rangeType;
        notifyListeners(new AxisChangeEvent(this));
    }
    
    
    public boolean getAutoRangeIncludesZero() {
        return this.autoRangeIncludesZero;
    }

    
    public void setAutoRangeIncludesZero(boolean flag) {
        if (this.autoRangeIncludesZero != flag) {
            this.autoRangeIncludesZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    
    public boolean getAutoRangeStickyZero() {
        return this.autoRangeStickyZero;
    }

    
    public void setAutoRangeStickyZero(boolean flag) {
        if (this.autoRangeStickyZero != flag) {
            this.autoRangeStickyZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));
        }
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

    
    public MarkerAxisBand getMarkerBand() {
        return this.markerBand;
    }

    
    public void setMarkerBand(MarkerAxisBand band) {
        this.markerBand = band;
        notifyListeners(new AxisChangeEvent(this));
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
            if (this.rangeType == RangeType.POSITIVE) {
                lower = Math.max(0.0, lower);
                upper = Math.max(0.0, upper);
            }
            else if (this.rangeType == RangeType.NEGATIVE) {
                lower = Math.min(0.0, lower);
                upper = Math.min(0.0, upper);                   
            }
            
            if (getAutoRangeIncludesZero()) {
                lower = Math.min(lower, 0.0);
                upper = Math.max(upper, 0.0);
            }
            double range = upper - lower;

            
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                    if (lower == upper) { 
                        double adjust = Math.abs(lower) / 10.0;
                        lower = lower - adjust;
                        upper = upper + adjust;
                    }
                    if (this.rangeType == RangeType.POSITIVE) {
                        if (lower < 0.0) {
                            upper = upper - lower;
                            lower = 0.0;
                        }
                    }
                    else if (this.rangeType == RangeType.NEGATIVE) {
                        if (upper > 0.0) {
                            lower = lower - upper;
                            upper = 0.0;
                        }
                    }
                }

                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + getUpperMargin() * range);
                    }
                    else {
                        upper = upper + getUpperMargin() * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - getLowerMargin() * range);
                    }
                    else {
                        lower = lower - getLowerMargin() * range;
                    }
                }
                else {
                    upper = upper + getUpperMargin() * range;
                    lower = lower - getLowerMargin() * range;
                }
            }

            setRange(new Range(lower, upper), false, false);
        }

    }

    
    public double valueToJava2D(double value, Rectangle2D area, 
                                RectangleEdge edge) {
        
        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();

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

    
    public double java2DToValue(double java2DValue, Rectangle2D area, 
                                RectangleEdge edge) {
        
        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();

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
        if (isInverted()) {
            return axisMax 
                   - (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
        else {
            return axisMin 
                   + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }

    }

    
    protected double calculateLowestVisibleTickValue() {

        double unit = getTickUnit().getSize();
        double index = Math.ceil(getRange().getLowerBound() / unit);
        return index * unit;

    }

    
    protected double calculateHighestVisibleTickValue() {

        double unit = getTickUnit().getSize();
        double index = Math.floor(getRange().getUpperBound() / unit);
        return index * unit;

    }

    
    protected int calculateVisibleTickCount() {

        double unit = getTickUnit().getSize();
        Range range = getRange();
        return (int) (Math.floor(range.getUpperBound() / unit)
                      - Math.ceil(range.getLowerBound() / unit) + 1);

    }

    
    public AxisState draw(Graphics2D g2, 
                          double cursor,
                          Rectangle2D plotArea, 
                          Rectangle2D dataArea, 
                          RectangleEdge edge,
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

    
    public static TickUnitSource createStandardTickUnits() {

        TickUnits units = new TickUnits();
        DecimalFormat df0 = new DecimalFormat("0.00000000");
        DecimalFormat df1 = new DecimalFormat("0.0000000");
        DecimalFormat df2 = new DecimalFormat("0.000000");
        DecimalFormat df3 = new DecimalFormat("0.00000");
        DecimalFormat df4 = new DecimalFormat("0.0000");
        DecimalFormat df5 = new DecimalFormat("0.000");
        DecimalFormat df6 = new DecimalFormat("0.00");
        DecimalFormat df7 = new DecimalFormat("0.0");
        DecimalFormat df8 = new DecimalFormat("#,##0");
        DecimalFormat df9 = new DecimalFormat("#,###,##0");
        DecimalFormat df10 = new DecimalFormat("#,###,###,##0");
        
        
        
        units.add(new NumberTickUnit(0.0000001, df1));
        units.add(new NumberTickUnit(0.000001, df2));
        units.add(new NumberTickUnit(0.00001, df3));
        units.add(new NumberTickUnit(0.0001, df4));
        units.add(new NumberTickUnit(0.001, df5));
        units.add(new NumberTickUnit(0.01, df6));
        units.add(new NumberTickUnit(0.1, df7));
        units.add(new NumberTickUnit(1, df8));
        units.add(new NumberTickUnit(10, df8));
        units.add(new NumberTickUnit(100, df8));
        units.add(new NumberTickUnit(1000, df8));
        units.add(new NumberTickUnit(10000, df8));
        units.add(new NumberTickUnit(100000, df8));
        units.add(new NumberTickUnit(1000000, df9));
        units.add(new NumberTickUnit(10000000, df9));
        units.add(new NumberTickUnit(100000000, df9));
        units.add(new NumberTickUnit(1000000000, df10));
        units.add(new NumberTickUnit(10000000000.0, df10));
        units.add(new NumberTickUnit(100000000000.0, df10));
        
        units.add(new NumberTickUnit(0.00000025, df0));
        units.add(new NumberTickUnit(0.0000025, df1));
        units.add(new NumberTickUnit(0.000025, df2));
        units.add(new NumberTickUnit(0.00025, df3));
        units.add(new NumberTickUnit(0.0025, df4));
        units.add(new NumberTickUnit(0.025, df5));
        units.add(new NumberTickUnit(0.25, df6));
        units.add(new NumberTickUnit(2.5, df7));
        units.add(new NumberTickUnit(25, df8));
        units.add(new NumberTickUnit(250, df8));
        units.add(new NumberTickUnit(2500, df8));
        units.add(new NumberTickUnit(25000, df8));
        units.add(new NumberTickUnit(250000, df8));
        units.add(new NumberTickUnit(2500000, df9));
        units.add(new NumberTickUnit(25000000, df9));
        units.add(new NumberTickUnit(250000000, df9));
        units.add(new NumberTickUnit(2500000000.0, df10));
        units.add(new NumberTickUnit(25000000000.0, df10));
        units.add(new NumberTickUnit(250000000000.0, df10));

        units.add(new NumberTickUnit(0.0000005, df1));
        units.add(new NumberTickUnit(0.000005, df2));
        units.add(new NumberTickUnit(0.00005, df3));
        units.add(new NumberTickUnit(0.0005, df4));
        units.add(new NumberTickUnit(0.005, df5));
        units.add(new NumberTickUnit(0.05, df6));
        units.add(new NumberTickUnit(0.5, df7));
        units.add(new NumberTickUnit(5L, df8));
        units.add(new NumberTickUnit(50L, df8));
        units.add(new NumberTickUnit(500L, df8));
        units.add(new NumberTickUnit(5000L, df8));
        units.add(new NumberTickUnit(50000L, df8));
        units.add(new NumberTickUnit(500000L, df8));
        units.add(new NumberTickUnit(5000000L, df9));
        units.add(new NumberTickUnit(50000000L, df9));
        units.add(new NumberTickUnit(500000000L, df9));
        units.add(new NumberTickUnit(5000000000L, df10));
        units.add(new NumberTickUnit(50000000000L, df10));
        units.add(new NumberTickUnit(500000000000L, df10));

        return units;

    }

    
    public static TickUnitSource createIntegerTickUnits() {

        TickUnits units = new TickUnits();
        DecimalFormat df0 = new DecimalFormat("0");
        DecimalFormat df1 = new DecimalFormat("#,##0");
        units.add(new NumberTickUnit(1, df0));
        units.add(new NumberTickUnit(2, df0));
        units.add(new NumberTickUnit(5, df0));
        units.add(new NumberTickUnit(10, df0));
        units.add(new NumberTickUnit(20, df0));
        units.add(new NumberTickUnit(50, df0));
        units.add(new NumberTickUnit(100, df0));
        units.add(new NumberTickUnit(200, df0));
        units.add(new NumberTickUnit(500, df0));
        units.add(new NumberTickUnit(1000, df1));
        units.add(new NumberTickUnit(2000, df1));
        units.add(new NumberTickUnit(5000, df1));
        units.add(new NumberTickUnit(10000, df1));
        units.add(new NumberTickUnit(20000, df1));
        units.add(new NumberTickUnit(50000, df1));
        units.add(new NumberTickUnit(100000, df1));
        units.add(new NumberTickUnit(200000, df1));
        units.add(new NumberTickUnit(500000, df1));
        units.add(new NumberTickUnit(1000000, df1));
        units.add(new NumberTickUnit(2000000, df1));
        units.add(new NumberTickUnit(5000000, df1));
        units.add(new NumberTickUnit(10000000, df1));
        units.add(new NumberTickUnit(20000000, df1));
        units.add(new NumberTickUnit(50000000, df1));
        units.add(new NumberTickUnit(100000000, df1));
        units.add(new NumberTickUnit(200000000, df1));
        units.add(new NumberTickUnit(500000000, df1));
        units.add(new NumberTickUnit(1000000000, df1));
        units.add(new NumberTickUnit(2000000000, df1));
        units.add(new NumberTickUnit(5000000000.0, df1));
        units.add(new NumberTickUnit(10000000000.0, df1));

        return units;

    }

    
    public static TickUnitSource createStandardTickUnits(Locale locale) {

        TickUnits units = new TickUnits();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

        
        
        units.add(new NumberTickUnit(0.0000001,    numberFormat));
        units.add(new NumberTickUnit(0.000001,     numberFormat));
        units.add(new NumberTickUnit(0.00001,      numberFormat));
        units.add(new NumberTickUnit(0.0001,       numberFormat));
        units.add(new NumberTickUnit(0.001,        numberFormat));
        units.add(new NumberTickUnit(0.01,         numberFormat));
        units.add(new NumberTickUnit(0.1,          numberFormat));
        units.add(new NumberTickUnit(1,            numberFormat));
        units.add(new NumberTickUnit(10,           numberFormat));
        units.add(new NumberTickUnit(100,          numberFormat));
        units.add(new NumberTickUnit(1000,         numberFormat));
        units.add(new NumberTickUnit(10000,        numberFormat));
        units.add(new NumberTickUnit(100000,       numberFormat));
        units.add(new NumberTickUnit(1000000,      numberFormat));
        units.add(new NumberTickUnit(10000000,     numberFormat));
        units.add(new NumberTickUnit(100000000,    numberFormat));
        units.add(new NumberTickUnit(1000000000,   numberFormat));
        units.add(new NumberTickUnit(10000000000.0,   numberFormat));

        units.add(new NumberTickUnit(0.00000025,   numberFormat));
        units.add(new NumberTickUnit(0.0000025,    numberFormat));
        units.add(new NumberTickUnit(0.000025,     numberFormat));
        units.add(new NumberTickUnit(0.00025,      numberFormat));
        units.add(new NumberTickUnit(0.0025,       numberFormat));
        units.add(new NumberTickUnit(0.025,        numberFormat));
        units.add(new NumberTickUnit(0.25,         numberFormat));
        units.add(new NumberTickUnit(2.5,          numberFormat));
        units.add(new NumberTickUnit(25,           numberFormat));
        units.add(new NumberTickUnit(250,          numberFormat));
        units.add(new NumberTickUnit(2500,         numberFormat));
        units.add(new NumberTickUnit(25000,        numberFormat));
        units.add(new NumberTickUnit(250000,       numberFormat));
        units.add(new NumberTickUnit(2500000,      numberFormat));
        units.add(new NumberTickUnit(25000000,     numberFormat));
        units.add(new NumberTickUnit(250000000,    numberFormat));
        units.add(new NumberTickUnit(2500000000.0,   numberFormat));
        units.add(new NumberTickUnit(25000000000.0,   numberFormat));

        units.add(new NumberTickUnit(0.0000005,    numberFormat));
        units.add(new NumberTickUnit(0.000005,     numberFormat));
        units.add(new NumberTickUnit(0.00005,      numberFormat));
        units.add(new NumberTickUnit(0.0005,       numberFormat));
        units.add(new NumberTickUnit(0.005,        numberFormat));
        units.add(new NumberTickUnit(0.05,         numberFormat));
        units.add(new NumberTickUnit(0.5,          numberFormat));
        units.add(new NumberTickUnit(5L,           numberFormat));
        units.add(new NumberTickUnit(50L,          numberFormat));
        units.add(new NumberTickUnit(500L,         numberFormat));
        units.add(new NumberTickUnit(5000L,        numberFormat));
        units.add(new NumberTickUnit(50000L,       numberFormat));
        units.add(new NumberTickUnit(500000L,      numberFormat));
        units.add(new NumberTickUnit(5000000L,     numberFormat));
        units.add(new NumberTickUnit(50000000L,    numberFormat));
        units.add(new NumberTickUnit(500000000L,   numberFormat));
        units.add(new NumberTickUnit(5000000000L,  numberFormat));
        units.add(new NumberTickUnit(50000000000L,  numberFormat));

        return units;

    }

    
    public static TickUnitSource createIntegerTickUnits(Locale locale) {

        TickUnits units = new TickUnits();

        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);

        units.add(new NumberTickUnit(1,              numberFormat));
        units.add(new NumberTickUnit(2,              numberFormat));
        units.add(new NumberTickUnit(5,              numberFormat));
        units.add(new NumberTickUnit(10,             numberFormat));
        units.add(new NumberTickUnit(20,             numberFormat));
        units.add(new NumberTickUnit(50,             numberFormat));
        units.add(new NumberTickUnit(100,            numberFormat));
        units.add(new NumberTickUnit(200,            numberFormat));
        units.add(new NumberTickUnit(500,            numberFormat));
        units.add(new NumberTickUnit(1000,           numberFormat));
        units.add(new NumberTickUnit(2000,           numberFormat));
        units.add(new NumberTickUnit(5000,           numberFormat));
        units.add(new NumberTickUnit(10000,          numberFormat));
        units.add(new NumberTickUnit(20000,          numberFormat));
        units.add(new NumberTickUnit(50000,          numberFormat));
        units.add(new NumberTickUnit(100000,         numberFormat));
        units.add(new NumberTickUnit(200000,         numberFormat));
        units.add(new NumberTickUnit(500000,         numberFormat));
        units.add(new NumberTickUnit(1000000,        numberFormat));
        units.add(new NumberTickUnit(2000000,        numberFormat));
        units.add(new NumberTickUnit(5000000,        numberFormat));
        units.add(new NumberTickUnit(10000000,       numberFormat));
        units.add(new NumberTickUnit(20000000,       numberFormat));
        units.add(new NumberTickUnit(50000000,       numberFormat));
        units.add(new NumberTickUnit(100000000,      numberFormat));
        units.add(new NumberTickUnit(200000000,      numberFormat));
        units.add(new NumberTickUnit(500000000,      numberFormat));
        units.add(new NumberTickUnit(1000000000,     numberFormat));
        units.add(new NumberTickUnit(2000000000,     numberFormat));
        units.add(new NumberTickUnit(5000000000.0,   numberFormat));
        units.add(new NumberTickUnit(10000000000.0,  numberFormat));

        return units;

    }

    
    protected double estimateMaximumTickLabelHeight(Graphics2D g2) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        result += tickLabelFont.getLineMetrics("123", frc).getHeight();
        return result;
        
    }

    
    protected double estimateMaximumTickLabelWidth(Graphics2D g2, 
                                                   TickUnit unit) {

        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();

        if (isVerticalTickLabels()) {
            
            
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = getTickLabelFont().getLineMetrics("0", frc);
            result += lm.getHeight();
        }
        else {
            
            FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
            Range range = getRange();
            double lower = range.getLowerBound();
            double upper = range.getUpperBound();
            String lowerStr = "";
            String upperStr = "";
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            }
            else {
                lowerStr = unit.valueToString(lower);
                upperStr = unit.valueToString(upper);                
            }
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }

        return result;

    }
    
    
    protected void selectAutoTickUnit(Graphics2D g2,
                                      Rectangle2D dataArea,
                                      RectangleEdge edge) {

        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }

    }

    
   protected void selectHorizontalAutoTickUnit(Graphics2D g2,
                                               Rectangle2D dataArea,
                                               RectangleEdge edge) {

        double tickLabelWidth = estimateMaximumTickLabelWidth(
            g2, getTickUnit()
        );

        
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double unit1Width = lengthToJava2D(unit1.getSize(), dataArea, edge);

        
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();

        NumberTickUnit unit2 
            = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double unit2Width = lengthToJava2D(unit2.getSize(), dataArea, edge);

        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }

        setTickUnit(unit2, false, false);

    }

    
    protected void selectVerticalAutoTickUnit(Graphics2D g2, 
                                              Rectangle2D dataArea, 
                                              RectangleEdge edge) {

        double tickLabelHeight = estimateMaximumTickLabelHeight(g2);

        
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double unitHeight = lengthToJava2D(unit1.getSize(), dataArea, edge);

        
        double guess = (tickLabelHeight / unitHeight) * unit1.getSize();
        
        NumberTickUnit unit2 
            = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double unit2Height = lengthToJava2D(unit2.getSize(), dataArea, edge);

        tickLabelHeight = estimateMaximumTickLabelHeight(g2);
        if (tickLabelHeight > unit2Height) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }

        setTickUnit(unit2, false, false);

    }
    
    
    public List refreshTicks(Graphics2D g2, 
                             AxisState state,
                             Rectangle2D dataArea,
                             RectangleEdge edge) {

        List result = new java.util.ArrayList();
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;

    }

    
    protected List refreshTicksHorizontal(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          RectangleEdge edge) {

        List result = new java.util.ArrayList();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        angle = Math.PI / 2.0;
                    }
                    else {
                        angle = -Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.TOP) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    }
                    else {
                        anchor = TextAnchor.TOP_CENTER;
                        rotationAnchor = TextAnchor.TOP_CENTER;
                    }
                }

                Tick tick = new NumberTick(
                    new Double(currentTickValue), tickLabel, anchor, 
                    rotationAnchor, angle
                );
                result.add(tick);
            }
        }
        return result;

    }

    
    protected List refreshTicksVertical(Graphics2D g2,
                                        Rectangle2D dataArea,
                                        RectangleEdge edge) {

        List result = new java.util.ArrayList();
        result.clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                }
                else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }

                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    if (edge == RectangleEdge.LEFT) { 
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = -Math.PI / 2.0;
                    }
                    else {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = Math.PI / 2.0;
                    }
                }
                else {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    }
                    else {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }

                Tick tick = new NumberTick(
                    new Double(currentTickValue), tickLabel, anchor, 
                    rotationAnchor, angle
                );
                result.add(tick);
            }
        }
        return result;

    }
    
    
    public Object clone() throws CloneNotSupportedException {
        NumberAxis clone = (NumberAxis) super.clone();
        if (this.numberFormatOverride != null) {
            clone.numberFormatOverride 
                = (NumberFormat) this.numberFormatOverride.clone();
        }
        return clone;
    }

        
    public boolean equals(Object obj) {           
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumberAxis)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        NumberAxis that = (NumberAxis) obj;        
        if (this.autoRangeIncludesZero != that.autoRangeIncludesZero) {
            return false;
        }
        if (this.autoRangeStickyZero != that.autoRangeStickyZero) {
            return false;
        }
        if (!ObjectUtilities.equal(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.numberFormatOverride, 
                that.numberFormatOverride)) {
            return false;
        }
        if (!this.rangeType.equals(that.rangeType)) {
            return false;
        }
        return true; 
    }
    
    
    public int hashCode() {
        if (getLabel() != null) {
            return getLabel().hashCode();
        }
        else {
            return 0;
        }
    }

}
