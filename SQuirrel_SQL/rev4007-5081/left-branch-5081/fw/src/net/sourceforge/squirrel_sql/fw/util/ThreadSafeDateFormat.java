
package net.sourceforge.squirrel_sql.fw.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;


public class ThreadSafeDateFormat {
    
    
    private DateFormat dateFormat;
    
    
    public ThreadSafeDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    
    public ThreadSafeDateFormat(int style) {
        this(style, false);
    }

        
    public ThreadSafeDateFormat(int style, boolean isTime) {
        if (isTime) {
            this.dateFormat = DateFormat.getTimeInstance(style);
        } else {
            this.dateFormat = DateFormat.getDateInstance(style);
        }
    }
    
    
    public ThreadSafeDateFormat(int dateSytle, int timeStyle) {
        this.dateFormat = DateFormat.getDateTimeInstance(dateSytle, timeStyle);
    }
    
    
    public synchronized String format(Object obj) {
        return dateFormat.format(obj);
    }
    
    
    public synchronized Date parse(String str) throws ParseException {
        return dateFormat.parse(str);
    }
    
    
    public synchronized void setLenient(boolean lenient) {
        dateFormat.setLenient(lenient);
    }
}
