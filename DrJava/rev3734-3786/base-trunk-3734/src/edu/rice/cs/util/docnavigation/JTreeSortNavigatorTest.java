 

package edu.rice.cs.util.docnavigation;

import edu.rice.cs.drjava.DrJavaTestCase;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.HashSet;

public class JTreeSortNavigatorTest extends DrJavaTestCase {
  
  protected JTreeSortNavigator<DummyINavigatorItem> tree;
  protected DefaultMutableTreeNode root;
  protected DefaultMutableTreeNode source;
  protected DefaultMutableTreeNode folder1;
  protected DefaultMutableTreeNode folder2;
  protected String projName;
  DummyINavigatorItem i1, i2, i3, i4;
  
  public void setUp() throws Exception {
    super.setUp();

    File f = File.createTempFile("project-",".pjt").getCanonicalFile();
    tree = new JTreeSortNavigator<DummyINavigatorItem>(f.getCanonicalPath());
    
    tree.addTopLevelGroup("[ Source Files ]", new INavigatorItemFilter<INavigatorItem>(){
      public boolean accept(INavigatorItem n) { return true; }
    });
    i1 = new DummyINavigatorItem("item1");
    i2 = new DummyINavigatorItem("item2");
    i3 = new DummyINavigatorItem("item1");
    i4 = new DummyINavigatorItem("item2");
    tree.addDocument(i1, "folder1");
    tree.addDocument(i2, "folder1");
    tree.addDocument(i3, "folder2");
    tree.addDocument(i4, "folder2");
    
    root = (DefaultMutableTreeNode)tree.getModel().getRoot();
    source = (DefaultMutableTreeNode)root.getChildAt(0);
    folder1 = (DefaultMutableTreeNode)source.getChildAt(0);
    folder2 = (DefaultMutableTreeNode)source.getChildAt(1);
    
    projName = root.toString();
  }
  
  public void testTraversalOps() {
    assertEquals("doc count test", 4, tree.getDocumentCount());
    assertSame("getFirst test", i1, tree.getFirst());
    assertSame("getLast test", i4, tree.getLast());
    
    tree.setActiveDoc(i1);
    assertSame("getCurrent test", i1, tree.getCurrent());
    assertSame("getNext test 1", i2, tree.getNext(i1));
    assertSame("getNext test 2", i3, tree.getNext(i2));
    assertSame("getNext test 3", i4, tree.getNext(i3));

    assertSame("getPrevious test 1", i3, tree.getPrevious(i4));
    assertSame("getPrevious test 2", i2, tree.getPrevious(i3));
    assertSame("getPrevious test 3", i1, tree.getPrevious(i2));
  }
  
  public void testGeneratePathString() {
    TreePath tp = new TreePath(root.getPath());
    assertEquals("Path String for Root", "./", tree.generatePathString(tp));
    
    tp = new TreePath(source.getPath());
    assertEquals("Path String for source", "./[ Source Files ]/", tree.generatePathString(tp));
    
    tp = new TreePath(folder1.getPath());

    assertEquals("Path String for folder1", "./[ Source Files ]/folder1/", tree.generatePathString(tp));
    
    tp = new TreePath(folder2.getPath());
    assertEquals("Path String for folder2", "./[ Source Files ]/folder2/", tree.generatePathString(tp));
  }
  
  public void testCollapsedPaths() {
    HashSet<String> set = new HashSet<String>();
    set.add("./[ Source Files ]/folder1/");
    set.add("./[ Source Files ]/");
    
    
    TreePath tp1 = new TreePath(source.getPath());
    TreePath tp2 = new TreePath(folder1.getPath());
    TreePath tp3 = new TreePath(folder2.getPath());
    
    tree.collapsePaths(set);
    assertTrue("Source should be collapsed.", tree.isCollapsed(tp1));
    assertTrue("Source InnerNode should say it is collapsed.", 
               ((InnerNode<?, ?>)tp1.getLastPathComponent()).isCollapsed());
    assertTrue("Folder1 should be collapsed.", tree.isCollapsed(tp2));


    assertTrue("folder1 InnerNode should say it is collapsed.", 
               ((InnerNode<?, ?>)tp2.getLastPathComponent()).isCollapsed());
    assertTrue("Tree should say Folder2 is collapsed.", tree.isCollapsed(tp3));
    assertFalse("folder2 InnerNode should not say it is collapsed.",
                ((InnerNode<?, ?>)tp3.getLastPathComponent()).isCollapsed());
    
    HashSet<String> tmp = new HashSet<String>();
    for(String s : tree.getCollapsedPaths()) {
      tmp.add(s);
    }
    assertEquals("Collapsed paths given should be collapsed paths received.", set, tmp);
    
  }
  

  
  public void testRenameDocument() {
    String name = "MyTest.dj0";
    String newName = "MyTest.dj0*";
    DummyINavigatorItem item = new DummyINavigatorItem(name);
    DummyINavigatorItem newItem = new DummyINavigatorItem(newName);


      tree.addDocument(item, "folder3");

    InnerNode folder3 = (InnerNode)source.getChildAt(2);
    assertEquals("folder3 should have 1 children", 1, folder3.getChildCount());

      tree.refreshDocument(item, "folder3");


      assertEquals("folder3 should have 1 children", 1, folder3.getChildCount());
      LeafNode<?> node = (LeafNode<?>)folder3.getChildAt(0);
      assertEquals("node should have correct name", name, node.toString());
      tree.removeDocument(item);
      tree.addDocument(newItem, "folder3");
      folder3 = (InnerNode)source.getChildAt(2);
      LeafNode<?> newNode = (LeafNode<?>)folder3.getChildAt(0);
      

      assertEquals("should have been renamed", newName, newNode.toString());
      assertEquals("node should have same parent", folder3, newNode.getParent());
      tree.removeDocument(newItem);

  }
  
}