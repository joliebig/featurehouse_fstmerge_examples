

package org.jfree.chart.util;


public class BooleanList extends AbstractObjectList {

    
    private static final long serialVersionUID = -8543170333219422042L;

    
    public BooleanList() {
    }

    
    public Boolean getBoolean(int index) {
        return (Boolean) get(index);
    }

    
    public void setBoolean(int index, Boolean b) {
        set(index, b);
    }

    
    public boolean equals(Object obj) {
        if (obj instanceof BooleanList) {
            return super.equals(obj);
        }
        return false;
    }

    
    public int hashCode() {
        return super.hashCode();
    }
}
