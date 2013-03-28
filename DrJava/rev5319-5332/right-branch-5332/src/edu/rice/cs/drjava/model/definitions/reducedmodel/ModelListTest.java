

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class ModelListTest extends DrJavaTestCase {
  protected ModelList<Integer> fEmpty;
  protected ModelList<Integer> fFull;

  protected void setUp() throws Exception {
    super.setUp();
    fFull = new ModelList<Integer>();
    fEmpty = new ModelList<Integer>();
  }

  public void testInsert() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    ModelList<Integer>.ModelIterator itEmpty = fEmpty.getIterator();
    assertTrue("#0.0", fEmpty.isEmpty());
    assertTrue("#0.1", fFull.isEmpty());
    assertEquals("#0.2", 0, fEmpty.length());
    assertEquals("#0.3", 0, fFull.length());
    assertTrue("#0.4", itEmpty.atStart());
    assertTrue("#0.5", itFull.atStart());
    itFull.insert(Integer.valueOf(5));
    assertTrue("#1.0", !itFull.atStart());
    assertEquals("#1.1", 1, fFull.length());
    assertEquals("#1.2", Integer.valueOf(5), itFull.current());
    assertTrue("#2.0", fEmpty.isEmpty());
    assertTrue("#2.1", !fFull.isEmpty());
    itFull.insert(Integer.valueOf(4));
    assertEquals("#2.2", 2, fFull.length());
    assertEquals("#2.3", Integer.valueOf(4), itFull.current());
    assertTrue("#2.4", !fFull.isEmpty());
  }

  public void testInsertFront() {
    fFull.insertFront(Integer.valueOf(3));
    fFull.insertFront(Integer.valueOf(2));
    fFull.insertFront(Integer.valueOf(1));
    fFull.insertFront(Integer.valueOf(0));
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    for (int i = 0; i < 4; i++) {
      itFull.next();
      assertEquals(Integer.valueOf(i), itFull.current());
    }
  }

  public void testRemove() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    
    assertTrue("#0.0", fEmpty.isEmpty());
    assertEquals("#0.1", 0, fEmpty.length());
    assertEquals("#0.2", 0, fFull.length());

    itFull.insert(Integer.valueOf(5));
    assertTrue("#2.0", !fFull.isEmpty());
    assertEquals("#2.1", 1, fFull.length());
    itFull.remove();
    assertTrue("#3.0", fFull.isEmpty());
    assertEquals("#3.1", 0, fFull.length());
  }

  public void testNext() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    

    itFull.insert(Integer.valueOf(6));
    itFull.insert(Integer.valueOf(5));
    itFull.insert(Integer.valueOf(4));
    
    assertEquals("#1.0", Integer.valueOf(4), itFull.current());
    itFull.next();
    assertEquals("#1.1", Integer.valueOf(5), itFull.current());
    itFull.next();
    assertEquals("#1.2", Integer.valueOf(6), itFull.current());
    itFull.next();
  }

  public void testPrev() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    

    itFull.insert(Integer.valueOf(6));
    itFull.insert(Integer.valueOf(5));
    itFull.insert(Integer.valueOf(4));
    itFull.next();
    itFull.next();
    itFull.next();

    itFull.prev();
    assertEquals("#1.1", Integer.valueOf(6), itFull.current());
    itFull.prev();
    assertEquals("#1.2", Integer.valueOf(5), itFull.current());
    itFull.prev();
    assertEquals("#1.3", Integer.valueOf(4), itFull.current());
    itFull.prev();
  }

  public void testCurrent() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    itFull.next();
  }

  public void testPrevItem() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    itFull.insert(Integer.valueOf(0));
    itFull.insert(Integer.valueOf(1));
    itFull.next();
    assertEquals("#0.0", Integer.valueOf(1), itFull.prevItem());
  }

  public void testNextItem() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    itFull.insert(Integer.valueOf(0));
    assertEquals("#0.2", Integer.valueOf(0), itFull.current());
    itFull.insert(Integer.valueOf(1));
    assertEquals("#0.1", Integer.valueOf(1), itFull.current());
    assertEquals("#0.0", Integer.valueOf(0), itFull.nextItem());
  }
  
  public void testCollapse() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    ModelList<Integer>.ModelIterator itEmpty = fEmpty.getIterator();
    ModelList<Integer>.ModelIterator itEmpty2 = itEmpty.copy();
    assertEquals("#0.0", 0, fEmpty.length());
    assertEquals("#0.1", 0, itEmpty.pos());
    itEmpty.collapse(itEmpty2);
    assertEquals("#0.0", 0, fEmpty.length());
    assertEquals("#0.2", 0, itFull.pos());

    itFull.insert(Integer.valueOf(6));
    assertEquals("#0.3", 1, itFull.pos());
    ModelList<Integer>.ModelIterator itFull2 = itFull.copy();
    assertEquals("#0.4", 1, itFull2.pos());
    
    assertEquals("#1.0", 1, fFull.length());
    itFull.collapse(itFull2);
    assertEquals("#1.1", 1, fFull.length());
    assertEquals("#1.2", 1, itFull2.pos());
    assertEquals("#1.3", 1, itFull.pos());

    itFull.insert(Integer.valueOf(5));
    assertEquals("#2.0", 2, fFull.length());
    assertEquals("#2.2", 1, itFull.pos());
    assertEquals("#2.3", 2, itFull2.pos());
    itFull.collapse(itFull2);
    assertEquals("#2.1", 2, fFull.length());

    
    itFull.insert(Integer.valueOf(4));
    assertEquals("#3.0", 3, fFull.length());
    assertEquals("#3.0b",Integer.valueOf(4),itFull.current());
    assertEquals("#3.0a", Integer.valueOf(6), itFull2.current());
    assertEquals("#3.0h", 3, itFull2.pos());
    itFull.collapse(itFull2);
    assertEquals("3.0d", Integer.valueOf(6), itFull2.current());
    assertEquals("3.0e", 2, itFull2.pos());
    assertEquals("3.0f", Integer.valueOf(4), itFull.current());
    assertEquals("3.0g", 1, itFull.pos());
    itFull.next();
    assertEquals("#3.0c",Integer.valueOf(6),itFull.current());
    assertEquals("#3.1", 2, fFull.length());
    itFull.prev();
    assertEquals("#4.0", Integer.valueOf(4), itFull.current());
    assertEquals("#4.1", Integer.valueOf(6), itFull2.current());
    
    
    itFull.insert(Integer.valueOf(7));
    assertEquals("#5.0a", 3, fFull.length());
    assertEquals("#5.0b", Integer.valueOf(7), itFull.current());
    assertEquals("#5.0c", Integer.valueOf(6), itFull2.current());
    itFull2.collapse(itFull);
    assertEquals("#5.1a", 2, fFull.length());
    assertEquals("#5.1b", Integer.valueOf(7), itFull.current());
    assertEquals("#5.1c", Integer.valueOf(6), itFull2.current());
    assertEquals("#5.2a", Integer.valueOf(6), itFull.nextItem());
    assertEquals("#5.2b", Integer.valueOf(7), itFull2.prevItem());
  }

  public void testNotifyInsert() {
    ModelList<Integer>.ModelIterator itFull2 = fFull.getIterator();

    itFull2.insert(Integer.valueOf(0));
    ModelList<Integer>.ModelIterator itFull = itFull2.copy();
    itFull2.insert(Integer.valueOf(1));
    assertEquals(Integer.valueOf(0), itFull.current());
  }

  public void testNotifyRemove() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    ModelList<Integer>.ModelIterator itFull2 = fFull.getIterator();

    itFull2.insert(Integer.valueOf(0));
    itFull2.insert(Integer.valueOf(1));
    itFull.next();
    assertEquals("#0.1", Integer.valueOf(1), itFull.current());
    itFull2.remove();
    assertEquals("#0.0", Integer.valueOf(0), itFull.current());
  }

  public void testNotifyCollapse() {
    ModelList<Integer>.ModelIterator itFull = fFull.getIterator();
    ModelList<Integer>.ModelIterator itFull2 = fFull.getIterator();
    ModelList<Integer>.ModelIterator itFull3 = fFull.getIterator();

    itFull2.insert(Integer.valueOf(0));
    itFull2.insert(Integer.valueOf(1));
    itFull2.insert(Integer.valueOf(2));
    itFull2.insert(Integer.valueOf(3));
    itFull2.insert(Integer.valueOf(4));

    assertTrue("#0.0.0",itFull.atStart());
    
    
    

    for (int i = 0; i < 3; i++) {
      itFull.next();
    }
    for (int j = 0; j < 5; j++) {
      itFull3.next();
    }
    assertEquals("#0.0", Integer.valueOf(2), itFull.current());
    assertEquals("#0.1", Integer.valueOf(0), itFull3.current());
    itFull2.collapse(itFull3);

    assertEquals("#1.0", Integer.valueOf(4), itFull2.current());
    assertEquals("#1.1", Integer.valueOf(0), itFull3.current());
    assertEquals("#1.2", Integer.valueOf(0), itFull.current());
  }
  
  public void testListenerCount() {
    ModelList<Character> testList = new ModelList<Character>();
    
    assertEquals("No iterators", 0, testList.listenerCount());
    
    ModelList<Character>.ModelIterator iter1 = testList.getIterator();
    
    assertEquals("One iterator", 1, testList.listenerCount());
    
    ModelList<Character>.ModelIterator iter2 = testList.getIterator();
    
    assertEquals("Two iterators", 2, testList.listenerCount());
    
    iter1.dispose();
    iter1 = null;
    
    assertEquals("Removed first iterator", 1, testList.listenerCount());
    
    iter2.dispose();
    iter2 = null;
    
    assertEquals("Removed second iterator", 0, testList.listenerCount());
  }
}








