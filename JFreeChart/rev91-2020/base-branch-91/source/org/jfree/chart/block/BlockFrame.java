

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.ui.RectangleInsets;
import org.jfree.util.PublicCloneable;


public interface BlockFrame {
    
    
    public RectangleInsets getInsets();
    
    
    public void draw(Graphics2D g2, Rectangle2D area);

}
