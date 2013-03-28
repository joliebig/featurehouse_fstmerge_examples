

package org.jfree.chart.block;

import java.awt.Graphics2D;

import org.jfree.chart.util.Size2D;


public interface Arrangement {
    
    
    public void add(Block block, Object key);
    
    
    public Size2D arrange(BlockContainer container, 
                          Graphics2D g2,
                          RectangleConstraint constraint);

    
    public void clear();
    
}
