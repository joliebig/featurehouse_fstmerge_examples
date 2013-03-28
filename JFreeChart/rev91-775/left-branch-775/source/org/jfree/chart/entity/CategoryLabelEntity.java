

package org.jfree.chart.entity;

import java.awt.Shape;

import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.util.ObjectUtilities;


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
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryLabelEntity)) {
            return false;
        }
        CategoryLabelEntity that = (CategoryLabelEntity) obj;
        if (!ObjectUtilities.equal(this.key, that.key)) {
            return false;
        }
        return super.equals(obj);
    }
    
    
    public int hashCode() {
        int result = super.hashCode();
        result = HashUtilities.hashCode(result, this.key);
        return result;
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
