
package net.sourceforge.squirrel_sql.fw.util;



public interface ExceptionFormatter {
    
    
    boolean formatsException(Throwable t);
    
    
    String format(Throwable t) throws Exception;
    
}
