package edu.rice.cs.util;
import edu.rice.cs.drjava.DrJavaTestCase;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;


public class BidirectionalHashMapTest extends DrJavaTestCase {
  
  public void testSearch() {
    Double dbl1 = new Double(.1);
    Double dbl2 = new Double(.2);
    Double dbl3 = new Double(.3);
    
    Double[] dbls = new Double[]{dbl1, dbl2, dbl3};

    Integer int1 = new Integer(1);
    Integer int2 = new Integer(2);
    Integer int3 = new Integer(3);
    
    Integer[] ints = new Integer[]{int1, int2, int3};
     
    BidirectionalHashMap<Integer, Double> iTod = new BidirectionalHashMap<Integer, Double>();
    
    assertTrue("Empty BHM is empty", iTod.isEmpty());
    assertTrue("Empty BHM has no values", iTod.values().isEmpty());

    assertEquals("Initial size of 0", iTod.size(), 0);

    assertFalse("Should not find non-existent key", iTod.containsKey(int1));
    assertFalse("Should not find non-existent key", iTod.containsKey(int2));
    assertFalse("Should not find non-existent key", iTod.containsKey(int3));

    assertFalse("Should not find non-existent value", iTod.containsValue(dbl1));
    assertFalse("Should not find non-existent value", iTod.containsValue(dbl2));
    assertFalse("Should not find non-existent value", iTod.containsValue(dbl3));

    iTod.put(int1, dbl1);
    
    assertFalse("NonEmpty BHM is not empty", iTod.isEmpty());
    assertFalse("NonEmpty BHM has some values", iTod.values().isEmpty());
    
    assertTrue("Should find key", iTod.containsKey(int1));
    assertFalse("Should not find non-existent key", iTod.containsKey(int2));
    assertFalse("Should not find non-existent key", iTod.containsKey(int3));

    assertTrue("Should find value", iTod.containsValue(dbl1));
    assertFalse("Should not find non-existent value", iTod.containsValue(dbl2));
    assertFalse("Should not find non-existent value", iTod.containsValue(dbl3));

    iTod.put(int2, dbl2);
    iTod.put(int3, dbl3);
    
    Collection<Double> valsCol = iTod.values();
    
    Object[] vals = iTod.valuesArray();
    Object[] colVals = valsCol.toArray();
    
    
    
    Arrays.sort(vals);
    Arrays.sort(colVals);
    
    assertTrue("values() test", Arrays.equals(vals, colVals));
    
    assertTrue("values test", Arrays.equals(dbls, vals));
               
    Iterator<Double> it = iTod.valuesIterator();
    try {
      it.remove();
      fail("Removing non-existent element should generate IllegalStateException");
    } catch(IllegalStateException e) { }

    Double val = it.next();
    Integer key = iTod.getKey(val);
    iTod.removeKey(val);
    assertEquals("Size should be 2", 2, iTod.size());
    assertTrue("Iterator should be non empty", it.hasNext());

    assertFalse("Should not find non-existant key", iTod.containsKey(key));
    assertFalse("Should not find non-existant key", iTod.containsValue(val));

    it = iTod.valuesIterator();
    val = it.next();
    key = iTod.getKey(val);
    it.remove();
    assertEquals("Size should be 1", 1, iTod.size());
    assertTrue("Iterator should be non empty", it.hasNext());

    assertFalse("Should not find non-existant key", iTod.containsKey(key));
    assertFalse("Should not find non-existant value", iTod.containsValue(val));


    iTod.clear();
  }
  
  public void testRemove() {
    Double dbl1 = new Double(.1);
    Double dbl2 = new Double(.2);
    Double dbl3 = new Double(.3);

    Integer int1 = new Integer(1);
    Integer int2 = new Integer(2);
    Integer int3 = new Integer(3);
    BidirectionalHashMap<Double, Integer> dToi = new BidirectionalHashMap<Double, Integer>();

    assertEquals("Initial size of 0", dToi.size(), 0);
    dToi.clear();
    assertEquals("Initial size of 0", dToi.size(), 0);

    dToi.put(dbl1, int1);
    assertEquals("Size should be 1", dToi.size(), 1);
    dToi.put(dbl2, int2);
    assertEquals("Size should be 2", dToi.size(), 2);
    dToi.put(dbl3, int3);
    assertEquals("Size should be 3", dToi.size(), 3);

    dToi.removeKey(int1);
    assertEquals("Size should be 2", dToi.size(), 2);

    
    assertEquals("Removing key should return associated value", null, dToi.removeKey(int1));
    assertEquals("Size should be 2", dToi.size(), 2);
    dToi.put(dbl1, int1);
    assertEquals("Size should be 3", dToi.size(), 3);
    try {
      dToi.put(dbl3, int3);
      fail("Adding existing element should generate IllegalArgumentException");
    }
    catch (IllegalArgumentException e) { }
    assertEquals("Size should be 3", dToi.size(), 3);

    
    dToi.removeValue(dbl3);
    assertEquals("Size should be 2", dToi.size(), 2);
    assertEquals("Removing value should retrun associated key", int2, dToi.removeValue(dbl2));

    assertEquals("Size should be 1", dToi.size(), 1);
    dToi.put(dbl3, int3);
    assertEquals("Size should be 2", dToi.size(), 2);
    try {
      dToi.put(dbl3, int3);
      fail("Adding existing element should generate IllegalArgumentException");
    }
    catch (IllegalArgumentException e) { }
    assertEquals("Size should be 2", dToi.size(), 2);

    dToi.clear();
    assertEquals("Cleared size of 0", dToi.size(), 0);

    assertFalse("Iterator to cleared list should be empty", dToi.valuesIterator().hasNext());
  }
  
  public void testPut() {
    
    String one = "1";
    String two = "2";
    String three = "3";
    
    Integer int1 = new Integer(1);
    Integer int2 = new Integer(2);
    Integer int3 = new Integer(3);
    
    BidirectionalHashMap<String, Integer> myhash = new BidirectionalHashMap<String, Integer>();

    assertEquals("Expected size of 0", 0, myhash.size());

    assertEquals("Expected null", null, myhash.getValue(one));
    assertEquals("Expected null", null, myhash.getValue(two));
    assertEquals("Expected null", null, myhash.getValue(three));

    assertEquals("Expected null", null, myhash.getKey(int1));
    assertEquals("Expected null", null, myhash.getKey(int2));
    assertEquals("Expected null", null, myhash.getKey(int3));

    myhash.put(one, int1);
    myhash.put(two, int2);
    myhash.put(three, int3);

    assertTrue("Given one, should get 1", myhash.getValue(one) == int1);
    assertTrue("Given two, should get 2", myhash.getValue(two) == int2);
    assertTrue("Given three, should get 3", myhash.getValue(three) == int3);
    
    assertTrue("Given 1, should get one", myhash.getKey(int1) == one);
    assertTrue("Given 2, should get two", myhash.getKey(int2) == two);
    assertTrue("Given 3, should get three", myhash.getKey(int3) == three);

    Iterator<Integer> it = myhash.valuesIterator();
    try { 
      it.remove();
      fail("Removing non-existent element should generate IllegalStateException");
    } catch(IllegalStateException e) { }
    
    Integer value = it.next();
    String key = myhash.getKey(value);
    assertEquals("key and value should match", value.toString(), key);
    it.remove();
    assertEquals("After removing key, it should not appear in map", null, myhash.getValue(key));
    assertEquals("After removing value, it should not appear in map", null, myhash.getKey(value));
    assertTrue("Map should contain elements", it.hasNext());

    value = it.next();
    key = myhash.getKey(value);
    assertEquals("key and value should match", value.toString(), key);
    it.remove();
    assertEquals("After removing key, it should not appear in map", null, myhash.getValue(key));
    assertEquals("After removing value, it should not appear in map", null, myhash.getKey(value));
    assertTrue("Map should contain elements", it.hasNext());

    value = it.next();
    key = myhash.getKey(value);
    assertEquals("key and value should match", value.toString(), key);
    it.remove();
    assertEquals("After removing key, it should not appear in map", null, myhash.getValue(key));
    assertEquals("After removing value, it should not appear in map", null, myhash.getKey(value));

    
    it = myhash.valuesIterator();
    assertFalse("Map should be empty", it.hasNext());
  }
  
}