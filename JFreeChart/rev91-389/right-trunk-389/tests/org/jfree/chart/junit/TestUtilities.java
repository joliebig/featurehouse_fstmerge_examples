

package org.jfree.chart.junit;

import java.util.Collection;
import java.util.Iterator;


public class TestUtilities {

    
    public static boolean containsInstanceOf(Collection collection, Class c) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj != null && obj.getClass().equals(c)) {
                return true;
            }
        }
        return false;
    }
    
}
