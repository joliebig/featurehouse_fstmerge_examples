

package org.jfree.data;

import java.io.Serializable;

import org.jfree.chart.util.ObjectUtilities;


public class ComparableObjectItem implements Cloneable, Comparable, 
        Serializable {

    private static final long serialVersionUID = 2751513470325494890L;
    
    
    private Comparable x;

    
    private Object obj;

    
    public ComparableObjectItem(Comparable x, Object y) {
        if (x == null) {
            throw new IllegalArgumentException("Null 'x' argument.");
        }
        this.x = x;
        this.obj = y;
    }

    
    protected Comparable getComparable() {
        return this.x;
    }

    
    protected Object getObject() {
        return this.obj;
    }

    
    protected void setObject(Object y) {
        this.obj = y;
    }

    
    public int compareTo(Object o1) {

        int result;

        
        
        if (o1 instanceof ComparableObjectItem) {
            ComparableObjectItem that = (ComparableObjectItem) o1;
            return this.x.compareTo(that.x);
        }

        
        
        else {
            
            result = 1;
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
        if (!(obj instanceof ComparableObjectItem)) {
            return false;
        }
        ComparableObjectItem that = (ComparableObjectItem) obj;
        if (!this.x.equals(that.x)) {
            return false;
        }
        if (!ObjectUtilities.equal(this.obj, that.obj)) {
            return false;
        }
        return true;        
    }

    
    public int hashCode() {
        int result;
        result = this.x.hashCode();
        result = 29 * result + (this.obj != null ? this.obj.hashCode() : 0);
        return result;
    }
    
}
