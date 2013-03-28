

package org.jfree.chart.text;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.util.HorizontalAlignment;
import org.jfree.chart.util.ShapeUtilities;
import org.jfree.chart.util.Size2D;


public class TextBlock implements Serializable {

    
    private static final long serialVersionUID = -4333175719424385526L;

    
    private List lines;

    
    private HorizontalAlignment lineAlignment;

    
    public TextBlock() {
        this.lines = new java.util.ArrayList();
        this.lineAlignment = HorizontalAlignment.CENTER;
    }

    
    public HorizontalAlignment getLineAlignment() {
        return this.lineAlignment;
    }

    
    public void setLineAlignment(HorizontalAlignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Null 'alignment' argument.");
        }
        this.lineAlignment = alignment;
    }

    
    public void addLine(String text, Font font, Paint paint) {
        addLine(new TextLine(text, font, paint));
    }

    
    public void addLine(TextLine line) {
        this.lines.add(line);
    }

    
    public TextLine getLastLine() {
        TextLine last = null;
        final int index = this.lines.size() - 1;
        if (index >= 0) {
            last = (TextLine) this.lines.get(index);
        }
        return last;
    }

    
    public List getLines() {
        return Collections.unmodifiableList(this.lines);
    }

    
    public Size2D calculateDimensions(Graphics2D g2) {
        double width = 0.0;
        double height = 0.0;
        Iterator iterator = this.lines.iterator();
        while (iterator.hasNext()) {
            TextLine line = (TextLine) iterator.next();
            Size2D dimension = line.calculateDimensions(g2);
            width = Math.max(width, dimension.getWidth());
            height = height + dimension.getHeight();
        }
        return new Size2D(width, height);
    }

    
    public Shape calculateBounds(Graphics2D g2, float anchorX, float anchorY,
            TextBlockAnchor anchor, float rotateX, float rotateY,
            double angle) {

        Size2D d = calculateDimensions(g2);
        float[] offsets = calculateOffsets(anchor, d.getWidth(), d.getHeight());
        Rectangle2D bounds = new Rectangle2D.Double(anchorX + offsets[0],
                anchorY + offsets[1], d.getWidth(), d.getHeight());
        Shape rotatedBounds = ShapeUtilities.rotateShape(bounds, angle,
                rotateX, rotateY);
        return rotatedBounds;

    }

    
    public void draw(Graphics2D g2, float x, float y, TextBlockAnchor anchor) {
        draw(g2, x, y, anchor, 0.0f, 0.0f, 0.0);
    }

    
    public void draw(Graphics2D g2, float anchorX, float anchorY,
                     TextBlockAnchor anchor, float rotateX, float rotateY,
                     double angle) {

        Size2D d = calculateDimensions(g2);
        float[] offsets = calculateOffsets(anchor, d.getWidth(), d.getHeight());
        Iterator iterator = this.lines.iterator();
        float yCursor = 0.0f;
        while (iterator.hasNext()) {
            TextLine line = (TextLine) iterator.next();
            Size2D dimension = line.calculateDimensions(g2);
            float lineOffset = 0.0f;
            if (this.lineAlignment == HorizontalAlignment.CENTER) {
                lineOffset = (float) (d.getWidth() - dimension.getWidth())
                    / 2.0f;
            }
            else if (this.lineAlignment == HorizontalAlignment.RIGHT) {
                lineOffset = (float) (d.getWidth() - dimension.getWidth());
            }
            line.draw(g2, anchorX + offsets[0] + lineOffset, anchorY
                    + offsets[1] + yCursor, TextAnchor.TOP_LEFT, rotateX,
                    rotateY, angle);
            yCursor = yCursor + (float) dimension.getHeight();
        }

    }

    
    private float[] calculateOffsets(TextBlockAnchor anchor,
                                     double width, double height) {
        float[] result = new float[2];
        float xAdj = 0.0f;
        float yAdj = 0.0f;

        if (anchor == TextBlockAnchor.TOP_CENTER
                || anchor == TextBlockAnchor.CENTER
                || anchor == TextBlockAnchor.BOTTOM_CENTER) {

            xAdj = (float) -width / 2.0f;

        }
        else if (anchor == TextBlockAnchor.TOP_RIGHT
                || anchor == TextBlockAnchor.CENTER_RIGHT
                || anchor == TextBlockAnchor.BOTTOM_RIGHT) {

            xAdj = (float) -width;

        }

        if (anchor == TextBlockAnchor.TOP_LEFT
                || anchor == TextBlockAnchor.TOP_CENTER
                || anchor == TextBlockAnchor.TOP_RIGHT) {

            yAdj = 0.0f;

        }
        else if (anchor == TextBlockAnchor.CENTER_LEFT
                || anchor == TextBlockAnchor.CENTER
                || anchor == TextBlockAnchor.CENTER_RIGHT) {

            yAdj = (float) -height / 2.0f;

        }
        else if (anchor == TextBlockAnchor.BOTTOM_LEFT
                || anchor == TextBlockAnchor.BOTTOM_CENTER
                || anchor == TextBlockAnchor.BOTTOM_RIGHT) {

            yAdj = (float) -height;

        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TextBlock) {
            final TextBlock block = (TextBlock) obj;
            return this.lines.equals(block.lines);
        }
        return false;
    }

    
    public int hashCode() {
        return (this.lines != null ? this.lines.hashCode() : 0);
    }
}
