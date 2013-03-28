package net.sf.freecol.common.option;

import java.util.List;


public interface ListOptionSelector<T> {

    
    public List<T> getOptions();
    
    
    public T getObject(String id);
    
    
    public String getId(T t);
    
    
    public String toString(T t);
}
