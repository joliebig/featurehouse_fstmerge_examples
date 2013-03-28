

package org.jfree.chart.title;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.LengthConstraintType;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.data.Range;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Size2D;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;


public class PaintScaleLegend extends Title implements PublicCloneable {

    
    static final long serialVersionUID = -1365146490993227503L;

    
    private PaintScale scale;

    
    private ValueAxis axis;

    
    private AxisLocation axisLocation;

    
    private double axisOffset;

    
    private double stripWidth;

    
    private boolean stripOutlineVisible;

    
    private transient Paint stripOutlinePaint;

    
    private transient Stroke stripOutlineStroke;

    
    private transient Paint backgroundPaint;

    
    private int subdivisions;

    
    public PaintScaleLegend(PaintScale scale, ValueAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.scale = scale;
        this.axis = axis;
        this.axisLocation = AxisLocation.BOTTOM_OR_LEFT;
        this.axisOffset = 0.0;
        this.axis.setRange(scale.getLowerBound(), scale.getUpperBound());
        this.stripWidth = 15.0;
        this.stripOutlineVisible = false;
        this.stripOutlinePaint = Color.gray;
        this.stripOutlineStroke = new BasicStroke(0.5f);
        this.backgroundPaint = Color.white;
        this.subdivisions = 100;
    }

    
    public PaintScale getScale() {
        return this.scale;
    }

    
    public void setScale(PaintScale scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Null 'scale' argument.");
        }
        this.scale = scale;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public ValueAxis getAxis() {
        return this.axis;
    }

    
    public void setAxis(ValueAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.axis = axis;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public AxisLocation getAxisLocation() {
        return this.axisLocation;
    }

    
    public void setAxisLocation(AxisLocation location) {
        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");
        }
        this.axisLocation = location;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public double getAxisOffset() {
        return this.axisOffset;
    }

    
    public void setAxisOffset(double offset) {
        this.axisOffset = offset;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public double getStripWidth() {
        return this.stripWidth;
    }

    
    public void setStripWidth(double width) {
        this.stripWidth = width;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public boolean isStripOutlineVisible() {
        return this.stripOutlineVisible;
    }

    
    public void setStripOutlineVisible(boolean visible) {
        this.stripOutlineVisible = visible;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public Paint getStripOutlinePaint() {
        return this.stripOutlinePaint;
    }

    
    public void setStripOutlinePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.stripOutlinePaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public Stroke getStripOutlineStroke() {
        return this.stripOutlineStroke;
    }

    
    public void setStripOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.stripOutlineStroke = stroke;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    
    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public int getSubdivisionCount() {
        return this.subdivisions;
    }

    
    public void setSubdivisionCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Requires 'count' > 0.");
        }
        this.subdivisions = count;
        notifyListeners(new TitleChangeEvent(this));
    }

    
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint cc = toContentConstraint(constraint);
        LengthConstraintType w = cc.getWidthConstraintType();
        LengthConstraintType h = cc.getHeightConstraintType();
        Size2D contentSize = null;
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = new Size2D(getWidth(), getHeight());
            }
            else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            }
            else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            }
            else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeRR(g2, cc.getWidthRange(),
                        cc.getHeightRange());
            }
            else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            }
            else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            }
            else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        return new Size2D(calculateTotalWidth(contentSize.getWidth()),
                calculateTotalHeight(contentSize.getHeight()));
    }

    
    protected Size2D arrangeRR(Graphics2D g2, Range widthRange,
            Range heightRange) {

        RectangleEdge position = getPosition();
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {


            float maxWidth = (float) widthRange.getUpperBound();

            
            AxisSpace space = this.axis.reserveSpace(g2, null,
                    new Rectangle2D.Double(0, 0, maxWidth, 100),
                    RectangleEdge.BOTTOM, null);

            return new Size2D(maxWidth, this.stripWidth + this.axisOffset
                    + space.getTop() + space.getBottom());
        }
        else if (position == RectangleEdge.LEFT || position
                == RectangleEdge.RIGHT) {
            float maxHeight = (float) heightRange.getUpperBound();
            AxisSpace space = this.axis.reserveSpace(g2, null,
                    new Rectangle2D.Double(0, 0, 100, maxHeight),
                    RectangleEdge.RIGHT, null);
            return new Size2D(this.stripWidth + this.axisOffset
                    + space.getLeft() + space.getRight(), maxHeight);
        }
        else {
            throw new RuntimeException("Unrecognised position.");
        }
    }

    
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {

        Rectangle2D target = (Rectangle2D) area.clone();
        target = trimMargin(target);
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(target);
        }
        getFrame().draw(g2, target);
        getFrame().getInsets().trim(target);
        target = trimPadding(target);
        double base = this.axis.getLowerBound();
        double increment = this.axis.getRange().getLength() / this.subdivisions;
        Rectangle2D r = new Rectangle2D.Double();


        if (RectangleEdge.isTopOrBottom(getPosition())) {
            RectangleEdge axisEdge = Plot.resolveRangeAxisLocation(
                    this.axisLocation, PlotOrientation.HORIZONTAL);
            if (axisEdge == RectangleEdge.TOP) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target,
                            RectangleEdge.TOP);
                    double vv1 = this.axis.valueToJava2D(v + increment, target,
                            RectangleEdge.TOP);
                    double ww = Math.abs(vv1 - vv0);
                    r.setRect(Math.min(vv0, vv1), target.getMaxY()
                            - this.stripWidth, ww, this.stripWidth);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                g2.setPaint(this.stripOutlinePaint);
                g2.setStroke(this.stripOutlineStroke);
                g2.draw(new Rectangle2D.Double(target.getMinX(),
                        target.getMaxY() - this.stripWidth, target.getWidth(),
                        this.stripWidth));
                this.axis.draw(g2, target.getMaxY() - this.stripWidth
                        - this.axisOffset, target, target, RectangleEdge.TOP,
                        null);
            }
            else if (axisEdge == RectangleEdge.BOTTOM) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target,
                            RectangleEdge.BOTTOM);
                    double vv1 = this.axis.valueToJava2D(v + increment, target,
                            RectangleEdge.BOTTOM);
                    double ww = Math.abs(vv1 - vv0);
                    r.setRect(Math.min(vv0, vv1), target.getMinY(), ww,
                            this.stripWidth);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                g2.setPaint(this.stripOutlinePaint);
                g2.setStroke(this.stripOutlineStroke);
                g2.draw(new Rectangle2D.Double(target.getMinX(),
                        target.getMinY(), target.getWidth(), this.stripWidth));
                this.axis.draw(g2, target.getMinY() + this.stripWidth
                        + this.axisOffset, target, target,
                        RectangleEdge.BOTTOM, null);
            }
        }
        else {
            RectangleEdge axisEdge = Plot.resolveRangeAxisLocation(
                    this.axisLocation, PlotOrientation.VERTICAL);
            if (axisEdge == RectangleEdge.LEFT) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target,
                            RectangleEdge.LEFT);
                    double vv1 = this.axis.valueToJava2D(v + increment, target,
                            RectangleEdge.LEFT);
                    double hh = Math.abs(vv1 - vv0);
                    r.setRect(target.getMaxX() - this.stripWidth,
                            Math.min(vv0, vv1), this.stripWidth, hh);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                g2.setPaint(this.stripOutlinePaint);
                g2.setStroke(this.stripOutlineStroke);
                g2.draw(new Rectangle2D.Double(target.getMaxX()
                        - this.stripWidth, target.getMinY(), this.stripWidth,
                        target.getHeight()));
                this.axis.draw(g2, target.getMaxX() - this.stripWidth
                        - this.axisOffset, target, target, RectangleEdge.LEFT,
                        null);
            }
            else if (axisEdge == RectangleEdge.RIGHT) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target,
                            RectangleEdge.LEFT);
                    double vv1 = this.axis.valueToJava2D(v + increment, target,
                            RectangleEdge.LEFT);
                    double hh = Math.abs(vv1 - vv0);
                    r.setRect(target.getMinX(), Math.min(vv0, vv1),
                            this.stripWidth, hh);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                g2.setPaint(this.stripOutlinePaint);
                g2.setStroke(this.stripOutlineStroke);
                g2.draw(new Rectangle2D.Double(target.getMinX(),
                        target.getMinY(), this.stripWidth, target.getHeight()));
                this.axis.draw(g2, target.getMinX() + this.stripWidth
                        + this.axisOffset, target, target, RectangleEdge.RIGHT,
                        null);
            }
        }
        return null;
    }

    
    public boolean equals(Object obj) {
        if (!(obj instanceof PaintScaleLegend)) {
            return false;
        }
        PaintScaleLegend that = (PaintScaleLegend) obj;
        if (!this.scale.equals(that.scale)) {
            return false;
        }
        if (!this.axis.equals(that.axis)) {
            return false;
        }
        if (!this.axisLocation.equals(that.axisLocation)) {
            return false;
        }
        if (this.axisOffset != that.axisOffset) {
            return false;
        }
        if (this.stripWidth != that.stripWidth) {
            return false;
        }
        if (this.stripOutlineVisible != that.stripOutlineVisible) {
            return false;
        }
        if (!PaintUtilities.equal(this.stripOutlinePaint,
                that.stripOutlinePaint)) {
            return false;
        }
        if (!this.stripOutlineStroke.equals(that.stripOutlineStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (this.subdivisions != that.subdivisions) {
            return false;
        }
        return super.equals(obj);
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.stripOutlinePaint, stream);
        SerialUtilities.writeStroke(this.stripOutlineStroke, stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.stripOutlinePaint = SerialUtilities.readPaint(stream);
        this.stripOutlineStroke = SerialUtilities.readStroke(stream);
    }

}
