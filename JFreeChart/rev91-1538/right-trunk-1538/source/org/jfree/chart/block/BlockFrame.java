

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.RectangleInsets;


public interface BlockFrame {
    
    
    public RectangleInsets getInsets();
    
    
    public void draw(Graphics2D g2, Rectangle2D area);

}
