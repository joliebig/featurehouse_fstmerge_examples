

package org.jfree.chart.labels;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class ItemLabelAnchor implements Serializable {

    
    private static final long serialVersionUID = -1233101616128695658L;
    
    
    public static final ItemLabelAnchor CENTER 
        = new ItemLabelAnchor("ItemLabelAnchor.CENTER");

    
    public static final ItemLabelAnchor INSIDE1 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE1");

    
    public static final ItemLabelAnchor INSIDE2 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE2");

    
    public static final ItemLabelAnchor INSIDE3 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE3");

    
    public static final ItemLabelAnchor INSIDE4 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE4");

    
    public static final ItemLabelAnchor INSIDE5 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE5");

    
    public static final ItemLabelAnchor INSIDE6 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE6");

    
    public static final ItemLabelAnchor INSIDE7 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE7");

    
    public static final ItemLabelAnchor INSIDE8 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE8");

    
    public static final ItemLabelAnchor INSIDE9 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE9");

    
    public static final ItemLabelAnchor INSIDE10 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE10");

    
    public static final ItemLabelAnchor INSIDE11 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE11");

    
    public static final ItemLabelAnchor INSIDE12 
        = new ItemLabelAnchor("ItemLabelAnchor.INSIDE12");

    
    public static final ItemLabelAnchor OUTSIDE1 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE1");

    
    public static final ItemLabelAnchor OUTSIDE2 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE2");

    
    public static final ItemLabelAnchor OUTSIDE3 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE3");

    
    public static final ItemLabelAnchor OUTSIDE4 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE4");

    
    public static final ItemLabelAnchor OUTSIDE5 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE5");

    
    public static final ItemLabelAnchor OUTSIDE6 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE6");

    
    public static final ItemLabelAnchor OUTSIDE7 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE7");

    
    public static final ItemLabelAnchor OUTSIDE8 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE8");

    
    public static final ItemLabelAnchor OUTSIDE9 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE9");

    
    public static final ItemLabelAnchor OUTSIDE10 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE10");

    
    public static final ItemLabelAnchor OUTSIDE11 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE11");

    
    public static final ItemLabelAnchor OUTSIDE12 
        = new ItemLabelAnchor("ItemLabelAnchor.OUTSIDE12");

    
    private String name;

    
    private ItemLabelAnchor(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemLabelAnchor)) {
            return false;
        }

        ItemLabelAnchor order = (ItemLabelAnchor) o;
        if (!this.name.equals(order.toString())) {
            return false;
        }

        return true;

    }
    
    
    private Object readResolve() throws ObjectStreamException {
        ItemLabelAnchor result = null;
        if (this.equals(ItemLabelAnchor.CENTER)) {
            result = ItemLabelAnchor.CENTER;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE1)) {
            result = ItemLabelAnchor.INSIDE1;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE2)) {
            result = ItemLabelAnchor.INSIDE2;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE3)) {
            result = ItemLabelAnchor.INSIDE3;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE4)) {
            result = ItemLabelAnchor.INSIDE4;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE5)) {
            result = ItemLabelAnchor.INSIDE5;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE6)) {
            result = ItemLabelAnchor.INSIDE6;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE7)) {
            result = ItemLabelAnchor.INSIDE7;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE8)) {
            result = ItemLabelAnchor.INSIDE8;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE9)) {
            result = ItemLabelAnchor.INSIDE9;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE10)) {
            result = ItemLabelAnchor.INSIDE10;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE11)) {
            result = ItemLabelAnchor.INSIDE11;
        }
        else if (this.equals(ItemLabelAnchor.INSIDE12)) {
            result = ItemLabelAnchor.INSIDE12;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE1)) {
            result = ItemLabelAnchor.OUTSIDE1;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE2)) {
            result = ItemLabelAnchor.OUTSIDE2;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE3)) {
            result = ItemLabelAnchor.OUTSIDE3;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE4)) {
            result = ItemLabelAnchor.OUTSIDE4;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE5)) {
            result = ItemLabelAnchor.OUTSIDE5;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE6)) {
            result = ItemLabelAnchor.OUTSIDE6;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE7)) {
            result = ItemLabelAnchor.OUTSIDE7;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE8)) {
            result = ItemLabelAnchor.OUTSIDE8;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE9)) {
            result = ItemLabelAnchor.OUTSIDE9;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE10)) {
            result = ItemLabelAnchor.OUTSIDE10;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE11)) {
            result = ItemLabelAnchor.OUTSIDE11;
        }
        else if (this.equals(ItemLabelAnchor.OUTSIDE12)) {
            result = ItemLabelAnchor.OUTSIDE12;
        }
        return result;
    }

}
