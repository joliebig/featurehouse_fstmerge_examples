

package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;


public final class DateTickMarkPosition implements Serializable {

    
    private static final long serialVersionUID = 2540750672764537240L;

    
    public static final DateTickMarkPosition START
        = new DateTickMarkPosition("DateTickMarkPosition.START");

    
    public static final DateTickMarkPosition MIDDLE
        = new DateTickMarkPosition("DateTickMarkPosition.MIDDLE");

    
    public static final DateTickMarkPosition END
        = new DateTickMarkPosition("DateTickMarkPosition.END");

    
    private String name;

    
    private DateTickMarkPosition(String name) {
        this.name = name;
    }

    
    public String toString() {
        return this.name;
    }

    
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DateTickMarkPosition)) {
            return false;
        }
        DateTickMarkPosition position = (DateTickMarkPosition) obj;
        if (!this.name.equals(position.toString())) {
            return false;
        }
        return true;

    }

    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(DateTickMarkPosition.START)) {
            return DateTickMarkPosition.START;
        }
        else if (this.equals(DateTickMarkPosition.MIDDLE)) {
            return DateTickMarkPosition.MIDDLE;
        }
        else if (this.equals(DateTickMarkPosition.END)) {
            return DateTickMarkPosition.END;
        }
        return null;
    }


}
