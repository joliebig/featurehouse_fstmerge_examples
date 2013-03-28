

package org.jfree.chart.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.RectangleAnchor;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.Size2D;


public class TextBox implements Serializable {

    
    private static final long serialVersionUID = 3360220213180203706L;

    
    private transient Paint outlinePaint;

    
    private transient Stroke outlineStroke;

    
    private RectangleInsets interiorGap;

    
    private transient Paint backgroundPaint;

    
    private transient Paint shadowPaint;

    
    private double shadowXOffset = 2.0;

    
    private double shadowYOffset = 2.0;

    
    private TextBlock textBlock;

    
    public TextBox() {
        this((TextBlock) null);
    }

    
    public TextBox(String text) {
        this((TextBlock) null);
        if (text != null) {
            this.textBlock = new TextBlock();
            this.textBlock.addLine(
                text, new Font("SansSerif", Font.PLAIN, 10),
                Color.black
            );
        }
    }

    
    public TextBox(TextBlock block) {
        this.outlinePaint = Color.black;
        this.outlineStroke = new BasicStroke(1.0f);
        this.interiorGap = new RectangleInsets(1.0, 3.0, 1.0, 3.0);
        this.backgroundPaint = new Color(255, 255, 192);
        this.shadowPaint = Color.gray;
        this.shadowXOffset = 2.0;
        this.shadowYOffset = 2.0;
        this.textBlock = block;
    }

    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    
    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
    }

    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    
    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
    }

    
    public RectangleInsets getInteriorGap() {
        return this.interiorGap;
    }

    
    public void setInteriorGap(RectangleInsets gap) {
        this.interiorGap = gap;
    }

    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    
    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
    }

    
    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    
    public void setShadowPaint(Paint paint) {
        this.shadowPaint = paint;
    }

    
    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    
    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
    }

    
    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    
    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
    }

    
    public TextBlock getTextBlock() {
        return this.textBlock;
    }

    
    public void setTextBlock(TextBlock block) {
        this.textBlock = block;
    }

    
    public void draw(Graphics2D g2, float x, float y, RectangleAnchor anchor) {
        Size2D d1 = this.textBlock.calculateDimensions(g2);
        double w = this.interiorGap.extendWidth(d1.getWidth());
        double h = this.interiorGap.extendHeight(d1.getHeight());
        Size2D d2 = new Size2D(w, h);
        Rectangle2D bounds = RectangleAnchor.createRectangle(d2, x, y, anchor);
        double xx = bounds.getX();
        double yy = bounds.getY();

        if (this.shadowPaint != null) {
            Rectangle2D shadow = new Rectangle2D.Double(
                    xx + this.shadowXOffset, yy + this.shadowYOffset,
                    bounds.getWidth(), bounds.getHeight());
            g2.setPaint(this.shadowPaint);
            g2.fill(shadow);
        }
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(bounds);
        }

        if (this.outlinePaint != null && this.outlineStroke != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.outlineStroke);
            g2.draw(bounds);
        }

        this.textBlock.draw(g2,
                (float) (xx + this.interiorGap.calculateLeftInset(w)),
                (float) (yy + this.interiorGap.calculateTopInset(h)),
                TextBlockAnchor.TOP_LEFT);

    }

    
    public double getHeight(Graphics2D g2) {
        Size2D d = this.textBlock.calculateDimensions(g2);
        return this.interiorGap.extendHeight(d.getHeight());
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TextBox)) {
            return false;
        }
        final TextBox that = (TextBox) obj;
        if (!ObjectUtilities.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.interiorGap, that.interiorGap)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.backgroundPaint,
                that.backgroundPaint)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (this.shadowXOffset != that.shadowXOffset) {
            return false;
        }
        if (this.shadowYOffset != that.shadowYOffset) {
            return false;
        }
        if (!ObjectUtilities.equal(this.textBlock, that.textBlock)) {
            return false;
        }

        return true;
    }

    
    public int hashCode() {
        int result;
        long temp;
        result = (this.outlinePaint != null ? this.outlinePaint.hashCode() : 0);
        result = 29 * result + (this.outlineStroke != null
                ? this.outlineStroke.hashCode() : 0);
        result = 29 * result + (this.interiorGap != null
                ? this.interiorGap.hashCode() : 0);
        result = 29 * result + (this.backgroundPaint != null
                ? this.backgroundPaint.hashCode() : 0);
        result = 29 * result + (this.shadowPaint != null
                ? this.shadowPaint.hashCode() : 0);
        temp = this.shadowXOffset != +0.0d
                ? Double.doubleToLongBits(this.shadowXOffset) : 0L;
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        temp = this.shadowYOffset != +0.0d
                ? Double.doubleToLongBits(this.shadowYOffset) : 0L;
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        result = 29 * result + (this.textBlock != null
                ? this.textBlock.hashCode() : 0);
        return result;
    }

    
    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.shadowPaint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.shadowPaint = SerialUtilities.readPaint(stream);
    }


}
