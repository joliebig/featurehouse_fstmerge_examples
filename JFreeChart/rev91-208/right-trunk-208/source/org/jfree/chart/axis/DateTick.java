

package org.jfree.chart.axis;

import java.util.Date;

import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.util.ObjectUtilities;


public class DateTick extends ValueTick {

    
    private Date date;
    
    
    public DateTick(Date date, String label,
                    TextAnchor textAnchor, TextAnchor rotationAnchor, 
                    double angle) {
                        
        super(date.getTime(), label, textAnchor, rotationAnchor, angle);
        this.date = date;
            
    }
    
    
    public Date getDate() {
        return this.date;
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof DateTick && super.equals(obj)) {
            DateTick dt = (DateTick) obj;
            if (!ObjectUtilities.equal(this.date, dt.date)) {
                return false;   
            }
            return true;
        }
        return false;
    }
    
    
    public int hashCode() {
        return this.date.hashCode();
    }
    
}
