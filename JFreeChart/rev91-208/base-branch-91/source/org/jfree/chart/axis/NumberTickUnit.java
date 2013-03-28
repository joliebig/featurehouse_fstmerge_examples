

package org.jfree.chart.axis;

import java.io.Serializable;
import java.text.NumberFormat;


public class NumberTickUnit extends TickUnit implements Serializable {

    
    private static final long serialVersionUID = 3849459506627654442L;
    
    
    private NumberFormat formatter;

    
    public NumberTickUnit(double size) {
        this(size, NumberFormat.getNumberInstance());
    }

    
    public NumberTickUnit(double size, NumberFormat formatter) {
        super(size);
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.formatter = formatter;
    }

    
    public String valueToString(double value) {
        return this.formatter.format(value);
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumberTickUnit)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        NumberTickUnit that = (NumberTickUnit) obj;
        if (!this.formatter.equals(that.formatter)) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (this.formatter != null 
                ? this.formatter.hashCode() : 0);
        return result;
    }

}
