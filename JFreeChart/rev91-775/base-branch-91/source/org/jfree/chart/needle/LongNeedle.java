

package org.jfree.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.io.Serializable;


public class LongNeedle extends MeterNeedle 
                        implements Cloneable, Serializable {

    
    private static final long serialVersionUID = -4319985779783688159L;
    
    
    public LongNeedle() {
        super();
        setRotateY(0.8);
    }

    
    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, 
                              Point2D rotate, double angle) {

        GeneralPath shape1 = new GeneralPath();
        GeneralPath shape2 = new GeneralPath();
        GeneralPath shape3 = new GeneralPath();

        float minX = (float) plotArea.getMinX();
        float minY = (float) plotArea.getMinY();
        float maxX = (float) plotArea.getMaxX();
        float maxY = (float) plotArea.getMaxY();
        
        
        float midX = (float) (minX + (plotArea.getWidth() * 0.5));
        float midY = (float) (minY + (plotArea.getHeight() * 0.8));
        float y = maxY - (2 * (maxY - midY));
        if (y < minY) {
            y = minY;
        }
        shape1.moveTo(minX, midY);
        shape1.lineTo(midX, minY);
        shape1.lineTo(midX, y);
        shape1.closePath();

        shape2.moveTo(maxX, midY);
        shape2.lineTo(midX, minY);
        shape2.lineTo(midX, y);
        shape2.closePath();

        shape3.moveTo(minX, midY);
        shape3.lineTo(midX, maxY);
        shape3.lineTo(maxX, midY);
        shape3.lineTo(midX, y);
        shape3.closePath();

        Shape s1 = shape1;
        Shape s2 = shape2;
        Shape s3 = shape3;

        if ((rotate != null) && (angle != 0)) {
            
            getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            s1 = shape1.createTransformedShape(transform);
            s2 = shape2.createTransformedShape(transform);
            s3 = shape3.createTransformedShape(transform);
        }


        if (getHighlightPaint() != null) {
            g2.setPaint(getHighlightPaint());
            g2.fill(s3);
        }

        if (getFillPaint() != null) {
            g2.setPaint(getFillPaint());
            g2.fill(s1);
            g2.fill(s2);
        }


        if (getOutlinePaint() != null) {
            g2.setStroke(getOutlineStroke());
            g2.setPaint(getOutlinePaint());
            g2.draw(s1);
            g2.draw(s2);
            g2.draw(s3);
        }
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LongNeedle)) {
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
