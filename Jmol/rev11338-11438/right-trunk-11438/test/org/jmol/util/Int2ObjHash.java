

package org.jmol.util;

public class Int2ObjHash {
  int entryCount;
  Entry[] entries;
  

  public Int2ObjHash(int initialCapacity) {
    entries = new Entry[initialCapacity];
  }

  public Int2ObjHash() {
    this(256);
  }

  public synchronized Object get(int key) {
    Entry[] entries = this.entries;
    int hash = (key & 0x7FFFFFFF) % entries.length;
    for (Entry e = entries[hash]; e != null; e = e.next)
      if (e.key == key)
        return e.value;
    return null;
  }

  public synchronized void put(int key, Object value) {
    Entry[] entries = this.entries;
    int hash = (key & 0x7FFFFFFF) % entries.length;
    for (Entry e = entries[hash]; e != null; e = e.next)
      if (e.key == key) {
        e.value = value;
        return;
      }
    if (entryCount > entries.length)
      rehash();
    entries = this.entries;
    hash = (key & 0x7FFFFFFF) % entries.length;
    entries[hash] = new Entry(key, value, entries[hash]);
    ++entryCount;
  }

  private void rehash() {
    Entry[] oldEntries = entries;
    int oldSize = oldEntries.length;
    int newSize = oldSize * 2 + 1;
    Entry[] newEntries = new Entry[newSize];

    for (int i = oldSize; --i >= 0; ) {
      for (Entry e = oldEntries[i]; e != null; ) {
        Entry t = e;
        e = e.next;

        int hash = (t.key & 0x7FFFFFFF) % newSize;
        t.next = newEntries[hash];
        newEntries[hash] = t;
      }
    }
    entries = newEntries;
  }

  static class Entry {
    int key;
    Object value;
    Entry next;
    
    Entry(int key, Object value, Entry next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }
  }
}


