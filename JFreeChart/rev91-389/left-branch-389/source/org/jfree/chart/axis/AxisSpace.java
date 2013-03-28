
 
package org.jfree.chart.axis;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;


public class AxisSpace implements Cloneable, PublicCloneable, Serializable {
    
    
    private static final long serialVersionUID = -2490732595134766305L;
    
    
    private double top;
    
    
    private double bottom;
    
    
    private double left;
    
    
    private double right;
    
    
    public AxisSpace() {
        this.top = 0.0;
        this.bottom = 0.0;
        this.left = 0.0;
        this.right = 0.0;
    }

    
    public double getTop() {
        return this.top;
    }
    
    
    public void setTop(double space) {
        this.top = space;
    }

    
    public double getBottom() {
        return this.bottom;
    }
    
    
    public void setBottom(double space) {
        this.bottom = space;
    }

    
    public double getLeft() {
        return this.left;
    }
    
    
    public void setLeft(double space) {
        this.left = space;
    }

    
    public double getRight() {
        return this.right;
    }
    
    
    public void setRight(double space) {
        this.right = space;
    }

    
    public void add(double space, RectangleEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Null 'edge' argument.");
        }
        if (edge == RectangleEdge.TOP) {     
            this.top += space;
        }
        else if (edge == RectangleEdge.BOTTOM) {
            this.bottom += space;
        }
        else if (edge == RectangleEdge.LEFT) {
            this.left += space;
        }
        else if (edge == RectangleEdge.RIGHT) {
            this.right += space;
        }
        else {
            throw new IllegalStateException("Unrecognised 'edge' argument.");
        }
    }
    
    
    public void ensureAtLeast(AxisSpace space) {
        this.top = Math.max(this.top, space.top);
        this.bottom = Math.max(this.bottom, space.bottom);
        this.left = Math.max(this.left, space.left);
        this.right = Math.max(this.right, space.right);
    }
    
    
    public void ensureAtLeast(double space, RectangleEdge edge) {
        if (edge == RectangleEdge.TOP) {
            if (this.top < space) {
                this.top = space;
            }
        }
        else if (edge == RectangleEdge.BOTTOM) {
            if (this.bottom < space) {
                this.bottom = space;
            }
        }
        else if (edge == RectangleEdge.LEFT) {
            if (this.left < space) {
                this.left = space;
            }
        }
        else if (edge == RectangleEdge.RIGHT) {
            if (this.right < space) {
                this.right = space;
            }
        }
        else {
            throw new IllegalStateException(
                "AxisSpace.ensureAtLeast(): unrecognised AxisLocation."
            );
        }
    }
    
    
    public Rectangle2D shrink(Rectangle2D area, Rectangle2D result) {
        if (result == null) {
            result = new Rectangle2D.Double();
        }
        result.setRect(
            area.getX() + this.left, 
            area.getY() + this.top,
            area.getWidth() - this.left - this.right,
            area.getHeight() - this.top - this.bottom
        );
        return result;
    }

    
    public Rectangle2D expand(Rectangle2D area, Rectangle2D result) {
        if (result == null) {
            result = new Rectangle2D.Double();
        }
        result.setRect(
            area.getX() - this.left, 
            area.getY() - this.top,
            area.getWidth() + this.left + this.right,
            area.getHeight() + this.top + this.bottom
        );
        return result;
    }
    
    
    public Rectangle2D reserved(Rectangle2D area, RectangleEdge edge) {
        Rectangle2D result = null;
        if (edge == RectangleEdge.TOP) {
            result = new Rectangle2D.Double(
                area.getX(), area.getY(), area.getWidth(), this.top
            );
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result = new Rectangle2D.Double(
                area.getX(), area.getMaxY() - this.top,
                area.getWidth(), this.bottom
            );
        }
        else if (edge == RectangleEdge.LEFT) {
            result = new Rectangle2D.Double(
                area.getX(), area.getY(), this.left, area.getHeight()
            );
        }
        else if (edge == RectangleEdge.RIGHT) {
            result = new Rectangle2D.Double(
                area.getMaxX() - this.right, area.getY(),
                this.right, area.getHeight()
            );
        }
        return result;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    
    public boolean equals(Object obj) {       
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AxisSpace)) {
            return false;
        }
        AxisSpace that = (AxisSpace) obj;
        if (this.top != that.top) {
            return false;
        }   
        if (this.bottom != that.bottom) {
            return false;
        }
        if (this.left != that.left) {
            return false;
        }
        if (this.right != that.right) {
            return false;
        }
        return true;
    }
    
    
    public int hashCode() {
        int result = 23;
        long l = Double.doubleToLongBits(this.top);
        result = 37 * result + (int) (l ^ (l >>> 32));
        l = Double.doubleToLongBits(this.bottom);
        result = 37 * result + (int) (l ^ (l >>> 32));
        l = Double.doubleToLongBits(this.left);
        result = 37 * result + (int) (l ^ (l >>> 32));
        l = Double.doubleToLongBits(this.right);
        result = 37 * result + (int) (l ^ (l >>> 32));
        return result;
    }
    
    
    public String toString() {
        return super.toString() + "[left=" + this.left + ",right=" + this.right 
                    + ",top=" + this.top + ",bottom=" + this.bottom + "]";
    }
    
}
