

package org.jfree.data;

import java.io.Serializable;


public class SelectableValue implements Value, Cloneable, Serializable {

    
    private Number value;

    
    private boolean selected;

    
    public SelectableValue(Number value) {
        this(value, false);
    }

    
    public SelectableValue(Number value, boolean selected) {
        this.value = value;
        this.selected = selected;
    }

    
    public Number getValue() {
        return this.value;
    }

    
    public boolean isSelected() {
        return this.selected;
    }

    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
