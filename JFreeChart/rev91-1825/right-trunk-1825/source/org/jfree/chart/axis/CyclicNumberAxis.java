

package org.jfree.chart.axis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.text.TextUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.RectangleEdge;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.data.Range;


public class CyclicNumberAxis extends NumberAxis {

    
    static final long serialVersionUID = -7514160997164582554L;

    
    public static Stroke DEFAULT_ADVANCE_LINE_STROKE = new BasicStroke(1.0f);

    
    public static final Paint DEFAULT_ADVANCE_LINE_PAINT = Color.gray;

    
    protected double offset;

    
    protected double period;

    
    protected boolean boundMappedToLastCycle;

    
    protected boolean advanceLineVisible;

    
    protected transient Stroke advanceLineStroke = DEFAULT_ADVANCE_LINE_STROKE;

    
    protected transient Paint advanceLinePaint;

    private transient boolean internalMarkerWhenTicksOverlap;
    private transient Tick internalMarkerCycleBoundTick;

    
    public CyclicNumberAxis(double period) {
        this(period, 0.0);
    }

    
    public CyclicNumberAxis(double period, double offset) {
        this(period, offset, null);
    }

    
    public CyclicNumberAxis(double period, String label) {
        this(0, period, label);
    }

    
    public CyclicNumberAxis(double period, double offset, String label) {
        super(label);
        this.period = period;
        this.offset = offset;
        setFixedAutoRange(period);
        this.advanceLineVisible = true;
        this.advanceLinePaint = DEFAULT_ADVANCE_LINE_PAINT;
    }

    
    public boolean isAdvanceLineVisible() {
        return this.advanceLineVisible;
    }

    
    public void setAdvanceLineVisible(boolean visible) {
        this.advanceLineVisible = visible;
    }

    
    public Paint getAdvanceLinePaint() {
        return this.advanceLinePaint;
    }

    
    public void setAdvanceLinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.advanceLinePaint = paint;
    }

    
    public Stroke getAdvanceLineStroke() {
        return this.advanceLineStroke;
    }
    
    public void setAdvanceLineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.advanceLineStroke = stroke;
    }

    
    public boolean isBoundMappedToLastCycle() {
        return this.boundMappedToLastCycle;
    }

    
    public void setBoundMappedToLastCycle(boolean boundMappedToLastCycle) {
        this.boundMappedToLastCycle = boundMappedToLastCycle;
    }

    
    protected void selectHorizontalAutoTickUnit(Graphics2D g2,
                                                Rectangle2D drawArea,
                                                Rectangle2D dataArea,
                                                RectangleEdge edge) {

        double tickLabelWidth
            = estimateMaximumTickLabelWidth(g2, getTickUnit());

        
        double n = getRange().getLength()
                   * tickLabelWidth / dataArea.getWidth();

        setTickUnit(
            (NumberTickUnit) getStandardTickUnits().getCeilingTickUnit(n),
            false, false
        );

     }

    
    protected void selectVerticalAutoTickUnit(Graphics2D g2,
                                                Rectangle2D drawArea,
                                                Rectangle2D dataArea,
                                                RectangleEdge edge) {

        double tickLabelWidth
            = estimateMaximumTickLabelWidth(g2, getTickUnit());

        
        double n = getRange().getLength()
                   * tickLabelWidth / dataArea.getHeight();

        setTickUnit(
            (NumberTickUnit) getStandardTickUnits().getCeilingTickUnit(n),
            false, false
        );

     }

    
    protected static class CycleBoundTick extends NumberTick {

        
        public boolean mapToLastCycle;

        
        public CycleBoundTick(boolean mapToLastCycle, Number number,
                              String label, TextAnchor textAnchor,
                              TextAnchor rotationAnchor, double angle) {
            super(number, label, textAnchor, rotationAnchor, angle);
            this.mapToLastCycle = mapToLastCycle;
        }
    }

    
    protected float[] calculateAnchorPoint(ValueTick tick, double cursor,
                                           Rectangle2D dataArea,
                                           RectangleEdge edge) {
        if (tick instanceof CycleBoundTick) {
            boolean mapsav = this.boundMappedToLastCycle;
            this.boundMappedToLastCycle
                = ((CycleBoundTick) tick).mapToLastCycle;
            float[] ret = super.calculateAnchorPoint(
                tick, cursor, dataArea, edge
            );
            this.boundMappedToLastCycle = mapsav;
            return ret;
        }
        return super.calculateAnchorPoint(tick, cursor, dataArea, edge);
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

        double unit = getTickUnit().getSize();
        double cycleBound = getCycleBound();
        double currentTickValue = Math.ceil(cycleBound / unit) * unit;
        double upperValue = getRange().getUpperBound();
        boolean cycled = false;

        boolean boundMapping = this.boundMappedToLastCycle;
        this.boundMappedToLastCycle = false;

        CycleBoundTick lastTick = null;
        float lastX = 0.0f;

        if (upperValue == cycleBound) {
            currentTickValue = calculateLowestVisibleTickValue();
            cycled = true;
            this.boundMappedToLastCycle = true;
        }

        while (currentTickValue <= upperValue) {

            
            boolean cyclenow = false;
            if ((currentTickValue + unit > upperValue) && !cycled) {
                cyclenow = true;
            }

            double xx = valueToJava2D(currentTickValue, dataArea, edge);
            String tickLabel;
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                tickLabel = formatter.format(currentTickValue);
            }
            else {
                tickLabel = getTickUnit().valueToString(currentTickValue);
            }
            float x = (float) xx;
            TextAnchor anchor = null;
            TextAnchor rotationAnchor = null;
            double angle = 0.0;
            if (isVerticalTickLabels()) {
                if (edge == RectangleEdge.TOP) {
                    angle = Math.PI / 2.0;
                }
                else {
                    angle = -Math.PI / 2.0;
                }
                anchor = TextAnchor.CENTER_RIGHT;
                
                if ((lastTick != null) && (lastX == x)
                        && (currentTickValue != cycleBound)) {
                    anchor = isInverted()
                        ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
                    result.remove(result.size() - 1);
                    result.add(new CycleBoundTick(
                        this.boundMappedToLastCycle, lastTick.getNumber(),
                        lastTick.getText(), anchor, anchor,
                        lastTick.getAngle())
                    );
                    this.internalMarkerWhenTicksOverlap = true;
                    anchor = isInverted()
                        ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
                }
                rotationAnchor = anchor;
            }
            else {
                if (edge == RectangleEdge.TOP) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if ((lastTick != null) && (lastX == x)
                            && (currentTickValue != cycleBound)) {
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(
                            this.boundMappedToLastCycle, lastTick.getNumber(),
                            lastTick.getText(), anchor, anchor,
                            lastTick.getAngle())
                        );
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                    }
                    rotationAnchor = anchor;
                }
                else {
                    anchor = TextAnchor.TOP_CENTER;
                    if ((lastTick != null) && (lastX == x)
                            && (currentTickValue != cycleBound)) {
                        anchor = isInverted()
                            ? TextAnchor.TOP_LEFT : TextAnchor.TOP_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(
                            this.boundMappedToLastCycle, lastTick.getNumber(),
                            lastTick.getText(), anchor, anchor,
                            lastTick.getAngle())
                        );
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted()
                            ? TextAnchor.TOP_RIGHT : TextAnchor.TOP_LEFT;
                    }
                    rotationAnchor = anchor;
                }
            }

            CycleBoundTick tick = new CycleBoundTick(
                this.boundMappedToLastCycle,
                new Double(currentTickValue), tickLabel, anchor,
                rotationAnchor, angle
            );
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            result.add(tick);
            lastTick = tick;
            lastX = x;

            currentTickValue += unit;

            if (cyclenow) {
                currentTickValue = calculateLowestVisibleTickValue();
                upperValue = cycleBound;
                cycled = true;
                this.boundMappedToLastCycle = true;
            }

        }
        this.boundMappedToLastCycle = boundMapping;
        return result;

    }

    
    protected List refreshVerticalTicks(Graphics2D g2,
                                        Rectangle2D dataArea,
                                        RectangleEdge edge) {

        List result = new java.util.ArrayList();
        result.clear();

        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }

        double unit = getTickUnit().getSize();
        double cycleBound = getCycleBound();
        double currentTickValue = Math.ceil(cycleBound / unit) * unit;
        double upperValue = getRange().getUpperBound();
        boolean cycled = false;

        boolean boundMapping = this.boundMappedToLastCycle;
        this.boundMappedToLastCycle = true;

        NumberTick lastTick = null;
        float lastY = 0.0f;

        if (upperValue == cycleBound) {
            currentTickValue = calculateLowestVisibleTickValue();
            cycled = true;
            this.boundMappedToLastCycle = true;
        }

        while (currentTickValue <= upperValue) {

            
            boolean cyclenow = false;
            if ((currentTickValue + unit > upperValue) && !cycled) {
                cyclenow = true;
            }

            double yy = valueToJava2D(currentTickValue, dataArea, edge);
            String tickLabel;
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                tickLabel = formatter.format(currentTickValue);
            }
            else {
                tickLabel = getTickUnit().valueToString(currentTickValue);
            }

            float y = (float) yy;
            TextAnchor anchor = null;
            TextAnchor rotationAnchor = null;
            double angle = 0.0;
            if (isVerticalTickLabels()) {

                if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if ((lastTick != null) && (lastY == y)
                            && (currentTickValue != cycleBound)) {
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(
                            this.boundMappedToLastCycle, lastTick.getNumber(),
                            lastTick.getText(), anchor, anchor,
                            lastTick.getAngle())
                        );
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                    }
                    rotationAnchor = anchor;
                    angle = -Math.PI / 2.0;
                }
                else {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if ((lastTick != null) && (lastY == y)
                            && (currentTickValue != cycleBound)) {
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(
                            this.boundMappedToLastCycle, lastTick.getNumber(),
                            lastTick.getText(), anchor, anchor,
                            lastTick.getAngle())
                        );
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                    }
                    rotationAnchor = anchor;
                    angle = Math.PI / 2.0;
                }
            }
            else {
                if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    if ((lastTick != null) && (lastY == y)
                            && (currentTickValue != cycleBound)) {
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(
                            this.boundMappedToLastCycle, lastTick.getNumber(),
                            lastTick.getText(), anchor, anchor,
                            lastTick.getAngle())
                        );
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted()
                            ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
                    }
                    rotationAnchor = anchor;
                }
                else {
                    anchor = TextAnchor.CENTER_LEFT;
                    if ((lastTick != null) && (lastY == y)
                            && (currentTickValue != cycleBound)) {
                        anchor = isInverted()
                            ? TextAnchor.BOTTOM_LEFT : TextAnchor.TOP_LEFT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(
                            this.boundMappedToLastCycle, lastTick.getNumber(),
                            lastTick.getText(), anchor, anchor,
                            lastTick.getAngle())
                        );
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted()
                            ? TextAnchor.TOP_LEFT : TextAnchor.BOTTOM_LEFT;
                    }
                    rotationAnchor = anchor;
                }
            }

            CycleBoundTick tick = new CycleBoundTick(
                this.boundMappedToLastCycle, new Double(currentTickValue),
                tickLabel, anchor, rotationAnchor, angle
            );
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            result.add(tick);
            lastTick = tick;
            lastY = y;

            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }

            currentTickValue += unit;

            if (cyclenow) {
                currentTickValue = calculateLowestVisibleTickValue();
                upperValue = cycleBound;
                cycled = true;
                this.boundMappedToLastCycle = false;
            }

        }
        this.boundMappedToLastCycle = boundMapping;
        return result;
    }

    
    public double java2DToValue(double java2DValue, Rectangle2D dataArea,
                                RectangleEdge edge) {
        Range range = getRange();

        double vmax = range.getUpperBound();
        double vp = getCycleBound();

        double jmin = 0.0;
        double jmax = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            jmin = dataArea.getMinX();
            jmax = dataArea.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            jmin = dataArea.getMaxY();
            jmax = dataArea.getMinY();
        }

        if (isInverted()) {
            double jbreak = jmax - (vmax - vp) * (jmax - jmin) / this.period;
            if (java2DValue >= jbreak) {
                return vp + (jmax - java2DValue) * this.period / (jmax - jmin);
            }
            else {
                return vp - (java2DValue - jmin) * this.period / (jmax - jmin);
            }
        }
        else {
            double jbreak = (vmax - vp) * (jmax - jmin) / this.period + jmin;
            if (java2DValue <= jbreak) {
                return vp + (java2DValue - jmin) * this.period / (jmax - jmin);
            }
            else {
                return vp - (jmax - java2DValue) * this.period / (jmax - jmin);
            }
        }
    }

    
    public double valueToJava2D(double value, Rectangle2D dataArea,
                                RectangleEdge edge) {
        Range range = getRange();

        double vmin = range.getLowerBound();
        double vmax = range.getUpperBound();
        double vp = getCycleBound();

        if ((value < vmin) || (value > vmax)) {
            return Double.NaN;
        }


        double jmin = 0.0;
        double jmax = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            jmin = dataArea.getMinX();
            jmax = dataArea.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            jmax = dataArea.getMinY();
            jmin = dataArea.getMaxY();
        }

        if (isInverted()) {
            if (value == vp) {
                return this.boundMappedToLastCycle ? jmin : jmax;
            }
            else if (value > vp) {
                return jmax - (value - vp) * (jmax - jmin) / this.period;
            }
            else {
                return jmin + (vp - value) * (jmax - jmin) / this.period;
            }
        }
        else {
            if (value == vp) {
                return this.boundMappedToLastCycle ? jmax : jmin;
            }
            else if (value >= vp) {
                return jmin + (value - vp) * (jmax - jmin) / this.period;
            }
            else {
                return jmax - (vp - value) * (jmax - jmin) / this.period;
            }
        }
    }

    
    public void centerRange(double value) {
        setRange(value - this.period / 2.0, value + this.period / 2.0);
    }

    
    public void setAutoRangeMinimumSize(double size, boolean notify) {
        if (size > this.period) {
            this.period = size;
        }
        super.setAutoRangeMinimumSize(size, notify);
    }

    
    public void setFixedAutoRange(double length) {
        this.period = length;
        super.setFixedAutoRange(length);
    }

    
    public void setRange(Range range, boolean turnOffAutoRange,
                         boolean notify) {
        double size = range.getUpperBound() - range.getLowerBound();
        if (size > this.period) {
            this.period = size;
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    
    public double getCycleBound() {
        return Math.floor(
            (getRange().getUpperBound() - this.offset) / this.period
        ) * this.period + this.offset;
    }

    
    public double getOffset() {
        return this.offset;
    }

    
    public void setOffset(double offset) {
        this.offset = offset;
    }

    
    public double getPeriod() {
        return this.period;
    }

    
    public void setPeriod(double period) {
        this.period = period;
    }

    
    protected AxisState drawTickMarksAndLabels(Graphics2D g2, double cursor,
            Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge,
            PlotRenderingInfo info) {

        this.internalMarkerWhenTicksOverlap = false;
        AxisState ret = super.drawTickMarksAndLabels(g2, cursor, plotArea,
                dataArea, edge, info);

        
        if (!this.internalMarkerWhenTicksOverlap) {
            return ret;
        }

        double ol = getTickMarkOutsideLength();
        FontMetrics fm = g2.getFontMetrics(getTickLabelFont());

        if (isVerticalTickLabels()) {
            ol = fm.getMaxAdvance();
        }
        else {
            ol = fm.getHeight();
        }

        double il = 0;
        if (isTickMarksVisible()) {
            float xx = (float) valueToJava2D(getRange().getUpperBound(),
                    dataArea, edge);
            Line2D mark = null;
            g2.setStroke(getTickMarkStroke());
            g2.setPaint(getTickMarkPaint());
            if (edge == RectangleEdge.LEFT) {
                mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
            }
            else if (edge == RectangleEdge.RIGHT) {
                mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
            }
            else if (edge == RectangleEdge.TOP) {
                mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
            }
            else if (edge == RectangleEdge.BOTTOM) {
                mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
            }
            g2.draw(mark);
        }
        return ret;
    }

    
    public AxisState draw(Graphics2D g2,
                          double cursor,
                          Rectangle2D plotArea,
                          Rectangle2D dataArea,
                          RectangleEdge edge,
                          PlotRenderingInfo plotState) {

        AxisState ret = super.draw(
            g2, cursor, plotArea, dataArea, edge, plotState
        );
        if (isAdvanceLineVisible()) {
            double xx = valueToJava2D(
                getRange().getUpperBound(), dataArea, edge
            );
            Line2D mark = null;
            g2.setStroke(getAdvanceLineStroke());
            g2.setPaint(getAdvanceLinePaint());
            if (edge == RectangleEdge.LEFT) {
                mark = new Line2D.Double(
                    cursor, xx, cursor + dataArea.getWidth(), xx
                );
            }
            else if (edge == RectangleEdge.RIGHT) {
                mark = new Line2D.Double(
                    cursor - dataArea.getWidth(), xx, cursor, xx
                );
            }
            else if (edge == RectangleEdge.TOP) {
                mark = new Line2D.Double(
                    xx, cursor + dataArea.getHeight(), xx, cursor
                );
            }
            else if (edge == RectangleEdge.BOTTOM) {
                mark = new Line2D.Double(
                    xx, cursor, xx, cursor - dataArea.getHeight()
                );
            }
            g2.draw(mark);
        }
        return ret;
    }

    
    public AxisSpace reserveSpace(Graphics2D g2,
                                  Plot plot,
                                  Rectangle2D plotArea,
                                  RectangleEdge edge,
                                  AxisSpace space) {

        this.internalMarkerCycleBoundTick = null;
        AxisSpace ret = super.reserveSpace(g2, plot, plotArea, edge, space);
        if (this.internalMarkerCycleBoundTick == null) {
            return ret;
        }

        FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
        Rectangle2D r = TextUtilities.getTextBounds(
            this.internalMarkerCycleBoundTick.getText(), g2, fm
        );

        if (RectangleEdge.isTopOrBottom(edge)) {
            if (isVerticalTickLabels()) {
                space.add(r.getHeight() / 2, RectangleEdge.RIGHT);
            }
            else {
                space.add(r.getWidth() / 2, RectangleEdge.RIGHT);
            }
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            if (isVerticalTickLabels()) {
                space.add(r.getWidth() / 2, RectangleEdge.TOP);
            }
            else {
                space.add(r.getHeight() / 2, RectangleEdge.TOP);
            }
        }

        return ret;

    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.advanceLinePaint, stream);
        SerialUtilities.writeStroke(this.advanceLineStroke, stream);

    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.advanceLinePaint = SerialUtilities.readPaint(stream);
        this.advanceLineStroke = SerialUtilities.readStroke(stream);

    }


    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CyclicNumberAxis)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CyclicNumberAxis that = (CyclicNumberAxis) obj;
        if (this.period != that.period) {
            return false;
        }
        if (this.offset != that.offset) {
            return false;
        }
        if (!PaintUtilities.equal(this.advanceLinePaint,
                that.advanceLinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.advanceLineStroke,
                that.advanceLineStroke)) {
            return false;
        }
        if (this.advanceLineVisible != that.advanceLineVisible) {
            return false;
        }
        if (this.boundMappedToLastCycle != that.boundMappedToLastCycle) {
            return false;
        }
        return true;
    }
}
