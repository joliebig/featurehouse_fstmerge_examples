

package edu.rice.cs.plt.iter;

import junit.framework.TestCase;

import static edu.rice.cs.plt.iter.IterUtilTest.assertIterator;
import static edu.rice.cs.plt.iter.IterUtilTest.assertIteratorUnchecked;

public class NoDuplicatesIteratorTest extends TestCase {
  
  private static <T> NoDuplicatesIterator<T> make(T... elts) {
    return NoDuplicatesIterator.make(IterUtil.asIterable(elts).iterator());
  }
  
  public void test() {
    assertIterator(make(new Integer[0]));
    assertIteratorUnchecked(make(new Integer[0]));
    assertIterator(make(1), 1);
    assertIteratorUnchecked(make(1), 1);
    assertIterator(make(1, 1), 1);
    assertIteratorUnchecked(make(1, 1), 1);
    assertIterator(make(1, 1, 1), 1);
    assertIteratorUnchecked(make(1, 1, 1), 1);
    assertIterator(make(1, 2), 1, 2);
    assertIteratorUnchecked(make(1, 2), 1, 2);
    assertIterator(make(1, 2, 1), 1, 2);
    assertIteratorUnchecked(make(1, 2, 1), 1, 2);
    assertIterator(make(1, 2, 1, 3, 2), 1, 2, 3);
    assertIteratorUnchecked(make(1, 2, 1, 3, 2), 1, 2, 3);
    assertIterator(make(1, 2, null, 4, null, 2, 5), 1, 2, null, 4, 5);
  }
  
}
