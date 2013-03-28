

package org.jfree.data;


public final class KeyedObjectComparatorType {

    
    public static final KeyedObjectComparatorType BY_KEY
        = new KeyedObjectComparatorType("KeyedObjectComparatorType.BY_KEY");

    
    public static final KeyedObjectComparatorType BY_VALUE
        = new KeyedObjectComparatorType("KeyedObjectComparatorType.BY_VALUE");

    
    private String name;

    
    private KeyedObjectComparatorType(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof KeyedObjectComparatorType)) {
            return false;
        }
        KeyedObjectComparatorType type = (KeyedObjectComparatorType) obj;
        if (!this.name.equals(type.name)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        return this.name.hashCode();
    }
}

