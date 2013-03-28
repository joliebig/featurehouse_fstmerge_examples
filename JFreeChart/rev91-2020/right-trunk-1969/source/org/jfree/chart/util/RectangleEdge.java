

package org.jfree.chart.util;

import java.awt.geom.Rectangle2D;
import java.io.ObjectStreamException;
import java.io.Serializable;


public final class RectangleEdge implements Serializable {

    
    private static final long serialVersionUID = -7400988293691093548L;

    
    public static final RectangleEdge TOP = new RectangleEdge(
            "RectangleEdge.TOP");

    
    public static final RectangleEdge BOTTOM = new RectangleEdge(
            "RectangleEdge.BOTTOM");

    
    public static final RectangleEdge LEFT = new RectangleEdge(
            "RectangleEdge.LEFT");

    
    public static final RectangleEdge RIGHT = new RectangleEdge(
            "RectangleEdge.RIGHT");

    
    private String name;

    
    private RectangleEdge(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RectangleEdge)) {
            return false;
        }

        RectangleEdge order = (RectangleEdge) obj;
        if (!this.name.equals(order.name)) {
            return false;
        }

        return true;

    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    public static boolean isTopOrBottom(RectangleEdge edge) {
        return (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM);
    }

    
    public static boolean isLeftOrRight(RectangleEdge edge) {
        return (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT);
    }

    
    public static RectangleEdge opposite(RectangleEdge edge) {
        RectangleEdge result = null;
        if (edge == RectangleEdge.TOP) {
            result = RectangleEdge.BOTTOM;
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result = RectangleEdge.TOP;
        }
        else if (edge == RectangleEdge.LEFT) {
            result = RectangleEdge.RIGHT;
        }
        else if (edge == RectangleEdge.RIGHT) {
            result = RectangleEdge.LEFT;
        }
        return result;
    }

    
    public static double coordinate(Rectangle2D rectangle,
                                    RectangleEdge edge) {
        double result = 0.0;
        if (edge == RectangleEdge.TOP) {
            result = rectangle.getMinY();
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result = rectangle.getMaxY();
        }
        else if (edge == RectangleEdge.LEFT) {
            result = rectangle.getMinX();
        }
        else if (edge == RectangleEdge.RIGHT) {
            result = rectangle.getMaxX();
        }
        return result;
    }

    
    private Object readResolve() throws ObjectStreamException {
        RectangleEdge result = null;
        if (this.equals(RectangleEdge.TOP)) {
            result = RectangleEdge.TOP;
        }
        else if (this.equals(RectangleEdge.BOTTOM)) {
            result = RectangleEdge.BOTTOM;
        }
        else if (this.equals(RectangleEdge.LEFT)) {
            result = RectangleEdge.LEFT;
        }
        else if (this.equals(RectangleEdge.RIGHT)) {
            result = RectangleEdge.RIGHT;
        }
        return result;
    }

}