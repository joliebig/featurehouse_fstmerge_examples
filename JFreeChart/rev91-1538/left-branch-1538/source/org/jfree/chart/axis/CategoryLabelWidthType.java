

package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class CategoryLabelWidthType implements Serializable {

    
    private static final long serialVersionUID = -6976024792582949656L;

    
    public static final CategoryLabelWidthType
        CATEGORY = new CategoryLabelWidthType(
            "CategoryLabelWidthType.CATEGORY"
        );

    
    public static final CategoryLabelWidthType
        RANGE = new CategoryLabelWidthType("CategoryLabelWidthType.RANGE");

    
    private String name;

    
    private CategoryLabelWidthType(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Null 'name' argument.");
        }
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CategoryLabelWidthType)) {
            return false;
        }
        CategoryLabelWidthType t = (CategoryLabelWidthType) obj;
        if (!this.name.equals(t.toString())) {
            return false;
        }
        return true;

    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(CategoryLabelWidthType.CATEGORY)) {
            return CategoryLabelWidthType.CATEGORY;
        }
        else if (this.equals(CategoryLabelWidthType.RANGE)) {
            return CategoryLabelWidthType.RANGE;
        }
        return null;
    }

}
