

package net.sf.freecol.common.util;

import java.util.List;


public class Utils {

    
    public static String join(String delimiter, String... strings) {
        if (strings == null || strings.length == 0) {
            return null;
        } else {
            StringBuilder result = new StringBuilder(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                result.append(delimiter);
                result.append(strings[i]);
            }
            return result.toString();
        }
    }

    
    public static String join(String delimiter, List<String> strings) {
        return join(delimiter, strings.toArray(new String[0]));
    }

    
    public static boolean equals(Object one, Object two) {
        return one == null ? two == null : one.equals(two);
    }

    
    public static String getUserDirectory() {
    	return System.getProperty("user.home");
    }
}
