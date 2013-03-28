

package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public class PinNeedle extends MeterNeedle 
                       implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -3787089953079863373L;
    
    
    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, 
                              Point2D rotate, double angle) {

        Area shape;
        GeneralPath pointer = new GeneralPath();

        int minY = (int) (plotArea.getMinY());
        
        int maxY = (int) (plotArea.getMaxY());
        int midX = (int) (plotArea.getMinX() + (plotArea.getWidth() / 2));
        
        int lenX = (int) (plotArea.getWidth() / 10);
        if (lenX < 2) {
            lenX = 2;
        }

        pointer.moveTo(midX - lenX, maxY - lenX);
        pointer.lineTo(midX + lenX, maxY - lenX);
        pointer.lineTo(midX, minY + lenX);
        pointer.closePath();

        lenX = 4 * lenX;
        Ellipse2D circle = new Ellipse2D.Double(midX - lenX / 2, 
                plotArea.getMaxY() - lenX, lenX, lenX);

        shape = new Area(circle);
        shape.add(new Area(pointer));
        if ((rotate != null) && (angle != 0)) {
            
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            shape.transform(getTransform());
        }

        defaultDisplay(g2, shape);

    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PinNeedle)) {
            return false;   
        }
        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();   
    }

}
