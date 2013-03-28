

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.Size2D;


public class EmptyBlock extends AbstractBlock
        implements Block, Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -4083197869412648579L;

    
    public EmptyBlock(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        Size2D base = new Size2D(calculateTotalWidth(getWidth()),
                calculateTotalHeight(getHeight()));
        return constraint.calculateConstrainedSize(base);
    }

    
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        area = trimMargin(area);
        drawBorder(g2, area);
        return null;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
