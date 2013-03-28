

package koala.dynamicjava.tree;

import junit.framework.TestCase;


 
public class LongLiteralTest extends TestCase {
  
   
  public void testLongLiteral()
  {
    LongLiteral ll;
    
    
    ll = new LongLiteral("0x138");
    assertTrue("Parse 0x138", new Long("312").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("0470");
    assertTrue("Parse 0470", new Long("312").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("312");
    assertTrue("Parse 312", new Long("312").compareTo((Long)ll.getValue()) == 0);

    
    ll = new LongLiteral("0x138l");
    assertTrue("Parse 0x138l", new Long("312").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("0470l");
    assertTrue("Parse 0470l", new Long("312").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("312l");
    assertTrue("Parse 312l", new Long("312").compareTo((Long)ll.getValue()) == 0);    
    ll = new LongLiteral("0x138L");
    assertTrue("Parse 0x138L", new Long("312").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("0470L");
    assertTrue("Parse 0470L", new Long("312").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("312L");
    assertTrue("Parse 312L", new Long("312").compareTo((Long)ll.getValue()) == 0);    
    ll = new LongLiteral("0");
    assertTrue("Parse 0", new Long("0").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("0L");
    assertTrue("Parse 0L", new Long("0").compareTo((Long)ll.getValue()) == 0);    
    ll = new LongLiteral("0l");
    assertTrue("Parse 0l", new Long("0").compareTo((Long)ll.getValue()) == 0);    
    
    
    ll = new LongLiteral("0x0");
    assertTrue("Parse 0", new Long("0").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("0x7fffffff");
    assertTrue("Parse 7fffffff", new Long("2147483647").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("0x80000000");
    assertTrue("Parse 80000000 Hexadecimal", new Long("2147483648").compareTo((Long)ll.getValue()) == 0);

    
    ll = new LongLiteral("0");
    assertTrue("Parse 0 Octal", new Long("0").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("017777777777");
    assertTrue("Parse 17777777777 Octal", new Long("2147483647").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("020000000000");
    assertTrue("Parse 20000000000 Octal", new Long("2147483648").compareTo((Long)ll.getValue()) == 0);
    
    
    ll = new LongLiteral("0xffffffffffffffff");
    assertTrue("Parse -1 Hexadecimal", new Long("-1").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("01777777777777777777777");
    assertTrue("Parse -1 Hexadecimal", new Long("-1").compareTo((Long)ll.getValue()) == 0);
    ll = new LongLiteral("-1");
    assertTrue("Parse -1", new Long("-1").compareTo((Long)ll.getValue()) == 0);
    
    }
}