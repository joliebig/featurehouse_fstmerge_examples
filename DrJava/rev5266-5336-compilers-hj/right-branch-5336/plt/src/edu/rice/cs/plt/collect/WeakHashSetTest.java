

package edu.rice.cs.plt.collect;

import junit.framework.TestCase;

import edu.rice.cs.plt.lambda.Condition;


public class WeakHashSetTest extends TestCase {
  
  private static final int MAX_GC_COUNT = 12;
  
  
  private WeakHashSet<Integer> intSet;
  
  
  public void setUp() {
    intSet = new WeakHashSet<Integer>();
  }
  
  
  public void testAddAndContains() {
    Integer one = new Integer(1);
    Integer two = new Integer(2);
    Integer three = new Integer(3);
    
    assertFalse("Does not contain 1", intSet.contains(one));
    assertFalse("Does not contain 2", intSet.contains(two));
    assertFalse("Does not contain 3", intSet.contains(three));
    
    assertTrue("Added one", intSet.add(one));
    
    assertTrue("Contains 1", intSet.contains(one));
    assertFalse("Does not contain 2", intSet.contains(two));
    assertFalse("Does not contain 3", intSet.contains(three));
    
    assertTrue("Added three", intSet.add(three));
    
    assertTrue("Contains 1", intSet.contains(one));
    assertTrue("Contains 3", intSet.contains(three));
    assertFalse("Does not contain 2", intSet.contains(two));
    
    assertTrue("Added two", intSet.add(two));
    
    assertTrue("Contains 1", intSet.contains(one));
    assertTrue("Contains 3", intSet.contains(three));
    assertTrue("Contains 2", intSet.contains(two));
    
    assertFalse("Did not add two again", intSet.add(two));
    assertFalse("Did not add three again", intSet.add(three));
    
    assertTrue("Contains 1", intSet.contains(one));
    assertTrue("Contains 2", intSet.contains(two));
    assertTrue("Contains 3", intSet.contains(three));
  }
  
  
  public void testSize() {
    Integer one = new Integer(1);
    Integer two = new Integer(2);
    Integer three = new Integer(3);
    
    assertSame("Empty set", 0, intSet.size());
    
    intSet.add(one);
    assertSame("One element", 1, intSet.size());
    
    intSet.add(two);
    intSet.add(three);
    assertSame("Three elements", 3, intSet.size());
    
    intSet.add(two);
    assertSame("Still three elements", 3, intSet.size());
  }
  
  
  public void testClear() {
    Integer one = new Integer(1);
    Integer two = new Integer(2);
    Integer three = new Integer(3);
    
    assertSame("Empty set", 0, intSet.size());
    
    intSet.add(one);
    intSet.add(two);
    intSet.add(three);
    assertSame("Three elements", 3, intSet.size());
    
    intSet.clear();
    assertSame("No more elements", 0, intSet.size());
  }
  
  
  public void testRemove() {
    Integer one = new Integer(1);
    Integer two = new Integer(2);
    Integer three = new Integer(3);
    
    assertSame("Empty set", 0, intSet.size());
    
    intSet.add(one);
    intSet.add(two);
    intSet.add(three);
    
    assertSame("Three elements", 3, intSet.size());
    assertTrue("Contains 1", intSet.contains(one));
    assertTrue("Contains 2", intSet.contains(two));
    assertTrue("Contains 3", intSet.contains(three));
    
    assertTrue("Removed two", intSet.remove(two));
    assertSame("Two elements", 2, intSet.size());
    assertFalse("Contains 2", intSet.contains(two));
    assertTrue("Contains 1", intSet.contains(one));
    assertTrue("Contains 3", intSet.contains(three));
    
    assertFalse("No need to remove two", intSet.remove(two));
    
    assertTrue("Removed one", intSet.remove(one));
    assertSame("One element", 1, intSet.size());
    assertFalse("Contains 1", intSet.contains(one));
    assertFalse("Contains 2", intSet.contains(two));
    assertTrue("Contains 3", intSet.contains(three));
  }
  
  
  public void testIterator() {
    Integer[] ints = {new Integer(1), new Integer(2), new Integer(3)};
  
    for (int j = 0; j < ints.length; ++j) {
      intSet.add(ints[j]);
    }
    
    assertSame("Three elements", 3, intSet.size());
    assertTrue("Contains 1", intSet.contains(ints[0]));
    assertTrue("Contains 2", intSet.contains(ints[1]));
    assertTrue("Contains 3", intSet.contains(ints[2]));
    
    for (Integer intInSet : intSet) {
      assertTrue(intInSet != null);
      
      int idx = -1;
      for (int j = 0; j < ints.length; ++j) {
        if (ints[j] != null && ints[j].equals(intInSet)) {
          idx = j;
          break;
        }
      }
      
      if (idx >= 0) {
        ints[idx] = null; 
      } else {
        fail("There was an int in the set that was not in the array.");
      }
    }
    
    for (Integer i : ints) {
      if (i != null) {
        fail("Not all ints were found.");
      }
    }
  }
  
  
  public void testAutomaticRemoval() { 
    Integer one = new Integer(1);
    Integer two = new Integer(2);
    Integer three = new Integer(3);
    
    assertSame("Empty set", 0, intSet.size());
    
    intSet.add(one);
    intSet.add(two);
    intSet.add(three);
    
    assertSame("Three elements", 3, intSet.size());
    
    one = null;
    final WeakHashSet<Integer> intSetForThunk1 = intSet;
    runGCUntil(new Condition() {
      public boolean isTrue() { return intSetForThunk1.size() == 2; }
    });
    
    assertSame("one removed", 2, intSet.size());
    assertTrue("two still there", intSet.contains(two));
    assertTrue("three still there", intSet.contains(three));
    
    two = null;
    three = null;
    final WeakHashSet<Integer> intSetForThunk2 = intSet;
    runGCUntil(new Condition() {
      public boolean isTrue() { return intSetForThunk2.size() == 0; }
    });
    
    assertSame("Set empty again", 0, intSet.size());
  }
  
  
  private void runGCUntil(Condition done) {
    int gcCount = 0;
    
    while (!done.isTrue() && gcCount < MAX_GC_COUNT) {
      System.runFinalization();
      System.gc();
      gcCount++;
    }
    
    if (gcCount >= MAX_GC_COUNT) {
      fail("Too many GCs required.");
    }
  }
}
