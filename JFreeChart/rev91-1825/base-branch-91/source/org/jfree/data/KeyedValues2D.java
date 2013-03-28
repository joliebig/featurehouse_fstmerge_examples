

package org.jfree.data;

import java.util.List;


public interface KeyedValues2D extends Values2D {

    
    public Comparable getRowKey(int row);

    
    public int getRowIndex(Comparable key);

    
    public List getRowKeys();

    
    public Comparable getColumnKey(int column);

    
    public int getColumnIndex(Comparable key);

    
    public List getColumnKeys();

    
    public Number getValue(Comparable rowKey, Comparable columnKey);

}
