

package org.jfree.chart.entity;

import java.awt.Shape;

import org.jfree.chart.axis.Axis;


public class AxisLabelEntity extends ChartEntity {
    
    
    private Axis axis;
    
    
    public AxisLabelEntity(Axis axis, Shape hotspot, String toolTipText, 
            String url) {
        super(hotspot, toolTipText, url);
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        this.axis = axis;
    }
    
    
    public Axis getAxis() {
        return this.axis;
    }
    
    
    public String toString() {
        StringBuffer buf = new StringBuffer("AxisLabelEntity: ");
        buf.append("label = ");
        buf.append(this.axis.getLabel());
        return buf.toString();
    }


}
