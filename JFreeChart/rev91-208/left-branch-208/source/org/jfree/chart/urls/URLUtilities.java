

package org.jfree.chart.urls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;


public class URLUtilities {

    
    private static final Class[] STRING_ARGS_2 = new Class[] {String.class, 
            String.class};
    
    
    public static String encode(String s, String encoding) {
        Class c = URLEncoder.class;
        String result = null;
        try {
            Method m = c.getDeclaredMethod("encode", STRING_ARGS_2);
            try {
                result = (String) m.invoke(null, new Object[] {s, encoding});
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e) {
            
            result = URLEncoder.encode(s);
        }
        return result;
    }
    
}
