

package koala.dynamicjava.util;

import java.util.*;



public class ListUtilities {
  
  
  static public <T> LinkedList<T> listCopy(List<T> l){
    Iterator<T> it = l.iterator();
    LinkedList<T> result = new LinkedList<T>();
    while (it.hasNext()) result.add(it.next());
    return result;
  }
}