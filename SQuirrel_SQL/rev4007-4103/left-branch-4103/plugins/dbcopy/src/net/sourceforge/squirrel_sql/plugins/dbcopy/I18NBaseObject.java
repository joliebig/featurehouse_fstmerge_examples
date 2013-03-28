
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public abstract class I18NBaseObject {

    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(I18NBaseObject.class);
    
    
    public static void main(String[] args) {
        

    }
    
    
    protected static String getMessage(String key) {
        return s_stringMgr.getString(key);
    }

    
    protected static String getMessage(String key, Object arg) {
        return s_stringMgr.getString(key, arg);
    }   

    
    protected static String getMessage(String key, Object[] args) {
        return s_stringMgr.getString(key, args);
    }      
    

}
