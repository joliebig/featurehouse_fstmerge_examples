package net.sourceforge.squirrel_sql.jdbcproxy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;



public class ProxyMethodManager {
    
    private static Properties _props = null; 
    
    private static HashMap<String, Long> methodsCalled = new HashMap<String, Long>();
    
    public static void setDriverProperties(Properties props) {
        _props = props;
    }
    
    public static void check(String className, String methodName) 
        throws SQLException 
    {
        String key = className + "." + methodName;
        if (methodsCalled.containsKey(key)) {
            Long count = methodsCalled.get(key);
            methodsCalled.put(key, Long.valueOf(count.longValue() + 1));
        } else {
            methodsCalled.put(key, Long.valueOf(1));
        }
        if (_props.containsKey(key)) {
            String info = (String)_props.get(key);
            if (info != null && ! "".equals(info)) {
                throw new SQLException(info);
            }
        }
    }

    public static void printMethodsCalled() {
        if (trackMethods()) {
            for (Iterator<String> iter = methodsCalled.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                Long count = methodsCalled.get(key);
                System.out.println(key + " -> "+count.longValue());
            }
        }
    }
    
    private static boolean trackMethods() {
        boolean result = false;
        String key = ProxyDriver.TRACK_METHOD_CALLS_KEY;
        if (_props.containsKey(key)) {
            String info = (String)_props.get(key);
            if (info != null && "true".equalsIgnoreCase(info)) {
                result = true;
            }
        }
        return result;
    }
}
