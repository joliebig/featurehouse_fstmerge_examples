

package org.jfree.data.resources;

import java.util.ListResourceBundle;


public class DataPackageResources extends ListResourceBundle {

    
    public Object[][] getContents() {
        return CONTENTS;
    }

    
    private static final Object[][] CONTENTS = {

        {"series.default-prefix",     "Series"},
        {"categories.default-prefix", "Category"},

    };

}
