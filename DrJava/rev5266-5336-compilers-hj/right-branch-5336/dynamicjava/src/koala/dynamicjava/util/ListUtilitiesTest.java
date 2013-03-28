



package koala.dynamicjava.util;

import java.util.*;

public class ListUtilitiesTest extends junit.framework.TestCase {
  
  public static void testListCopy(){
    LinkedList<String> l = new LinkedList<String>();
    l.add("String 1");
    l.add("String 2");
    l.add("String 3");
    List<String> ll = ListUtilities.listCopy(l);
    
    assertFalse("l and ll should not be the same object", l == ll);
    assertTrue("l[0] and ll[0] should be the same object", l.get(0) == ll.get(0));
    assertTrue("l[1] and ll[1] should be the same object", l.get(1) == ll.get(1));
    assertTrue("l[2] and ll[2] should be the same object", l.get(2) == ll.get(2));
    
  }
}
  