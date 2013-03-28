

package org.jfree.chart.axis;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;


public abstract class ValueAxis extends Axis
        implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = 3698345477322391456L;

    
    public static final Range DEFAULT_RANGE = new Range(0.0, 1.0);

    
    public static final boolean DEFAULT_AUTO_RANGE = true;

    
    public static final boolean DEFAULT_INVERTED = false;

    
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE = 0.00000001;

    
    public static final double DEFAULT_LOWER_MARGIN = 0.05;

    
    public static final double DEFAULT_UPPER_MARGIN = 0.05;

    
    public static final double DEFAULT_LOWER_BOUND = 0.0;

    
    public static final double DEFAULT_UPPER_BOUND = 1.0;

    
    public static final boolean DEFAULT_AUTO_TICK_UNIT_SELECTION = true;

    
    public static final int MAXIMUM_TICK_COUNT = 500;

    
    private boolean positiveArrowVisible;

    
    private boolean negativeArrowVisible;

    
    private transient Shape upArrow;

    
    private transient Shape downArrow;

    
    private transient Shape leftArrow;

    
    private transient Shape rightArrow;

    
    private boolean inverted;

    
    private Range range;

    
    private boolean autoRange;

    
    private double autoRangeMinimumSize;

    
    private Range defaultAutoRange;

    
    private double upperMargin;

    
    private double lowerMargin;

    
    private double fixedAutoRange;

    
    private boolean autoTickUnitSelection;

    
    private TickUnitSource standardTickUnits;

    
    private int autoTickIndex;

    
    private int minorTickCount;

    
    private boolean verticalTickLabels;

    
    protected ValueAxis(String label, TickUnitSource standardTickUnits) {

        super(label);

        this.positiveArrowVisible = false;
        this.negativeArrowVisible = false;

        this.range = DEFAULT_RANGE;
        this.autoRange = DEFAULT_AUTO_RANGE;
        this.defaultAutoRange = DEFAULT_RANGE;

        this.inverted = DEFAULT_INVERTED;
        this.autoRangeMinimumSize = DEFAULT_AUTO_RANGE_MINIMUM_SIZE;

        this.lowerMargin = DEFAULT_LOWER_MARGIN;
        this.upperMargin = DEFAULT_UPPER_MARGIN;

        this.fixedAutoRange = 0.0;

        this.autoTickUnitSelection = DEFAULT_AUTO_TICK_UNIT_SELECTION;
        this.standardTickUnits = standardTickUnits;

        Polygon p1 = new Polygon();
        p1.addPoint(0, 0);
        p1.addPoint(-2, 2);
        p1.addPoint(2, 2);

        this.upArrow = p1;

        Polygon p2 = new Polygon();
        p2.addPoint(0, 0);
        p2.addPoint(-2, -2);
        p2.addPoint(2, -2);

        this.downArrow = p2;

        Polygon p3 = new Polygon();
        p3.addPoint(0, 0);
        p3.addPoint(-2, -2);
        p3.addPoint(-2, 2);

        this.rightArrow = p3;

        Polygon p4 = new Polygon();
        p4.addPoint(0, 0);
        p4.addPoint(2, -2);
        p4.addPoint(2, 2);

        this.leftArrow = p4;

        this.verticalTickLabels = false;
        this.minorTickCount = 0;

    }

    
    public boolean isVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    
    public void setVerticalTickLabels(boolean flag) {
        if (this.verticalTickLabels != flag) {
            this.verticalTickLabels = flag;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    
    public boolean isPositiveArrowVisible() {
        return this.positiveArrowVisible;
    }

    
    public void setPositiveArrowVisible(boolean visible) {
        this.positiveArrowVisible = visible;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public boolean isNegativeArrowVisible() {
        return this.negativeArrowVisible;
    }

    
    public void setNegativeArrowVisible(boolean visible) {
        this.negativeArrowVisible = visible;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public Shape getUpArrow() {
        return this.upArrow;
    }

    
    public void setUpArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.upArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public Shape getDownArrow() {
        return this.downArrow;
    }

    
    public void setDownArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.downArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public Shape getLeftArrow() {
        return this.leftArrow;
    }

    
    public void setLeftArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.leftArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public Shape getRightArrow() {
        return this.rightArrow;
    }

    
    public void setRightArrow(Shape arrow) {
        if (arrow == null) {
            throw new IllegalArgumentException("Null 'arrow' argument.");
        }
        this.rightArrow = arrow;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    protected void drawAxisLine(Graphics2D g2, double cursor,
                                Rectangle2D dataArea, RectangleEdge edge) {
        Line2D axisLine = null;
        if (edge == RectangleEdge.TOP) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor,
                    dataArea.getMaxX(), cursor);
        }
        else if (edge == RectangleEdge.BOTTOM) {
            axisLine = new Line2D.Double(dataArea.getX(), cursor,
                    dataArea.getMaxX(), cursor);
        }
        else if (edge == RectangleEdge.LEFT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor,
                    dataArea.getMaxY());
        }
        else if (edge == RectangleEdge.RIGHT) {
            axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor,
                    dataArea.getMaxY());
        }
        g2.setPaint(getAxisLinePaint());
        g2.setStroke(getAxisLineStroke());
        g2.draw(axisLine);

        boolean drawUpOrRight = false;
        boolean drawDownOrLeft = false;
        if (this.positiveArrowVisible) {
            if (this.inverted) {
                drawDownOrLeft = true;
            }
            else {
                drawUpOrRight = true;
            }
        }
        if (this.negativeArrowVisible) {
            if (this.inverted) {
                drawUpOrRight = true;
            }
            else {
                drawDownOrLeft = true;
            }
        }
        if (drawUpOrRight) {
            double x = 0.0;
            double y = 0.0;
            Shape arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMaxX();
                y = cursor;
                arrow = this.rightArrow;
            }
            else if (edge == RectangleEdge.LEFT
                    || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMinY();
                arrow = this.upArrow;
            }

            
            AffineTransform transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            Shape shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }

        if (drawDownOrLeft) {
            double x = 0.0;
            double y = 0.0;
            Shape arrow = null;
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                x = dataArea.getMinX();
                y = cursor;
                arrow = this.leftArrow;
            }
            else if (edge == RectangleEdge.LEFT
                    || edge == RectangleEdge.RIGHT) {
                x = cursor;
                y = dataArea.getMaxY();
                arrow = this.downArrow;
            }

            
            AffineTransform transformer = new AffineTransform();
            transformer.setToTranslation(x, y);
            Shape shape = transformer.createTransformedShape(arrow);
            g2.fill(shape);
            g2.draw(shape);
        }

    }

    
    protected float[] calculateAnchorPoint(ValueTick tick,
                                           double cursor,
                                           Rectangle2D dataArea,
                                           RectangleEdge edge) {

        RectangleInsets insets = getTickLabelInsets();
        float[] result = new float[2];
        if (edge == RectangleEdge.TOP) {
            result[0] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float) (cursor - insets.getBottom() - 2.0);
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result[0] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
            result[1] = (float) (cursor + insets.getTop() + 2.0);
        }
        else if (edge == RectangleEdge.LEFT) {
            result[0] = (float) (cursor - insets.getLeft() - 2.0);
            result[1] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
        }
        else if (edge == RectangleEdge.RIGHT) {
            result[0] = (float) (cursor + insets.getRight() + 2.0);
            result[1] = (float) valueToJava2D(tick.getValue(), dataArea, edge);
        }
        return result;
    }

    
    protected AxisState drawTickMarksAndLabels(Graphics2D g2,
            double cursor, Rectangle2D plotArea, Rectangle2D dataArea,
            RectangleEdge edge) {

        AxisState state = new AxisState(cursor);

        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }

        List ticks = refreshTicks(g2, state, dataArea, edge);
        state.setTicks(ticks);
        g2.setFont(getTickLabelFont());
        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            ValueTick tick = (ValueTick) iterator.next();
            if (isTickLabelsVisible()) {
                g2.setPaint(getTickLabelPaint());
                float[] anchorPoint = calculateAnchorPoint(tick, cursor,
                        dataArea, edge);
                TextUtilities.drawRotatedString(tick.getText(), g2,
                        anchorPoint[0], anchorPoint[1], tick.getTextAnchor(),
                        tick.getAngle(), tick.getRotationAnchor());
            }

            if ((isTickMarksVisible() && tick.getTickType().equals(
                    TickType.MAJOR)) || (isMinorTickMarksVisible() 
                    && tick.getTickType().equals(TickType.MINOR))) {

                double ol = (tick.getTickType().equals(TickType.MINOR)) ?
                    getMinorTickMarkOutsideLength() : getTickMarkOutsideLength();

                double il = (tick.getTickType().equals(TickType.MINOR)) ?
                    getMinorTickMarkInsideLength() : getTickMarkInsideLength();

                float xx = (float) valueToJava2D(tick.getValue(), dataArea,
                        edge);
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
        }

        
        
        double used = 0.0;
        if (isTickLabelsVisible()) {
            if (edge == RectangleEdge.LEFT) {
                used += findMaximumTickLabelWidth(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorLeft(used);
            }
            else if (edge == RectangleEdge.RIGHT) {
                used = findMaximumTickLabelWidth(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorRight(used);
            }
            else if (edge == RectangleEdge.TOP) {
                used = findMaximumTickLabelHeight(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorUp(used);
            }
            else if (edge == RectangleEdge.BOTTOM) {
                used = findMaximumTickLabelHeight(ticks, g2, plotArea,
                        isVerticalTickLabels());
                state.cursorDown(used);
            }
        }

        return state;
    }

    
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot,
                                  Rectangle2D plotArea,
                                  RectangleEdge edge, AxisSpace space) {

        
        if (space == null) {
            space = new AxisSpace();
        }

        
        if (!isVisible()) {
            return space;
        }

        
        double dimension = getFixedDimension();
        if (dimension > 0.0) {
            space.ensureAtLeast(dimension, edge);
        }

        
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            List ticks = refreshTicks(g2, new AxisState(), plotArea, edge);
            if (RectangleEdge.isTopOrBottom(edge)) {
                tickLabelHeight = findMaximumTickLabelHeight(ticks, g2,
                        plotArea, isVerticalTickLabels());
            }
            else if (RectangleEdge.isLeftOrRight(edge)) {
                tickLabelWidth = findMaximumTickLabelWidth(ticks, g2, plotArea,
                        isVerticalTickLabels());
            }
        }

        
        Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelHeight, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelWidth, edge);
        }

        return space;

    }

    
    protected double findMaximumTickLabelHeight(List ticks,
                                                Graphics2D g2,
                                                Rectangle2D drawArea,
                                                boolean vertical) {

        RectangleInsets insets = getTickLabelInsets();
        Font font = getTickLabelFont();
        double maxHeight = 0.0;
        if (vertical) {
            FontMetrics fm = g2.getFontMetrics(font);
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = TextUtilities.getTextBounds(
                        tick.getText(), g2, fm);
                if (labelBounds.getWidth() + insets.getTop()
                        + insets.getBottom() > maxHeight) {
                    maxHeight = labelBounds.getWidth()
                                + insets.getTop() + insets.getBottom();
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("ABCxyz",
                    g2.getFontRenderContext());
            maxHeight = metrics.getHeight()
                        + insets.getTop() + insets.getBottom();
        }
        return maxHeight;

    }

    
    protected double findMaximumTickLabelWidth(List ticks,
                                               Graphics2D g2,
                                               Rectangle2D drawArea,
                                               boolean vertical) {

        RectangleInsets insets = getTickLabelInsets();
        Font font = getTickLabelFont();
        double maxWidth = 0.0;
        if (!vertical) {
            FontMetrics fm = g2.getFontMetrics(font);
            Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = TextUtilities.getTextBounds(
                        tick.getText(), g2, fm);
                if (labelBounds.getWidth() + insets.getLeft()
                        + insets.getRight() > maxWidth) {
                    maxWidth = labelBounds.getWidth()
                               + insets.getLeft() + insets.getRight();
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("ABCxyz",
                    g2.getFontRenderContext());
            maxWidth = metrics.getHeight()
                       + insets.getTop() + insets.getBottom();
        }
        return maxWidth;

    }

    
    public boolean isInverted() {
        return this.inverted;
    }

    
    public void setInverted(boolean flag) {

        if (this.inverted != flag) {
            this.inverted = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    
    public boolean isAutoRange() {
        return this.autoRange;
    }

    
    public void setAutoRange(boolean auto) {
        setAutoRange(auto, true);
    }

    
    protected void setAutoRange(boolean auto, boolean notify) {
        if (this.autoRange != auto) {
            this.autoRange = auto;
            if (this.autoRange) {
                autoAdjustRange();
            }
            if (notify) {
                notifyListeners(new AxisChangeEvent(this));
            }
        }
    }

    
    public double getAutoRangeMinimumSize() {
        return this.autoRangeMinimumSize;
    }

    
    public void setAutoRangeMinimumSize(double size) {
        setAutoRangeMinimumSize(size, true);
    }

    
    public void setAutoRangeMinimumSize(double size, boolean notify) {
        if (size <= 0.0) {
            throw new IllegalArgumentException(
                "NumberAxis.setAutoRangeMinimumSize(double): must be > 0.0.");
        }
        if (this.autoRangeMinimumSize != size) {
            this.autoRangeMinimumSize = size;
            if (this.autoRange) {
                autoAdjustRange();
            }
            if (notify) {
                notifyListeners(new AxisChangeEvent(this));
            }
        }

    }

    
    public Range getDefaultAutoRange() {
        return this.defaultAutoRange;
    }

    
    public void setDefaultAutoRange(Range range) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        this.defaultAutoRange = range;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public double getUpperMargin() {
        return this.upperMargin;
    }

    
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public double getFixedAutoRange() {
        return this.fixedAutoRange;
    }

    
    public void setFixedAutoRange(double length) {
        this.fixedAutoRange = length;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public double getLowerBound() {
        return this.range.getLowerBound();
    }

    
    public void setLowerBound(double min) {
        if (this.range.getUpperBound() > min) {
            setRange(new Range(min, this.range.getUpperBound()));
        }
        else {
            setRange(new Range(min, min + 1.0));
        }
    }

    
    public double getUpperBound() {
        return this.range.getUpperBound();
    }

    
    public void setUpperBound(double max) {
        if (this.range.getLowerBound() < max) {
            setRange(new Range(this.range.getLowerBound(), max));
        }
        else {
            setRange(max - 1.0, max);
        }
    }

    
    public Range getRange() {
        return this.range;
    }

    
    public void setRange(Range range) {
        
        setRange(range, true, true);
    }

    
    public void setRange(Range range, boolean turnOffAutoRange,
                         boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        if (turnOffAutoRange) {
            this.autoRange = false;
        }
        this.range = range;
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    
    public void setRange(double lower, double upper) {
        setRange(new Range(lower, upper));
    }

    
    public void setRangeWithMargins(Range range) {
        setRangeWithMargins(range, true, true);
    }

    
    public void setRangeWithMargins(Range range, boolean turnOffAutoRange,
                                    boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        setRange(Range.expand(range, getLowerMargin(), getUpperMargin()),
                turnOffAutoRange, notify);
    }

    
    public void setRangeWithMargins(double lower, double upper) {
        setRangeWithMargins(new Range(lower, upper));
    }

    
    public void setRangeAboutValue(double value, double length) {
        setRange(new Range(value - length / 2, value + length / 2));
    }

    
    public boolean isAutoTickUnitSelection() {
        return this.autoTickUnitSelection;
    }

    
    public void setAutoTickUnitSelection(boolean flag) {
        setAutoTickUnitSelection(flag, true);
    }

    
    public void setAutoTickUnitSelection(boolean flag, boolean notify) {

        if (this.autoTickUnitSelection != flag) {
            this.autoTickUnitSelection = flag;
            if (notify) {
                notifyListeners(new AxisChangeEvent(this));
            }
        }
    }

    
    public TickUnitSource getStandardTickUnits() {
        return this.standardTickUnits;
    }

    
    public void setStandardTickUnits(TickUnitSource source) {
        this.standardTickUnits = source;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public int getMinorTickCount() {
        return this.minorTickCount;
    }
    
    
    public void setMinorTickCount(int count) {
        this.minorTickCount = count;
        notifyListeners(new AxisChangeEvent(this));
    }

    
    public abstract double valueToJava2D(double value, Rectangle2D area,
                                         RectangleEdge edge);

    
    public double lengthToJava2D(double length, Rectangle2D area,
                                 RectangleEdge edge) {
        double zero = valueToJava2D(0.0, area, edge);
        double l = valueToJava2D(length, area, edge);
        return Math.abs(l - zero);
    }

    
    public abstract double java2DToValue(double java2DValue,
                                         Rectangle2D area,
                                         RectangleEdge edge);

    
    protected abstract void autoAdjustRange();

    
    public void centerRange(double value) {

        double central = this.range.getCentralValue();
        Range adjusted = new Range(this.range.getLowerBound() + value - central,
                this.range.getUpperBound() + value - central);
        setRange(adjusted);

    }

    
    public void resizeRange(double percent) {
        resizeRange(percent, this.range.getCentralValue());
    }

    
    public void resizeRange(double percent, double anchorValue) {
        if (percent > 0.0) {
            double halfLength = this.range.getLength() * percent / 2;
            Range adjusted = new Range(anchorValue - halfLength,
                    anchorValue + halfLength);
            setRange(adjusted);
        }
        else {
            setAutoRange(true);
        }
    }

    
    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.range.getLowerBound();
        double length = this.range.getLength();
        Range adjusted = null;
        if (isInverted()) {
            adjusted = new Range(start + (length * (1 - upperPercent)),
                                 start + (length * (1 - lowerPercent)));
        }
        else {
            adjusted = new Range(start + length * lowerPercent,
                    start + length * upperPercent);
        }
        setRange(adjusted);
    }

    
    protected int getAutoTickIndex() {
        return this.autoTickIndex;
    }

    
    protected void setAutoTickIndex(int index) {
        this.autoTickIndex = index;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ValueAxis)) {
            return false;
        }
        ValueAxis that = (ValueAxis) obj;
        if (this.positiveArrowVisible != that.positiveArrowVisible) {
            return false;
        }
        if (this.negativeArrowVisible != that.negativeArrowVisible) {
            return false;
        }
        if (this.inverted != that.inverted) {
            return false;
        }
        if (!ObjectUtilities.equal(this.range, that.range)) {
            return false;
        }
        if (this.autoRange != that.autoRange) {
            return false;
        }
        if (this.autoRangeMinimumSize != that.autoRangeMinimumSize) {
            return false;
        }
        if (!this.defaultAutoRange.equals(that.defaultAutoRange)) {
            return false;
        }
        if (this.upperMargin != that.upperMargin) {
            return false;
        }
        if (this.lowerMargin != that.lowerMargin) {
            return false;
        }
        if (this.fixedAutoRange != that.fixedAutoRange) {
            return false;
        }
        if (this.autoTickUnitSelection != that.autoTickUnitSelection) {
            return false;
        }
        if (!ObjectUtilities.equal(this.standardTickUnits,
                that.standardTickUnits)) {
            return false;
        }
        if (this.verticalTickLabels != that.verticalTickLabels) {
            return false;
        }
        if (this.minorTickCount != that.minorTickCount) {
            return false;
        }
        return super.equals(obj);
    }

    
    public Object clone() throws CloneNotSupportedException {
        ValueAxis clone = (ValueAxis) super.clone();
        return clone;
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.upArrow, stream);
        SerialUtilities.writeShape(this.downArrow, stream);
        SerialUtilities.writeShape(this.leftArrow, stream);
        SerialUtilities.writeShape(this.rightArrow, stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {

        stream.defaultReadObject();
        this.upArrow = SerialUtilities.readShape(stream);
        this.downArrow = SerialUtilities.readShape(stream);
        this.leftArrow = SerialUtilities.readShape(stream);
        this.rightArrow = SerialUtilities.readShape(stream);

    }

}
