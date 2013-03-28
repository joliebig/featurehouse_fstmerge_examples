package net.sourceforge.pmd.util.viewer.util;

import java.util.ResourceBundle;


public class NLS {
    private final static ResourceBundle BUNDLE;

    static {
        BUNDLE = ResourceBundle.getBundle("net.sourceforge.pmd.util.viewer.resources.viewer_strings");
    }

    
    public static String nls(String key) {
        return BUNDLE.getString(key);
    }
}

