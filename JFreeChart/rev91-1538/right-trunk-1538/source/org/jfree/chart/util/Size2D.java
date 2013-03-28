

package org.jfree.chart.util;

import java.io.Serializable;


public class Size2D implements Cloneable, PublicCloneable, Serializable {

     
    private static final long serialVersionUID = 2558191683786418168L;
    
    
    public double width;

    
    public double height;

    
    public Size2D() {
        this(0.0, 0.0);
    }
    
    
    public Size2D(double width, double height) {
        this.width = width;
        this.height = height;
    }

    
    public double getWidth() {
        return this.width;
    }
    
    
    public void setWidth(double width) {
        this.width = width;
    }

    
    public double getHeight() {
        return this.height;
    }
    
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    
    public String toString() {
        return "Size2D[width=" + this.width + ", height=" + this.height + "]";   
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Size2D)) {
            return false;
        }
        final Size2D that = (Size2D) obj;
        if (this.width != that.width) {
            return false;
        }
        if (this.height != that.height) {
            return false;
        }
        return true;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
