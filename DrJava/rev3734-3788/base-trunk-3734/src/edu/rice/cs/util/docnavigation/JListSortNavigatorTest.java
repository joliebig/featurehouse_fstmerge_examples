 

package edu.rice.cs.util.docnavigation;

import junit.framework.TestCase;

import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.DrJavaTestCase;

import java.io.*;
import java.util.*;

public class JListSortNavigatorTest extends DrJavaTestCase {
  
  protected JListSortNavigator<DummyINavigatorItem> list;
  protected DummyINavigatorItem i1, i2, i3, i4;
  
  public void setUp() throws Exception {
    super.setUp();

    list = new JListSortNavigator<DummyINavigatorItem>();
    
    i1 = new DummyINavigatorItem("item1");
    i2 = new DummyINavigatorItem("item2");
    i3 = new DummyINavigatorItem("item3");
    i4 = new DummyINavigatorItem("item4");
    list.addDocument(i1);
    list.addDocument(i2);
    list.addDocument(i3);
    list.addDocument(i4);
  }
  
  public void testTraversalOps() {
    assertEquals("doc count test", 4, list.getDocumentCount());
    assertSame("getFirst test", i1, list.getFirst());
    assertSame("getLast test", i4, list.getLast());
    
    list.setActiveDoc(i1);
    
    Utilities.clearEventQueue();
    assertSame("getCurrent test", i1, list.getCurrent());
    assertSame("getNext test 1", i2, list.getNext(i1));
    assertSame("getNext test 2", i3, list.getNext(i2));
    assertSame("getNext test 3", i4, list.getNext(i3));

    assertSame("getPrevious test 1", i3, list.getPrevious(i4));
    assertSame("getPrevious test 2", i2, list.getPrevious(i3));
    assertSame("getPrevious test 3", i1, list.getPrevious(i2));
    
    assertTrue("contains test 1", list.contains(i1));
    assertTrue("contains test 2", list.contains(i2));
    assertTrue("contains test 3", list.contains(i3));
    assertTrue("contains test 4", list.contains(i4));
    
    assertFalse("contains test 5", list.contains(new DummyINavigatorItem("item1")));
    
    Enumeration<DummyINavigatorItem> docs = list.getDocuments();
    DummyINavigatorItem[] docsArray = new DummyINavigatorItem[4];
    for (int i = 0; i < 4; i++) docsArray[i] = docs.nextElement();
    assertTrue("getDocuments test", Arrays.equals(docsArray, new DummyINavigatorItem[] {i1, i2, i3, i4}));
  }
  
}