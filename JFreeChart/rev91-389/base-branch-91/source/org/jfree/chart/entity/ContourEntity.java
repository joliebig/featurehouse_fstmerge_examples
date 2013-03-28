

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;


public class ContourEntity extends ChartEntity 
                           implements Cloneable, Serializable {

    
    private static final long serialVersionUID = 1249570520505992847L;
    
    
    private int index = -1;

    
    public ContourEntity(Shape area, String toolTipText) {
        super(area, toolTipText);
    }

    
    public ContourEntity(Shape area, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
    }

    
    public int getIndex() {
        return this.index;
    }

    
    public void setIndex(int index) {
        this.index = index;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof ContourEntity && super.equals(obj)) {
            ContourEntity ce = (ContourEntity) obj;
            if (this.index != ce.index) {
                return false;   
            }
            return true;
        }
        return false;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
