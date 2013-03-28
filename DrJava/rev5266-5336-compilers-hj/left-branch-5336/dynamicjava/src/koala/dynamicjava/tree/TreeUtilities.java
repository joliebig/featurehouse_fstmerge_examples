

package koala.dynamicjava.tree;

import java.util.*;



public class TreeUtilities {
  
  public static String listToName(List<? extends IdentifierToken> l) {
    String   result = "";
    if (l != null) {
      Iterator<? extends IdentifierToken> it = l.iterator();
      if (it.hasNext()) {
        result += it.next().image();
      }
      while (it.hasNext()) {
        result += "." + it.next().image();
      }
    }
    return result;
  }
  
  
  private TreeUtilities() {
  }
}
