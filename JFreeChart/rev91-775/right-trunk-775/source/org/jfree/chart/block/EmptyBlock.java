

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.util.PublicCloneable;


public class EmptyBlock extends AbstractBlock 
                        implements Block, Cloneable, PublicCloneable,
                                   Serializable {
    
    
    private static final long serialVersionUID = -4083197869412648579L;
    
    
    public EmptyBlock(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    
    public void draw(Graphics2D g2, Rectangle2D area) {
        
    }
    
    
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        return null;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();   
    }

}
