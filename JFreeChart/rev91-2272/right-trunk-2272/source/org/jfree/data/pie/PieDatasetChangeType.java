


package org.jfree.data.pie;

import java.io.ObjectStreamException;
import java.io.Serializable;


public class PieDatasetChangeType implements Serializable {

    
    public static final PieDatasetChangeType NEW
            = new PieDatasetChangeType("PieDatasetChangeType.NEW");

    
    public static final PieDatasetChangeType ADD
            = new PieDatasetChangeType("PieDatasetChangeType.ADD");

    
    public static final PieDatasetChangeType REMOVE
            = new PieDatasetChangeType("PieDatasetChangeType.REMOVE");

    
    public static final PieDatasetChangeType ADD_AND_REMOVE
            = new PieDatasetChangeType("PieDatasetChangeType.ADD_AND_REMOVE");

    
    public static final PieDatasetChangeType UPDATE
            = new PieDatasetChangeType("PieDatasetChangeType.UPDATE");

    
    public static final PieDatasetChangeType CHANGE_KEY
            = new PieDatasetChangeType("PieDatasetChangeType.ITEM_KEY");

    
    public static final PieDatasetChangeType CLEAR
            = new PieDatasetChangeType("PieDatasetChangeType.CLEAR");

    
    private String name;

    
    private PieDatasetChangeType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PieDatasetChangeType)) {
            return false;
        }
        PieDatasetChangeType pdct = (PieDatasetChangeType) obj;
        if (!this.name.equals(pdct.toString())) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        Object result = null;
        if (this.equals(PieDatasetChangeType.ADD)) {
            result = PieDatasetChangeType.ADD;
        }
        
        return result;
    }

}

