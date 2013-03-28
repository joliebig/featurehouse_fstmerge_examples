


package org.jfree.data.general;

import java.io.ObjectStreamException;
import java.io.Serializable;


public class SeriesChangeType implements Serializable {

    
    public static final SeriesChangeType CHANGE_KEY
            = new SeriesChangeType("SeriesChangeType.CHANGE_KEY");

    
    public static final SeriesChangeType ADD
            = new SeriesChangeType("SeriesChangeType.ADD");

    
    public static final SeriesChangeType REMOVE
            = new SeriesChangeType("SeriesChangeType.REMOVE");

    
    public static final SeriesChangeType ADD_AND_REMOVE
            = new SeriesChangeType("SeriesChangeType.ADD_AND_REMOVE");

    
    public static final SeriesChangeType UPDATE
            = new SeriesChangeType("SeriesChangeType.UPDATE");


    
    private String name;

    
    private SeriesChangeType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SeriesChangeType)) {
            return false;
        }
        SeriesChangeType style = (SeriesChangeType) obj;
        if (!this.name.equals(style.toString())) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }

    
    private Object readResolve() throws ObjectStreamException {
        Object result = null;
        if (this.equals(SeriesChangeType.ADD)) {
            result = SeriesChangeType.ADD;
        }
        
        return result;
    }

}
