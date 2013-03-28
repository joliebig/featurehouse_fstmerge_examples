

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.util.PaintUtilities;
import org.jfree.chart.util.SerialUtilities;
import org.jfree.chart.util.Size2D;


public class ColorBlock extends AbstractBlock implements Block {

    
    static final long serialVersionUID = 3383866145634010865L;

    
    private transient Paint paint;

    
    public ColorBlock(Paint paint, double width, double height) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.paint = paint;
        setWidth(width);
        setHeight(height);
    }

    
    public Paint getPaint() {
        return this.paint;
    }

    
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        return new Size2D(calculateTotalWidth(getWidth()),
                calculateTotalHeight(getHeight()));
    }

    
    public void draw(Graphics2D g2, Rectangle2D area) {
        area = trimMargin(area);
        drawBorder(g2, area);
        area = trimBorder(area);
        area = trimPadding(area);
        g2.setPaint(this.paint);
        g2.fill(area);
    }

    
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        draw(g2, area);
        return null;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ColorBlock)) {
            return false;
        }
        ColorBlock that = (ColorBlock) obj;
        if (!PaintUtilities.equal(this.paint, that.paint)) {
            return false;
        }
        return super.equals(obj);
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
    }

    
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
    }

}
