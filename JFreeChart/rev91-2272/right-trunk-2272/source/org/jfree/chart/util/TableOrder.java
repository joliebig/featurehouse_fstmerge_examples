

package org.jfree.chart.util;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class TableOrder implements Serializable {

    
    private static final long serialVersionUID = 525193294068177057L;

    
    public static final TableOrder BY_ROW = new TableOrder("TableOrder.BY_ROW");

    
    public static final TableOrder BY_COLUMN
            = new TableOrder("TableOrder.BY_COLUMN");

    
    private String name;

    
    private TableOrder(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TableOrder)) {
            return false;
        }
        TableOrder that = (TableOrder) obj;
        if (!this.name.equals(that.name)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(TableOrder.BY_ROW)) {
            return TableOrder.BY_ROW;
        }
        else if (this.equals(TableOrder.BY_COLUMN)) {
            return TableOrder.BY_COLUMN;
        }
        return null;
    }

}