

package org.jfree.data;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class DomainOrder implements Serializable {

    
    private static final long serialVersionUID = 4902774943512072627L;
    
    
    public static final DomainOrder NONE = new DomainOrder("DomainOrder.NONE");

    
    public static final DomainOrder ASCENDING 
        = new DomainOrder("DomainOrder.ASCENDING");

    
    public static final DomainOrder DESCENDING 
        = new DomainOrder("DomainOrder.DESCENDING");

    
    private String name;

    
    private DomainOrder(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DomainOrder)) {
            return false;
        }
        DomainOrder that = (DomainOrder) obj;
        if (!this.name.equals(that.toString())) {
            return false;
        }
        return true;
    }
    
    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(DomainOrder.ASCENDING)) {
            return DomainOrder.ASCENDING;
        }
        else if (this.equals(DomainOrder.DESCENDING)) {
            return DomainOrder.DESCENDING;
        }
        else if (this.equals(DomainOrder.NONE)) {
            return DomainOrder.NONE;
        }
        return null;
    }
    
}
