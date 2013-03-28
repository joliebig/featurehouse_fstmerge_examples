

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import org.jfree.ui.Size2D;


public class CenterArrangement implements Arrangement, Serializable {

    
    private static final long serialVersionUID = -353308149220382047L;

    
    public CenterArrangement() {
    }

    
    public void add(Block block, Object key) {
        
        
    }

    
    public Size2D arrange(BlockContainer container, Graphics2D g2,
                          RectangleConstraint constraint) {

        LengthConstraintType w = constraint.getWidthConstraintType();
        LengthConstraintType h = constraint.getHeightConstraintType();
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                return arrangeNN(container, g2);
            }
            else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            }
            else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        }
        else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                return arrangeFN(container, g2, constraint);
            }
            else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not implemented.");
            }
            else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");
            }
        }
        else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                return arrangeRN(container, g2, constraint);
            }
            else if (h == LengthConstraintType.FIXED) {
                return arrangeRF(container, g2, constraint);
            }
            else if (h == LengthConstraintType.RANGE) {
                return arrangeRR(container, g2, constraint);
            }
        }
        throw new IllegalArgumentException("Unknown LengthConstraintType.");

    }

    
    protected Size2D arrangeFN(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {

        List blocks = container.getBlocks();
        Block b = (Block) blocks.get(0);
        Size2D s = b.arrange(g2, RectangleConstraint.NONE);
        double width = constraint.getWidth();
        Rectangle2D bounds = new Rectangle2D.Double((width - s.width) / 2.0,
                0.0, s.width, s.height);
        b.setBounds(bounds);
        return new Size2D((width - s.width) / 2.0, s.height);
    }

    
    protected Size2D arrangeFR(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {

        Size2D s = arrangeFN(container, g2, constraint);
        if (constraint.getHeightRange().contains(s.height)) {
            return s;
        }
        else {
            RectangleConstraint c = constraint.toFixedHeight(
                    constraint.getHeightRange().constrain(s.getHeight()));
            return arrangeFF(container, g2, c);
        }
    }

    
    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {

        
        return arrangeFN(container, g2, constraint);
    }

    
    protected Size2D arrangeRR(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {

        
        
        Size2D s1 = arrangeNN(container, g2);
        if (constraint.getWidthRange().contains(s1.width)) {
            return s1;  
        }
        else {
            RectangleConstraint c = constraint.toFixedWidth(
                    constraint.getWidthRange().getUpperBound());
            return arrangeFR(container, g2, c);
        }
    }

    
    protected Size2D arrangeRF(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {

        Size2D s = arrangeNF(container, g2, constraint);
        if (constraint.getWidthRange().contains(s.width)) {
            return s;
        }
        else {
            RectangleConstraint c = constraint.toFixedWidth(
                    constraint.getWidthRange().constrain(s.getWidth()));
            return arrangeFF(container, g2, c);
        }
    }

    
    protected Size2D arrangeRN(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {
        
        
        Size2D s1 = arrangeNN(container, g2);
        if (constraint.getWidthRange().contains(s1.width)) {
            return s1;
        }
        else {
            RectangleConstraint c = constraint.toFixedWidth(
                    constraint.getWidthRange().getUpperBound());
            return arrangeFN(container, g2, c);
        }
    }

    
    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        List blocks = container.getBlocks();
        Block b = (Block) blocks.get(0);
        Size2D s = b.arrange(g2, RectangleConstraint.NONE);
        b.setBounds(new Rectangle2D.Double(0.0, 0.0, s.width, s.height));
        return new Size2D(s.width, s.height);
    }

    
    protected Size2D arrangeNF(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {
        
        return arrangeNN(container, g2);
    }

    
    public void clear() {
        
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CenterArrangement)) {
            return false;
        }
        return true;
    }

}
