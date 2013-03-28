

package org.jfree.chart.util;


public class StringUtilities {

    
    private StringUtilities() {
    }

    
    public static boolean startsWithIgnoreCase(String base, String start) {
        if (base.length() < start.length()) {
            return false;
        }
        return base.regionMatches(true, 0, start, 0, start.length());
    }

    
    public static boolean endsWithIgnoreCase(String base, String end) {
        if (base.length() < end.length()) {
            return false;
        }
        return base.regionMatches(true, base.length() - end.length(), end, 0, 
                end.length());
    }

    
    public static String getLineSeparator() {
        try {
            return System.getProperty("line.separator", "\n");
        }
        catch (Exception e) {
            return "\n";
        }
    }


}
