

package org.jfree.chart.plot.dial;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.EventListener;


public interface DialLayer {
   
    
    public boolean isVisible();
    
    
    public void addChangeListener(DialLayerChangeListener listener);
    
    
    public void removeChangeListener(DialLayerChangeListener listener);
    
        
    public boolean hasListener(EventListener listener);
    
    
    public boolean isClippedToWindow();
    
    
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame, 
            Rectangle2D view);
    
}
