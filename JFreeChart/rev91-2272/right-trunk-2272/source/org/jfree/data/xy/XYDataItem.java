

package org.jfree.data.xy;

import java.io.Serializable;

import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.ObjectUtilities;


public class XYDataItem implements Cloneable, Comparable, Serializable {

    
    private static final long serialVersionUID = 2751513470325494890L;

    
    private Number x;

    
    private Number y;

    
    private boolean selected;

    
    public XYDataItem(Number x, Number y) {
        if (x == null) {
            throw new IllegalArgumentException("Null 'x' argument.");
        }
        this.x = x;
        this.y = y;
        this.selected = false;
    }

    
    public XYDataItem(double x, double y) {
        this(new Double(x), new Double(y));
    }

    
    public Number getX() {
        return this.x;
    }

    
    public double getXValue() {
        
        return this.x.doubleValue();
    }

    
    public Number getY() {
        return this.y;
    }

    
    public double getYValue() {
        double result = Double.NaN;
        if (this.y != null) {
            result = this.y.doubleValue();
        }
        return result;
    }

    
    public void setY(double y) {
        setY(new Double(y));
    }

    
    public void setY(Number y) {
        this.y = y;
    }

    
    public boolean isSelected() {
        return this.selected;
    }

    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof XYDataItem) {
            XYDataItem dataItem = (XYDataItem) o1;
            double compare = this.x.doubleValue()
                             - dataItem.getX().doubleValue();
            if (compare > 0.0) {
                result = 1;
            }
            else {
                if (compare < 0.0) {
                    result = -1;
                }
                else {
                    result = 0;
                }
            }
        }

        
        
        else {
            
            result = 1;
        }

        return result;

    }

    
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e) { 
            e.printStackTrace();
        }
        return clone;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDataItem)) {
            return false;
        }
        XYDataItem that = (XYDataItem) obj;
        if (!this.x.equals(that.x)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.y, that.y)) {
            return false;
        }
        if (this.selected != that.selected) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result;
        result = this.x.hashCode();
        result = 29 * result + (this.y != null ? this.y.hashCode() : 0);
        result = HashUtilities.hashCode(result, this.selected);
        return result;
    }

    
    public String toString() {
        return "[" + getXValue() + ", " + getYValue() + "]";
    }

}
