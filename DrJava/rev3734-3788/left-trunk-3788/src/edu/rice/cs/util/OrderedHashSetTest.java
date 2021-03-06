package edu.rice.cs.util;
import junit.framework.TestCase;
import java.util.*;

import edu.rice.cs.drjava.DrJavaTestCase;


public class OrderedHashSetTest extends DrJavaTestCase {
  
  public void testSearch() {

    Integer int1 = new Integer(1);
    Integer int2 = new Integer(2);
    Integer int3 = new Integer(3);
    
    Integer[] ints = new Integer[]{int1, int2, int3};
     
    OrderedHashSet<Integer> iTod = new OrderedHashSet<Integer>();
    
    assertTrue("Empty BHM is empty", iTod.isEmpty());
    assertTrue("Empty BHM has no values", iTod.elements().isEmpty());

    assertEquals("Initial size of 0", iTod.size(), 0);
    try {
      iTod.get(0);
      fail("Queue should be empty forcing an Exception to be thrown");
    }
    catch(IndexOutOfBoundsException e) {  }        

    assertFalse("Should not find non-existent key", iTod.contains(int1));
    assertFalse("Should not find non-existent key", iTod.contains(int2));
    assertFalse("Should not find non-existent key", iTod.contains(int3));

    iTod.add(int1);
    
    assertFalse("NonEmpty BHM is not empty", iTod.isEmpty());
    assertFalse("NonEmpty BHM has some values", iTod.elements().isEmpty());
    
    assertTrue("Should find key", iTod.contains(int1));
    assertEquals("Should find key using index", int1, iTod.get(0));
    assertFalse("Should not find non-existent key", iTod.contains(int2));
    assertFalse("Should not find non-existent key", iTod.contains(int3));

    iTod.add(int2);
    iTod.add(int3);
    
    assertEquals("get(1) test", int2, iTod.get(1));  
    assertEquals("get(2) test", int3, iTod.get(2));
    
    Collection<Integer> valsCol = iTod.elements();
    
    Object[] vals = iTod.toArray();
    Object[] colVals = valsCol.toArray();
    
    
    
    assertTrue("elements() test", Arrays.equals(vals, colVals));
           
    Iterator<Integer> it = iTod.iterator();
    try {
      it.remove();
      fail("Removing non-existent element should generate IllegalStateException");
    } catch(IllegalStateException e) { }

    Integer key = it.next();
    iTod.remove(key);
    assertEquals("Size should be 2", 2, iTod.size());
    assertTrue("Iterator should be non empty", it.hasNext());

    assertFalse("Should not find non-existent key", iTod.contains(key));

    it = iTod.iterator();
    key = it.next();
    it.remove();
    assertEquals("Size should be 1", 1, iTod.size());
    assertTrue("Iterator should be non empty", it.hasNext());

    assertFalse("Should not find non-existent key", iTod.contains(key));

    iTod.remove(0);
    assertTrue("iTod should be empty", iTod.isEmpty());
    
  }
  
  public void testRemove() {
 
    Integer int1 = new Integer(1);
    Integer int2 = new Integer(2);
    Integer int3 = new Integer(3);
    
    OrderedHashSet<Integer> dToi = new OrderedHashSet<Integer>();

    assertEquals("Initial size of 0", dToi.size(), 0);
    dToi.clear();
    assertEquals("Initial size of 0", dToi.size(), 0);

    dToi.add(int1);
    assertEquals("Size should be 1", dToi.size(), 1);
    dToi.add(int2);
    assertEquals("Size should be 2", dToi.size(), 2);
    dToi.add(int3);
    assertEquals("Size should be 3", dToi.size(), 3);

    dToi.remove(int1);
    assertEquals("Size should be 2", dToi.size(), 2);

    
    assertFalse("Removed key should be found", dToi.remove(int1));
    assertEquals("Size should be 2", dToi.size(), 2);
    dToi.add(int1);
    assertEquals("Size should be 3", dToi.size(), 3);
    assertFalse("Adding existing element should return false", dToi.add(int1));
    assertEquals("Size should be 3", dToi.size(), 3);

    dToi.remove(int3);
    assertEquals("Size should be 2", dToi.size(), 2);
    dToi.remove(int2);
    assertFalse("Removed key should not be found", dToi.contains(int2));

    assertEquals("Size should be 1", dToi.size(), 1);
    dToi.add(int3);
    assertEquals("Size should be 2", dToi.size(), 2);
    assertFalse("Adding existing element should return false", dToi.add(int3));
    assertEquals("Size should be 2", dToi.size(), 2);

    Integer i = dToi.remove(1);
    assertEquals("Deleted element should be int3", i, int3);
    assertEquals("Deleted element should be int1", dToi.remove(0), int1);
    assertEquals("Resulting size of 0", dToi.size(), 0);

    assertFalse("Iterator to cleared list should be empty", dToi.iterator().hasNext());
  }
  
  public void testPut() {
    
    String one = "1";
    String two = "2";
    String three = "3";
    
    OrderedHashSet<String> myhash = new OrderedHashSet<String>();
    
    assertFalse("Expected false", myhash.contains(one));
    assertFalse("Expected false", myhash.contains(two));
    assertFalse("Expected false", myhash.contains(three));
          
    myhash.add(one);
    myhash.add(two);
    myhash.add(three);
    
    assertTrue("one should be in the set", myhash.contains(one));
    assertTrue("two should be in the set", myhash.contains(two));
    assertTrue("three should be in the set", myhash.contains(three));
    
    Iterator<String> it = myhash.iterator();
    try { 
      it.remove();
      fail("Removing non-existent element should generate IllegalStateException");
    } catch(IllegalStateException e) { }
    
    String key = it.next();
    it.remove();
    assertFalse("After removing key, it should not appear in set", myhash.contains(key));
    
    key = it.next();
    it.remove();
    assertFalse("After removing key, it should not appear in set", myhash.contains(key));
    
    key = it.next();
    it.remove();
    assertFalse("After removing key, it should not appear in set", myhash.contains(key));
    
    
    it = myhash.iterator();
    assertFalse("Set should be empty", it.hasNext());
  }
}
