

package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public class WindNeedle extends ArrowNeedle 
                                implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -2861061368907167834L;
    
    
    public WindNeedle() {
        super(false);  
    }

    
    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, 
                              Point2D rotate, double angle) {

        super.drawNeedle(g2, plotArea, rotate, angle);
        if ((rotate != null) && (plotArea != null)) {

            int spacing = getSize() * 3;
            Rectangle2D newArea = new Rectangle2D.Double();

            Point2D newRotate = rotate;
            newArea.setRect(plotArea.getMinX() - spacing, plotArea.getMinY(),
                    plotArea.getWidth(), plotArea.getHeight());
            super.drawNeedle(g2, newArea, newRotate, angle);

            newArea.setRect(plotArea.getMinX() + spacing, 
                    plotArea.getMinY(), plotArea.getWidth(), 
                    plotArea.getHeight());
            super.drawNeedle(g2, newArea, newRotate, angle);

        }
    }

    
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (super.equals(object) && object instanceof WindNeedle) {
            return true;
        }
        return false;
    }
    
    
    public int hashCode() {
        return super.hashCode();
    }

}

