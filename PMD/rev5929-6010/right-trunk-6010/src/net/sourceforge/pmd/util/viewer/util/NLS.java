package net.sourceforge.pmd.util.viewer.util;

import java.util.ResourceBundle;


public class NLS {
    private final static ResourceBundle bundle;

    static {
        bundle = ResourceBundle.getBundle("net.sourceforge.pmd.util.viewer.resources.viewer_strings");
    }

    
    public static String nls(String key) {
        return bundle.getString(key);
    }
}

