

package org.jmol.util;

public class TestIntInt2ObjHash extends junit.framework.TestCase {

  public TestIntInt2ObjHash() {
  }

  public void setUp() {
  }

  public void tearDown() {
  }

  public void testOne() {
    IntInt2ObjHash h = new IntInt2ObjHash(10);
    for (int i = 0; i < 10; ++i)
      h.put(i, i, new Integer(i));
    for (int i = 0; i < 10; ++i)
      assertEquals(((Integer)h.get(i, i)).intValue(), i);
  }

  public void test256() {
    IntInt2ObjHash h = new IntInt2ObjHash(256);
    for (int i = 0; i < 256; ++i)
      h.put(i, i, new Integer(i));
    for (int i = 0; i < 256; ++i)
      assertEquals(((Integer)h.get(i, i)).intValue(), i);
  }

  public void test257() {
    IntInt2ObjHash h = new IntInt2ObjHash(256);
    for (int i = 0; i < 257; ++i)
      h.put(i, i, new Integer(i));
    for (int i = 0; i < 257; ++i)
      assertEquals(((Integer)h.get(i, i)).intValue(), i);
  }

  public void testUpTo1000() {
    for (int i = 1; i < 1000; i += 100)
      tryOne(i);
  }

  void tryOne(int count) {
    IntInt2ObjHash h = new IntInt2ObjHash(4);
    for (int i = 0; i < count; ++i)
      h.put(i, i, new Integer(i));
    
    for (int i = 0; i < count; ++i)
      assertEquals(((Integer)h.get(i, i)).intValue(), i);
  }

  void dumpHash(IntInt2ObjHash h) {
    Logger.info("dumping hash:" + h);
    Logger.info("h.entryCount=" + h.entryCount);
    IntInt2ObjHash.Entry[] entries = h.entries;
    for (int i = 0; i < entries.length; ++i) {
      StringBuffer log = new StringBuffer();
      log.append(i).append(": ");
      for (IntInt2ObjHash.Entry e = entries[i]; e != null; e = e.next) {
        log.append(e.key1).append(",").append(e.key2).append(" ");
      }
      Logger.info(log.toString());
    }
  }

  public void test1000() {
    IntInt2ObjHash h = new IntInt2ObjHash();
    for (int i = 0; i < 1000; ++i)
      h.put(i, -i, new Integer(i));
    for (int i = 0; i < 1000; ++i)
      assertEquals(((Integer)h.get(i, -i)).intValue(), i);
  }

}
