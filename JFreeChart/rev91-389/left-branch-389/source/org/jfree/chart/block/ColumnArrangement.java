

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Size2D;
import org.jfree.ui.VerticalAlignment;


public class ColumnArrangement implements Arrangement, Serializable {

    
    private static final long serialVersionUID = -5315388482898581555L;
    
    
    private HorizontalAlignment horizontalAlignment;
    
    
    private VerticalAlignment verticalAlignment;
    
    
    private double horizontalGap;
    
    
    private double verticalGap;
    
    
    public ColumnArrangement() {   
    }
    
    
    public ColumnArrangement(HorizontalAlignment hAlign, 
                             VerticalAlignment vAlign,
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
                throw new RuntimeException("Not implemented.");  
            }
            else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");  
            }
        }
        else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not implemented.");  
            }
            else if (h == LengthConstraintType.FIXED) {
                return arrangeFF(container, g2, constraint); 
            }
            else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not implemented.");  
            }
        }
        else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not implemented.");  
            }
            else if (h == LengthConstraintType.FIXED) {
                return arrangeRF(container, g2, constraint);  
            }
            else if (h == LengthConstraintType.RANGE) {
                return arrangeRR(container, g2, constraint);  
            }
        }
        return new Size2D();  
        
    }

    
    protected Size2D arrangeFF(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {
        
        return arrangeNF(container, g2, constraint);
    }
    
    
    protected Size2D arrangeNF(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {
    
        List blocks = container.getBlocks();
        
        double height = constraint.getHeight();
        if (height <= 0.0) {
            height = Double.POSITIVE_INFINITY;
        }
        
        double x = 0.0;
        double y = 0.0;
        double maxWidth = 0.0;
        List itemsInColumn = new ArrayList();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = (Block) blocks.get(i);
            Size2D size = block.arrange(g2, RectangleConstraint.NONE);
            if (y + size.height <= height) {
                itemsInColumn.add(block);
                block.setBounds(
                    new Rectangle2D.Double(x, y, size.width, size.height)
                );
                y = y + size.height + this.verticalGap;
                maxWidth = Math.max(maxWidth, size.width);
            }
            else {
                if (itemsInColumn.isEmpty()) {
                    
                    block.setBounds(
                        new Rectangle2D.Double(
                            x, y, size.width, Math.min(size.height, height - y)
                        )
                    );
                    y = 0.0;
                    x = x + size.width + this.horizontalGap;
                }
                else {
                    
                    itemsInColumn.clear();
                    x = x + maxWidth + this.horizontalGap;
                    y = 0.0;
                    maxWidth = size.width;
                    block.setBounds(
                        new Rectangle2D.Double(
                            x, y, size.width, Math.min(size.height, height)
                        )
                    );
                    y = size.height + this.verticalGap;
                    itemsInColumn.add(block);
                }
            }
        }
        return new Size2D(x + maxWidth, constraint.getHeight());  
    }

    protected Size2D arrangeRR(BlockContainer container, Graphics2D g2,
                               RectangleConstraint constraint) {

        
        
        Size2D s1 = arrangeNN(container, g2);
        if (constraint.getHeightRange().contains(s1.height)) {
            return s1;  
        }
        else {
            RectangleConstraint c = constraint.toFixedHeight(
                constraint.getHeightRange().getUpperBound()
            );
            return arrangeRF(container, g2, c);
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

    
    protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
        double y = 0.0;
        double height = 0.0;
        double maxWidth = 0.0;
        List blocks = container.getBlocks();
        int blockCount = blocks.size();
        if (blockCount > 0) {
            Size2D[] sizes = new Size2D[blocks.size()];
            for (int i = 0; i < blocks.size(); i++) {
                Block block = (Block) blocks.get(i);
                sizes[i] = block.arrange(g2, RectangleConstraint.NONE);
                height = height + sizes[i].getHeight();
                maxWidth = Math.max(sizes[i].width, maxWidth);
                block.setBounds(
                    new Rectangle2D.Double(
                        0.0, y, sizes[i].width, sizes[i].height
                    )
                );
                y = y + sizes[i].height + this.verticalGap;
            }
            if (blockCount > 1) {
                height = height + this.verticalGap * (blockCount - 1);   
            }
            if (this.horizontalAlignment != HorizontalAlignment.LEFT) {
                for (int i = 0; i < blocks.size(); i++) {
                    
                    if (this.horizontalAlignment 
                            == HorizontalAlignment.CENTER) {
                        
                    }
                    else if (this.horizontalAlignment 
                            == HorizontalAlignment.RIGHT) {
                        
                    }
                }            
            }
        }
        return new Size2D(maxWidth, height);
    }

    
    public void clear() {
        
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof ColumnArrangement)) {
            return false;   
        }
        ColumnArrangement that = (ColumnArrangement) obj;
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
