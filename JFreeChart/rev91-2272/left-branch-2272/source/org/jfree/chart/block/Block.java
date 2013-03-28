

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.ui.Drawable;
import org.jfree.ui.Size2D;


public interface Block extends Drawable {

    
    public String getID();

    
    public void setID(String id);

    
    public Size2D arrange(Graphics2D g2);

    
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint);

    
    public Rectangle2D getBounds();

    
    public void setBounds(Rectangle2D bounds);

    
    public Object draw(Graphics2D g2, Rectangle2D area, Object params);

}
