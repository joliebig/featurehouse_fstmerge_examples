

package org.jfree.chart.entity;

import java.awt.Shape;

import org.jfree.chart.axis.CategoryAxis;


public class CategoryLabelEntity extends TickLabelEntity {
    
    
    private Comparable key;
    
    
    public CategoryLabelEntity(Comparable key, Shape area, 
            String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        this.key = key;
    }
    
    
    public Comparable getKey() {
        return this.key;
    }
    
    
    public String toString() {
        StringBuffer buf = new StringBuffer("CategoryLabelEntity: ");
        buf.append("category=");
        buf.append(this.key);
        buf.append(", tooltip=" + getToolTipText());
        buf.append(", url=" + getURLText());
        return buf.toString();
    }
}
