package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;



public class DatabaseSpecificBooleanValue {
   
    private static final IBooleanValue[] _booleans =
                                            new IBooleanValue[] {
                                                        new SybaseBoolean(),
                                                        new MSSQLServerBoolean()
                                            };
    
    
    
    public static String getBooleanValue(String orig, 
                                         ISQLDatabaseMetaData md)
    {
        for (int i = 0; i < _booleans.length; i++) {
            if(_booleans[i].productMatches(md)) {
                return _booleans[i].getBooleanValue(orig);
            }
        }
        return orig;
    }
    
    private static interface IBooleanValue {
        public boolean productMatches(ISQLDatabaseMetaData md);
        public String getBooleanValue(String originalValue);
    }    
    
    private static class SybaseBoolean implements IBooleanValue {
        
        public boolean productMatches(ISQLDatabaseMetaData md) {
            return DialectFactory.isSyBase(md);
        }
        
        public String getBooleanValue(String orig) {
            String result = orig;
            if ("false".equalsIgnoreCase(orig)) {
                result = "0";
            }
            if ("true".equalsIgnoreCase(orig)) {
                result = "1";
            }
            return result;
        }
    }
    
    private static class MSSQLServerBoolean extends SybaseBoolean {
        
        public boolean productMatches(ISQLDatabaseMetaData md) {
            return DialectFactory.isMSSQLServer(md);
        }
        
    }

}
