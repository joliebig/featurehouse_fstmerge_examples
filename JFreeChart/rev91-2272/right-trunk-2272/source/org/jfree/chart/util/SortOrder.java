

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class SortOrder implements Serializable {

    
    private static final long serialVersionUID = -2124469847758108312L;

    
    public static final SortOrder ASCENDING = new SortOrder(
            "SortOrder.ASCENDING");

    
    public static final SortOrder DESCENDING = new SortOrder(
            "SortOrder.DESCENDING");

    
    private String name;

    
    private SortOrder(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SortOrder)) {
            return false;
        }

        final SortOrder that = (SortOrder) obj;
        if (!this.name.equals(that.toString())) {
            return false;
        }

        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(SortOrder.ASCENDING)) {
            return SortOrder.ASCENDING;
        }
        else if (this.equals(SortOrder.DESCENDING)) {
            return SortOrder.DESCENDING;
        }
        return null;
    }
}