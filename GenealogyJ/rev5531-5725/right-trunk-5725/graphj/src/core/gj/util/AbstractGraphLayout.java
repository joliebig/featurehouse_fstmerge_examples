
package gj.util;


import gj.layout.GraphLayout;

import java.util.WeakHashMap;


public abstract class AbstractGraphLayout<Attribute> implements GraphLayout {

  private WeakHashMap<Object, Attribute> object2attr = new WeakHashMap<Object, Attribute>();
  
  
  protected Attribute getAttribute(Object object) {
    return object2attr.get(object);
  }
  
  
  protected void setAttribute(Object object, Attribute attr) {
    object2attr.put(object, attr);
  }
  
} 
