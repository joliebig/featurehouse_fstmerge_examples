

package org.jfree.data;

import java.util.List;


public interface KeyedValues extends Values {

    
    public Comparable getKey(int index);

    
    public int getIndex(Comparable key);

    
    public List getKeys();

    
    public Number getValue(Comparable key);

}
