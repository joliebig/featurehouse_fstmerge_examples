

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;


public class FlowArrangement implements Arrangement, Serializable {

    
    private static final long serialVersionUID = 4543632485478613800L;
    
    
    private HorizontalAlignment horizontalAlignment;
    
    
    private VerticalAlignment verticalAlignment;
    
    
    private double horizontalGap;
    
    
    private double verticalGap;
    
    
    public FlowArrangement() {   
        this(HorizontalAlignment.CENTER, VerticalAlignment.CENTER, 2.0, 2.0);
    }
     
    
    public FlowArrangement(HorizontalAlignment hAlign, VerticalAlignment vAlign,
                           double hGap, double vGap) {   
        this.horizontalAlignment = hAlign;
        this.verticalAlignment = vAlign;
        this.horizontalGap = hGap;
        this.verticalGap = vGap;
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
                return arrangeNF(container, g2, constraint);  
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
                return arrangeFF(container, g2, constraint);  
            }
            else if (h == LengthConstraintType.RANGE) {
                return arrangeFR(container, g2, constraint);  
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
        throw new RuntimeException("Unrecognised constraint type.");
        
    }

    
    protected Size2D arrangeFN(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {
        
        List blocks = container.getBlocks();
        double width = constraint.getWidth();
        
        double x = 0.0;
        double y = 0.0;
        double maxHeight = 0.0;
        List itemsInRow = new ArrayList();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = (Block) blocks.get(i);
            Size2D size = block.arrange(g2, RectangleConstraint.NONE);
            if (x + size.width <= width) {
                itemsInRow.add(block);
                block.setBounds(
                    new Rectangle2D.Double(x, y, size.width, size.height)
                );
                x = x + size.width + this.horizontalGap;
                maxHeight = Math.max(maxHeight, size.height);
            }
            else {
                if (itemsInRow.isEmpty()) {
                    
                    block.setBounds(
                        new Rectangle2D.Double(
                            x, y, Math.min(size.width, width - x), size.height
                        )
                    );
                    x = 0.0;
                    y = y + size.height + this.verticalGap;
                }
                else {
                    
                    itemsInRow.clear();
                    x = 0.0;
                    y = y + maxHeight + this.verticalGap;
                    maxHeight = size.height;
                    block.setBounds(
                        new Rectangle2D.Double(
                            x, y, Math.min(size.width, width), size.height
                        )
                    );
                    x = size.width + this.horizontalGap;
                    itemsInRow.add(block);
                }
            }
        }
        return new Size2D(constraint.getWidth(), y + maxHeight);  
    }
    
    
    protected Size2D arrangeFR(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {

        Size2D s = arrangeFN(container, g2, constraint);
        if (constraint.getHeightRange().contains(s.height)) {
            return s;   
        }
        else {
            RectangleConstraint c = constraint.toFixedHeight(
                constraint.getHeightRange().constrain(s.getHeight())
            );
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
                constraint.getWidthRange().getUpperBound()
            );
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
                constraint.getWidthRange().constrain(s.getWidth())
            );
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
                constraint.getWidthRange().getUpperBound()
            );
            return arrangeFN(container, g2, c);
        }
    }
    
    
    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        double x = 0.0;
        double width = 0.0;
        double maxHeight = 0.0;
        List blocks = container.getBlocks();
        int blockCount = blocks.size();
        if (blockCount > 0) {
            Size2D[] sizes = new Size2D[blocks.size()];
            for (int i = 0; i < blocks.size(); i++) {
                Block block = (Block) blocks.get(i);
                sizes[i] = block.arrange(g2, RectangleConstraint.NONE);
                width = width + sizes[i].getWidth();
                maxHeight = Math.max(sizes[i].height, maxHeight);
                block.setBounds(
                    new Rectangle2D.Double(
                        x, 0.0, sizes[i].width, sizes[i].height
                    )
                );
                x = x + sizes[i].width + this.horizontalGap;
            }
            if (blockCount > 1) {
                width = width + this.horizontalGap * (blockCount - 1);   
            }
            if (this.verticalAlignment != VerticalAlignment.TOP) {
                for (int i = 0; i < blocks.size(); i++) {
                    
                    if (this.verticalAlignment == VerticalAlignment.CENTER) {
                        
                    }
                    else if (this.verticalAlignment 
                            == VerticalAlignment.BOTTOM) {
                        
                    }
                }            
            }
        }
        return new Size2D(width, maxHeight);
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
        if (!(obj instanceof FlowArrangement)) {
            return false;   
        }
        FlowArrangement that = (FlowArrangement) obj;
        if (this.horizontalAlignment != that.horizontalAlignment) {
            return false;
        }
        if (this.verticalAlignment != that.verticalAlignment) {
            return false;
        }
        if (this.horizontalGap != that.horizontalGap) {
            return false;   
        }
        if (this.verticalGap != that.verticalGap) {
            return false;   
        }
        return true;
    }
    
}
