

package org.jmol.util;

public class TestInt2ObjHash extends junit.framework.TestCase {

  public TestInt2ObjHash() {
  }

  public void setUp() {
  }

  public void tearDown() {
  }

  public void testOne() {
    Int2ObjHash h = new Int2ObjHash(10);
    for (int i = 0; i < 10; ++i)
      h.put(i, new Integer(i));
    for (int i = 0; i < 10; ++i)
      assertEquals(((Integer)h.get(i)).intValue(), i);
  }

  public void test256() {
    Int2ObjHash h = new Int2ObjHash(256);
    for (int i = 0; i < 256; ++i)
      h.put(i, new Integer(i));
    for (int i = 0; i < 256; ++i)
      assertEquals(((Integer)h.get(i)).intValue(), i);
  }

  public void test257() {
    Int2ObjHash h = new Int2ObjHash(256);
    for (int i = 0; i < 257; ++i)
      h.put(i, new Integer(i));
    for (int i = 0; i < 257; ++i)
      assertEquals(((Integer)h.get(i)).intValue(), i);
  }

  public void testUpTo1000() {
    for (int i = 1; i < 1000; i += 100)
      tryOne(i);
  }

  void tryOne(int count) {
    Int2ObjHash h = new Int2ObjHash(4);
    for (int i = 0; i < count; ++i)
      h.put(i, new Integer(i));
    
    for (int i = 0; i < count; ++i)
      assertEquals(((Integer)h.get(i)).intValue(), i);
  }

  void dumpHash(Int2ObjHash h) {
    Logger.info("dumping hash:" + h);
    Logger.info("h.entryCount=" + h.entryCount);
    Int2ObjHash.Entry[] entries = h.entries;
    for (int i = 0; i < entries.length; ++i) {
      StringBuffer log = new StringBuffer();
      log.append(i).append(": ");
      for (Int2ObjHash.Entry e = entries[i]; e != null; e = e.next) {
        log.append(e.key).append(" ");
      }
      Logger.info(log.toString());
    }
  }

  public void test1000() {
    Int2ObjHash h = new Int2ObjHash();
    for (int i = 0; i < 1000; ++i)
      h.put(-i, new Integer(i));
    for (int i = 0; i < 1000; ++i)
      assertEquals(((Integer)h.get(-i)).intValue(), i);
  }

}
