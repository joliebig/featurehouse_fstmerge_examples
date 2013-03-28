

package org.jfree.data.xy;

import org.jfree.data.ComparableObjectItem;


public class VectorDataItem extends ComparableObjectItem {

    
    public VectorDataItem(double x, double y, double deltaX, double deltaY) {
        super(new XYCoordinate(x, y), new Vector(deltaX, deltaY));
    }
    
    
    public double getXValue() {
        XYCoordinate xy = (XYCoordinate) getComparable();
        return xy.getX();
    }
    
    
    public double getYValue() {
        XYCoordinate xy = (XYCoordinate) getComparable();
        return xy.getY();
    }
    
    
    public Vector getVector() {
        return (Vector) getObject();
    }
    
    
    public double getVectorX() {
        Vector vi = (Vector) getObject();
        if (vi != null) {
            return vi.getX();
        }
        else {
            return Double.NaN;
        }
    }
    
    
    public double getVectorY() {
        Vector vi = (Vector) getObject();
        if (vi != null) {
            return vi.getY();
        }
        else {
            return Double.NaN;
        }
    }
    
}
