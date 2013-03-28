

package org.jfree.chart.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ObjectStreamException;
import java.io.Serializable;


public final class RectangleAnchor implements Serializable {

    
    private static final long serialVersionUID = -2457494205644416327L;

    
    public static final RectangleAnchor CENTER
        = new RectangleAnchor("RectangleAnchor.CENTER");

    
    public static final RectangleAnchor TOP
        = new RectangleAnchor("RectangleAnchor.TOP");

    
    public static final RectangleAnchor TOP_LEFT
        = new RectangleAnchor("RectangleAnchor.TOP_LEFT");

    
    public static final RectangleAnchor TOP_RIGHT
        = new RectangleAnchor("RectangleAnchor.TOP_RIGHT");

    
    public static final RectangleAnchor BOTTOM
        = new RectangleAnchor("RectangleAnchor.BOTTOM");

    
    public static final RectangleAnchor BOTTOM_LEFT
        = new RectangleAnchor("RectangleAnchor.BOTTOM_LEFT");

    
    public static final RectangleAnchor BOTTOM_RIGHT
        = new RectangleAnchor("RectangleAnchor.BOTTOM_RIGHT");

    
    public static final RectangleAnchor LEFT
        = new RectangleAnchor("RectangleAnchor.LEFT");

    
    public static final RectangleAnchor RIGHT
        = new RectangleAnchor("RectangleAnchor.RIGHT");

    
    private String name;

    
    private RectangleAnchor(final String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RectangleAnchor)) {
            return false;
        }

        final RectangleAnchor order = (RectangleAnchor) obj;
        if (!this.name.equals(order.name)) {
            return false;
        }

        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    public static Point2D coordinates(final Rectangle2D rectangle,
                                      final RectangleAnchor anchor) {
        Point2D result = new Point2D.Double();
        if (anchor == RectangleAnchor.CENTER) {
            result.setLocation(rectangle.getCenterX(), rectangle.getCenterY());
        }
        else if (anchor == RectangleAnchor.TOP) {
            result.setLocation(rectangle.getCenterX(), rectangle.getMinY());
        }
        else if (anchor == RectangleAnchor.BOTTOM) {
            result.setLocation(rectangle.getCenterX(), rectangle.getMaxY());
        }
        else if (anchor == RectangleAnchor.LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getCenterY());
        }
        else if (anchor == RectangleAnchor.RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getCenterY());
        }
        else if (anchor == RectangleAnchor.TOP_LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getMinY());
        }
        else if (anchor == RectangleAnchor.TOP_RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getMinY());
        }
        else if (anchor == RectangleAnchor.BOTTOM_LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getMaxY());
        }
        else if (anchor == RectangleAnchor.BOTTOM_RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getMaxY());
        }
        return result;
    }

    
    public static Rectangle2D createRectangle(final Size2D dimensions,
                                              final double anchorX,
                                              final double anchorY,
                                              final RectangleAnchor anchor) {
        Rectangle2D result = null;
        final double w = dimensions.getWidth();
        final double h = dimensions.getHeight();
        if (anchor == RectangleAnchor.CENTER) {
            result = new Rectangle2D.Double(
                anchorX - w / 2.0, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.TOP) {
            result = new Rectangle2D.Double(
                anchorX - w / 2.0, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.BOTTOM) {
            result = new Rectangle2D.Double(
                anchorX - w / 2.0, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.LEFT) {
            result = new Rectangle2D.Double(
                anchorX, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.RIGHT) {
            result = new Rectangle2D.Double(
                anchorX - w, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.TOP_LEFT) {
            result = new Rectangle2D.Double(
                anchorX - w / 2.0, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.TOP_RIGHT) {
            result = new Rectangle2D.Double(
                anchorX - w / 2.0, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.BOTTOM_LEFT) {
            result = new Rectangle2D.Double(
                anchorX - w / 2.0, anchorY - h / 2.0, w, h
            );
        }
        else if (anchor == RectangleAnchor.BOTTOM_RIGHT) {
            result = new Rectangle2D.Double(
                anchorX - w / 2.0, anchorY - h / 2.0, w, h
            );
        }
        return result;
    }

    
    private Object readResolve() throws ObjectStreamException {
        RectangleAnchor result = null;
        if (this.equals(RectangleAnchor.CENTER)) {
            result = RectangleAnchor.CENTER;
        }
        else if (this.equals(RectangleAnchor.TOP)) {
            result = RectangleAnchor.TOP;
        }
        else if (this.equals(RectangleAnchor.BOTTOM)) {
            result = RectangleAnchor.BOTTOM;
        }
        else if (this.equals(RectangleAnchor.LEFT)) {
            result = RectangleAnchor.LEFT;
        }
        else if (this.equals(RectangleAnchor.RIGHT)) {
            result = RectangleAnchor.RIGHT;
        }
        else if (this.equals(RectangleAnchor.TOP_LEFT)) {
            result = RectangleAnchor.TOP_LEFT;
        }
        else if (this.equals(RectangleAnchor.TOP_RIGHT)) {
            result = RectangleAnchor.TOP_RIGHT;
        }
        else if (this.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = RectangleAnchor.BOTTOM_LEFT;
        }
        else if (this.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = RectangleAnchor.BOTTOM_RIGHT;
        }
        return result;
    }

}