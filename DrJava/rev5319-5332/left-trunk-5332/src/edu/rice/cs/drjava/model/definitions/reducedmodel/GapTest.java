

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import edu.rice.cs.drjava.DrJavaTestCase;


public final class GapTest extends DrJavaTestCase {
  
  public void testGrow() {
    Gap gap0 = new Gap(0, ReducedToken.FREE);
    Gap gap1 = new Gap(1, ReducedToken.FREE);
    gap0.grow(5);
    assertEquals(5, gap0.getSize());
    gap0.grow(0);
    assertEquals(5, gap0.getSize());
    gap1.grow(-6);
    assertEquals(1, gap1.getSize());
  }

  
  public void testShrink() {
    Gap gap0 = new Gap(5, ReducedToken.FREE);
    Gap gap1 = new Gap(1, ReducedToken.FREE);
    gap0.shrink(3);
    assertEquals(2, gap0.getSize());
    gap0.shrink(0);
    assertEquals(2, gap0.getSize());
    gap1.shrink(3);
    assertEquals(1, gap1.getSize());
    gap1.shrink(-1);
    assertEquals(1, gap1.getSize());
  }
}



