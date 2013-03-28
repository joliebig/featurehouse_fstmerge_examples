

package edu.rice.cs.util.docnavigation;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.*;
import java.io.File;
import java.awt.*;
import java.util.*;
import java.awt.dnd.*;
import edu.rice.cs.util.swing.*;
import edu.rice.cs.plt.collect.OneToOneRelation;
import edu.rice.cs.plt.collect.IndexedOneToOneRelation;

import edu.rice.cs.drjava.DrJavaRoot;

public class JTreeSortNavigator<ItemT extends INavigatorItem> extends JTree 
  implements IDocumentNavigator<ItemT>, TreeSelectionListener, TreeExpansionListener,
  DropTargetListener {
  
  
  private final DefaultTreeModel _model;
  
  
  private volatile NodeData<ItemT> _current;
  
  
  private final HashMap<ItemT, LeafNode<ItemT>> _doc2node = new HashMap<ItemT, LeafNode<ItemT>>();
  
  
  private final OneToOneRelation<String, InnerNode<?, ItemT>> _path2node =
    new IndexedOneToOneRelation<String, InnerNode<?, ItemT>>();
  
  
  private final ArrayList<INavigationListener<? super ItemT>> navListeners =
    new ArrayList<INavigationListener<? super ItemT>>();
  
  
  private final CustomTreeCellRenderer _renderer;
  
  private volatile DisplayManager<? super ItemT> _displayManager;
  private volatile Icon _rootIcon;
  
  private java.util.List<GroupNode<ItemT>> _roots = new LinkedList<GroupNode<ItemT>>();
  
  
  public void setForeground(Color c) {
    super.setForeground(c);
    if (_renderer != null) _renderer.setTextNonSelectionColor(c);
  }
  
  
  public void setBackground(Color c) {
    super.setBackground(c);
    if (_renderer != null) _renderer.setBackgroundNonSelectionColor(c);
  }
  
  
  public JTreeSortNavigator(String projRoot) {
    
    super(new DefaultTreeModel(new RootNode<ItemT>(projRoot.substring(projRoot.lastIndexOf(File.separator) + 1))));
    
    addTreeSelectionListener(this);
    addTreeExpansionListener(this);
    
    _model = (DefaultTreeModel) getModel();
    _renderer = new CustomTreeCellRenderer();
    _renderer.setOpaque(false);
    setCellRenderer(_renderer);

    getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    setRowHeight(18);

  }
  
  
  public JTreeSortNavigator(String projRoot, DisplayManager<? super ItemT> dm) {
    this(projRoot);
    _displayManager = dm;
  }
  
  
  public void setDisplayManager(DisplayManager<? super ItemT> manager) { _displayManager = manager; }
  
  
  public void setRootIcon(Icon ico) { _rootIcon = ico; }
  
  
  public Container asContainer() { return this; }
  
  
  public void addDocument(ItemT doc) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    addDocument(doc, "");
  }
  
  public void addDocument(ItemT doc, String path) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    synchronized(_model) { 
      
      
      GroupNode<ItemT> root = null;
      
      for (GroupNode<ItemT> r: _roots) {
        if (r.getFilter().accept(doc)) {
          root = r;
          break;
        }
      }
      
      if (root == null) return;
      
      
      StringTokenizer tok = new StringTokenizer(path, File.separator);
      
      final StringBuilder pathSoFarBuf = new StringBuilder();
      InnerNode<?, ItemT> lastNode = root;
      while (tok.hasMoreTokens()) {
        String element = tok.nextToken();
        pathSoFarBuf.append(element).append('/');
        String pathSoFar = pathSoFarBuf.toString();
        InnerNode<?, ItemT> thisNode;
        
        
        if (!_path2node.containsFirst(pathSoFar)) {
          
          
          
          thisNode = new FileNode<ItemT>(new File(pathSoFar));
          insertFolderSortedInto(thisNode, lastNode);
          this.expandPath(new TreePath(lastNode.getPath()));
          
          _path2node.add(pathSoFar, thisNode);
        }
        else {
          
          thisNode = _path2node.value(pathSoFar);
        }
        
        lastNode = thisNode;
        
        
      }
      
      
      
      LeafNode<ItemT> child = new LeafNode<ItemT>(doc);
      _doc2node.put(doc, child);
      insertNodeSortedInto(child, lastNode);

      
      this.expandPath(new TreePath(lastNode.getPath()));
    }
  }
  
  private void addTopLevelGroupToRoot(InnerNode<?, ItemT> parent) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    synchronized(_model) { 
      int indexInRoots = _roots.indexOf(parent);
      int num = _model.getChildCount(_model.getRoot());
      int i;
      for (i = 0; i < num; i++) {
        TreeNode n = (TreeNode)_model.getChild(_model.getRoot(), i);
        if(_roots.indexOf(n) > indexInRoots) break;
      }
      _model.insertNodeInto(parent, (MutableTreeNode)_model.getRoot(), i);
    }
  }
  
  
  private void insertNodeSortedInto(LeafNode<ItemT> child, InnerNode<?, ItemT> parent) {
    int numChildren = parent.getChildCount();
    String newName = child.toString();
    String oldName = parent.getUserObject().toString();
    DefaultMutableTreeNode parentsKid;
    
    
    if (((DefaultMutableTreeNode)_model.getRoot()).getIndex(parent) == -1 && _roots.contains(parent)) {
      addTopLevelGroupToRoot(parent);
    }
    int i;
    for (i = 0; i < numChildren; i++ ) {
      parentsKid = ((DefaultMutableTreeNode) parent.getChildAt(i));
      if (parentsKid instanceof InnerNode<?,?>) {
        
      } else if(parentsKid instanceof LeafNode<?>) {
        oldName = ((LeafNode<?>)parentsKid).getData().getName();
        if ((newName.toUpperCase().compareTo(oldName.toUpperCase()) < 0)) break;
      } else throw new IllegalStateException("found a node in navigator that is not an InnerNode or LeafNode");
    }
    _model.insertNodeInto(child, parent, i);
  }
  
  
  private void insertFolderSortedInto(InnerNode<?, ItemT> child, InnerNode<?, ItemT> parent) {
    int numChildren = parent.getChildCount();
    String newName = child.toString();
    String oldName = parent.getUserObject().toString();
    DefaultMutableTreeNode parentsKid;
    
    if (((DefaultMutableTreeNode)_model.getRoot()).getIndex(parent) == -1 && _roots.contains(parent)) {
      addTopLevelGroupToRoot(parent);
    }
    
    int countFolders = 0;
    int i;
    for (i = 0; i < numChildren; i++) {
      parentsKid = ((DefaultMutableTreeNode)parent.getChildAt(i));
      if (parentsKid instanceof InnerNode<?,?>) {
        countFolders++;
        oldName = parentsKid.toString();
        if ((newName.toUpperCase().compareTo(oldName.toUpperCase()) < 0)) break;
      } 
      else if (parentsKid instanceof LeafNode<?>) break;
      
      else throw new IllegalStateException("found a node in navigator that is not an InnerNode or LeafNode");
    }
    _model.insertNodeInto(child, parent, i);
  }
  
  
  public ItemT removeDocument(ItemT doc) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    synchronized(_model) { 
      LeafNode<ItemT> toRemove = getNodeForDoc(doc);
      if (toRemove == null) return null;
      return removeNode(getNodeForDoc(doc));
    }
  } 
  
  
  private LeafNode<ItemT> getNodeForDoc(ItemT doc) { 

    return _doc2node.get(doc); 

  }
  
  
  private ItemT removeNode(LeafNode<ItemT> node) {
    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
    _model.removeNodeFromParent(node);
    _doc2node.remove(node.getData());
    cleanFolderNode(parent);
    return node.getData();
  }
  
  
  private void cleanFolderNode(DefaultMutableTreeNode node) {
    if (node instanceof InnerNode<?,?> && node.getChildCount() == 0) {
      DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
      _model.removeNodeFromParent(node);
      @SuppressWarnings("unchecked") InnerNode<?, ItemT> typedNode = (InnerNode<?, ItemT>) node;
      _path2node.remove(_path2node.antecedent(typedNode), typedNode);
      cleanFolderNode(parent);
    }
  }
  
  
  public void refreshDocument(ItemT doc, String path) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);

    LeafNode<ItemT> node = _doc2node.get(doc);
    InnerNode<?, ?> oldParent;
    if (node == null) { 
      addDocument(doc, path);
      return;
    }
    
    InnerNode<?, ?> p = (InnerNode<?, ?>) node.getParent();
    oldParent = p;
    
    
    String newPath = path;
    
    if (newPath.length() > 0) {
      if (newPath.substring(0,1).equals("/")) newPath = newPath.substring(1);
      if (! newPath.substring(newPath.length() - 1).equals("/")) newPath = newPath + "/";
    }
    
    InnerNode<?, ItemT> newParent = _path2node.value(newPath); 
    
    if (newParent == oldParent) { 
      if (! node.toString().equals(doc.getName())) { 
        synchronized(_model) {
          LeafNode<ItemT> newLeaf = new LeafNode<ItemT>(doc);
          _doc2node.put(doc, newLeaf);
          insertNodeSortedInto(newLeaf, newParent);
          _model.removeNodeFromParent(node);
        }
      }
      
    } 
    else { 
      synchronized(_model) {
        removeNode(node);
        addDocument(doc, path);
      }
    }

  }
  
  
  public void selectDocument(ItemT doc) {
    assert EventQueue.isDispatchThread();

    DefaultMutableTreeNode node = _doc2node.get(doc);
    if (node == null) return; 
    if (node == _current) return;  

    TreeNode[] nodes = node.getPath();
    TreePath path = new TreePath(nodes);
    expandPath(path);
    setSelectionPath(path);  
    scrollPathToVisible(path);


  }
  
  
  private ItemT getNodeUserObject(DefaultMutableTreeNode n) {
    @SuppressWarnings("unchecked") ItemT result = (ItemT) n.getUserObject();
    return result;
  }
  
  
  public ItemT getNext(ItemT doc) {
    synchronized(_model) { 
      DefaultMutableTreeNode node = _doc2node.get(doc);
      if (node == null) return doc; 
      
      DefaultMutableTreeNode next = node.getNextLeaf();
      if (next == null || next == _model.getRoot()) { return doc; }
      else { return getNodeUserObject(next); }
    }
  }
  
  
  public ItemT getPrevious(ItemT doc) {
    synchronized(_model) { 
      DefaultMutableTreeNode node = _doc2node.get(doc);
      if (node == null) return doc; 
      
      DefaultMutableTreeNode prev = node.getPreviousLeaf();
      if (prev == null || prev == _model.getRoot()) { return doc; }
      else { return getNodeUserObject(prev); }
    }
  }
  
  
  public ItemT getFirst() {
    synchronized(_model) { 
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) _model.getRoot();
      return getNodeUserObject(root.getFirstLeaf());
    }
  }
  
  
  public ItemT getLast() {
    synchronized(_model) { 
      DefaultMutableTreeNode root = (DefaultMutableTreeNode) _model.getRoot();
      return getNodeUserObject(root.getLastLeaf());
    }
  }
  
  
  public boolean contains(ItemT doc) { 
    synchronized(_model) { return _doc2node.containsKey(doc); }  
  }
  
  
  public boolean _contains(ItemT doc) { return _doc2node.containsKey(doc); }
  
  
  public ArrayList<ItemT> getDocuments() {
    
    final ArrayList<ItemT> list = new ArrayList<ItemT>(getDocumentCount()); 
    
    synchronized(_model) { 
      
      Enumeration<?> e = ((DefaultMutableTreeNode)_model.getRoot()).depthFirstEnumeration();
      
      while(e.hasMoreElements()) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
        if (node.isLeaf() && node != _model.getRoot()) {
          list.add(getNodeUserObject(node));
        }
      }
    }
    return list;
  }
  
  
  public ArrayList<ItemT> getDocumentsInBin(String binName) {
    final ArrayList<ItemT> list = new ArrayList<ItemT>();
    
    synchronized(_model) { 
      for(GroupNode<ItemT> gn: _roots) {
        if (gn.getData().equals(binName)) {
          
          
          Enumeration<?> e = gn.depthFirstEnumeration();
          
          while(e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf() && node != _model.getRoot()) {
              list.add(getNodeUserObject(node));
            }
          }
        }
      }
    }
    
    return list;
  }
  
  
  public int getDocumentCount() { return _doc2node.size(); }
  
  
  public boolean isEmpty() { return _doc2node.isEmpty(); }
  
  
  public void clear() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    synchronized(_model) {
      _doc2node.clear();
      ((DefaultMutableTreeNode)_model.getRoot()).removeAllChildren();
    }
  }
  
  
  public void addNavigationListener(INavigationListener<? super ItemT> listener) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    synchronized(_model) { navListeners.add(listener); }  
  }
  
  
  public void removeNavigationListener(INavigationListener<? super ItemT> listener) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    synchronized(_model) { navListeners.remove(listener); }
  }
  
  
  public Collection<INavigationListener<? super ItemT>> getNavigatorListeners() { return navListeners; }
  
  
  public <InType, ReturnType> ReturnType execute(IDocumentNavigatorAlgo<ItemT, InType, ReturnType> algo, InType input) {
    return algo.forTree(this, input);
  }
  
  
  public void valueChanged(TreeSelectionEvent e) {
    Object treeNode = this.getLastSelectedPathComponent();
    if (treeNode == null || ! (treeNode instanceof NodeData<?>)) return;
    @SuppressWarnings("unchecked") NodeData<ItemT> newSelection = (NodeData<ItemT>) treeNode;
    if (_current != newSelection) {
      for(INavigationListener<? super ItemT> listener : navListeners) {
        listener.lostSelection(_current, isNextChangeModelInitiated());
        listener.gainedSelection(newSelection, isNextChangeModelInitiated());
      }
      _current = newSelection;
    }
    
    setNextChangeModelInitiated(false);
  }
  
  
  public Component getRenderer() { return _renderer; }
  
  
  private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean isExpanded,
                                                  boolean leaf, int row, boolean hasFocus) {
      
      
      super.getTreeCellRendererComponent(tree, value, sel, isExpanded, leaf, row, false);  
      
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      if (node instanceof RootNode<?> && _rootIcon != null) setIcon(_rootIcon);
      
      else if (node instanceof LeafNode<?>) {
        ItemT doc = getNodeUserObject(node);
        if (leaf && _displayManager != null) {
          setIcon(_displayManager.getIcon(doc));
          setText(_displayManager.getName(doc));
        }
      }
      return this;
    }
  }
  
  
  public boolean selectDocumentAt(int x, int y) {
    TreePath path = getPathForLocation(x, y);
    if (path == null) return false;
    else {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
      if (node instanceof LeafNode<?>) {
        this.expandPath(path);
        this.setSelectionPath(path);
        this.scrollPathToVisible(path);
        return true;
      } 
      else if (node instanceof InnerNode<?,?>) {
        this.expandPath(path);
        this.setSelectionPath(path);
        this.scrollPathToVisible(path);
        return true;
      } 
      else if (node instanceof RootNode<?>) {
        this.expandPath(path);
        this.setSelectionPath(path);
        this.scrollPathToVisible(path);
        return true;
      } 
      else return false;
    }

  }
  
  
  public boolean isSelectedAt(int x, int y) {
    TreePath path = getPathForLocation(x, y);
    if (path == null) return false;
    TreePath[] ps = getSelectionPaths();
    if (ps == null) { return false; }
    for(TreePath p: ps) {
      if (path.equals(p)) { return true; }
    }
    return false;
  }
  
  
  public boolean isGroupSelected() { return getGroupSelectedCount() != 0; }
  
  
  public int getGroupSelectedCount() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    int count = 0;
    TreePath[] ps = getSelectionPaths();
    if (ps == null) { return 0; }
    for(TreePath p: ps) {
      TreeNode n = (TreeNode) p.getLastPathComponent();
      if (n instanceof InnerNode<?,?>) { ++count; }
    }
    return count;
  }
  
  
  public java.util.List<File> getSelectedFolders() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    ArrayList<File> l = new ArrayList<File>();
    TreePath[] ps = getSelectionPaths();
    if (ps == null) { return l; }
    for(TreePath p: ps) {
      TreeNode n = (TreeNode) p.getLastPathComponent();
      if (n instanceof FileNode<?>) {
        l.add(((FileNode<?>)n).getData());
      }
    }
    return l;
  }
  
  
  public boolean isDocumentSelected() { return getDocumentSelectedCount()!=0; }
  
  
  public int getDocumentSelectedCount() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    int count = 0;
    TreePath[] ps = getSelectionPaths();
    if (ps==null) { return 0; }
    for(TreePath p: ps) {
      TreeNode n = (TreeNode) p.getLastPathComponent();
      if (n instanceof LeafNode<?>) {
        ++count;
      }
    }
    return count;
  }
  
  
  @SuppressWarnings("unchecked") public java.util.List<ItemT> getSelectedDocuments() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    ArrayList<ItemT> l = new ArrayList<ItemT>();
    TreePath[] ps = getSelectionPaths();
    if (ps==null) { return l; }
    for(TreePath p: ps) {
      TreeNode n = (TreeNode) p.getLastPathComponent();
      if (n instanceof LeafNode) {
        l.add((ItemT)((LeafNode)n).getData());
      }
    }
    return l;
  }
  
  
  public boolean isTopLevelGroupSelected() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    TreePath[] ps = getSelectionPaths();
    if (ps==null) { return false; }
    for(TreePath p: ps) {
      TreeNode n = (TreeNode) p.getLastPathComponent();
      if (n instanceof GroupNode<?>) { return true; }
    }
    return false;
  }
  
  
  public boolean isRootSelected() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    TreePath[] ps = getSelectionPaths();
    if (ps==null) { return false; }
    for(TreePath p: ps) {
      TreeNode n = (TreeNode) p.getLastPathComponent();
      if (n == _model.getRoot()) { return true; }
    }
    return false;
  }
  
  
  public java.util.Set<String> getNamesOfSelectedTopLevelGroup() throws GroupNotSelectedException {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    
    HashSet<String> names = new HashSet<String>();
    LinkedList<GroupNode<ItemT>> roots = new LinkedList<GroupNode<ItemT>>(_roots);
    
    TreePath[] ps = getSelectionPaths();
    if (ps!=null) {
      for(TreePath p: ps) {
        if (p.getLastPathComponent() instanceof DefaultMutableTreeNode) {
          DefaultMutableTreeNode n = (DefaultMutableTreeNode) p.getLastPathComponent();
          
          for(GroupNode<ItemT> gn: roots) {
            if (gn.isNodeDescendant(n)) {
              
              names.add(gn.getData());
              
              
              roots.remove(gn);
              break;
            }
          }
        }
      }
    }
    
    if (names.isEmpty()) { throw new GroupNotSelectedException("there is no top level group for the root of the tree"); }
    
    return names;
  }
  
  
  public ItemT getCurrent() {
    NodeData<ItemT> current = _current;
    if (current == null) return null;
    return current.execute(_leafVisitor);
  }
  
  
  public Object getModelLock() { return _model; }
  
  
  
  
  private final NodeDataVisitor<ItemT, ItemT> _leafVisitor = new NodeDataVisitor<ItemT, ItemT>() {
    public ItemT fileCase(File f, Object... p){ return null; }
    public ItemT stringCase(String s, Object... p){ return null; }
    public ItemT itemCase(ItemT ini, Object... p){ return ini; }
  };
  
  
  public boolean isSelectedInGroup(ItemT i) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    
    TreePath[] ps = getSelectionPaths();
    if (ps==null) { return false; }
    for(TreePath p: ps) {
      TreeNode n = (TreeNode) p.getLastPathComponent();
      TreeNode l = _doc2node.get(i);
      
      if (n == _model.getRoot()) return true;
      
      while (l.getParent() != _model.getRoot()) {
        if(l.getParent() == n) return true;
        l = l.getParent();
      }
    }
    
    return false;
  }
  
  
  public void addTopLevelGroup(String name, INavigatorItemFilter<? super ItemT> f){
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    
    if (f == null)
      throw new IllegalArgumentException("parameter 'f' is not allowed to be null");
    GroupNode<ItemT> n = new GroupNode<ItemT>(name, f);
    _roots.add(n);
  }
  
  
  
  
  public void treeCollapsed(TreeExpansionEvent event) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    
    Object o = event.getPath().getLastPathComponent();
    if (o instanceof InnerNode<?,?>) ((InnerNode<?,?>)o).setCollapsed(true);
  }
  
  
  public void treeExpanded(TreeExpansionEvent event) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    
    Object o = event.getPath().getLastPathComponent();
    if (o instanceof InnerNode<?,?>) ((InnerNode<?,?>)o).setCollapsed(false);
  }
  
  
  public void collapsePaths(String[] paths) {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    
    HashSet<String> set = new HashSet<String>();
    for (String s : paths) { set.add(s); }
    collapsePaths(set);
  }
  
  
  void collapsePaths(HashSet<String> paths) {
    
    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)_model.getRoot();
    
    Enumeration<?> nodes = rootNode.depthFirstEnumeration();

    while (nodes.hasMoreElements()) {
      DefaultMutableTreeNode tn = (DefaultMutableTreeNode)nodes.nextElement();
      if (tn instanceof InnerNode<?,?>) {
        TreePath tp = new TreePath(tn.getPath());
        String s = generatePathString(tp);
        boolean shouldCollapse = paths.contains(s);
        if (shouldCollapse) { collapsePath(tp); }
      }
    }
  }
  
  
  public String[] getCollapsedPaths() {
    assert (EventQueue.isDispatchThread() || Utilities.TEST_MODE);
    
    ArrayList<String> list = new ArrayList<String>();

    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)_model.getRoot();
    
    Enumeration<?> nodes = rootNode.depthFirstEnumeration(); 
    while (nodes.hasMoreElements()) {
      DefaultMutableTreeNode tn = (DefaultMutableTreeNode)nodes.nextElement();
      if (tn instanceof InnerNode<?,?> && ((InnerNode<?,?>)tn).isCollapsed()) {
        TreePath tp = new TreePath(tn.getPath());
        list.add(generatePathString(tp));
      }
    }

    return list.toArray(new String[list.size()]);
  }
  
  
  public String generatePathString(TreePath tp) {
    String path = "";

    TreeNode root = (TreeNode) _model.getRoot();
    
    while (tp != null) {
      TreeNode curr = (TreeNode) tp.getLastPathComponent();
      if (curr == root) path = "./" + path;
      else path = curr + "/" + path;
      tp = tp.getParentPath();

    }
    
    return path;
  }
  
  
  public void requestSelectionUpdate(ItemT ini) {

    
    
    




  }  
  
  
  public void setNextChangeModelInitiated(boolean b) {
    putClientProperty(MODEL_INITIATED_PROPERTY_NAME, b?Boolean.TRUE:null);
  }
  
  
  public boolean isNextChangeModelInitiated() {
    return getClientProperty(MODEL_INITIATED_PROPERTY_NAME) != null;
  }
  


  
  
  DropTarget dropTarget = new DropTarget(this, this);  
  
  
  public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
    DrJavaRoot.dragEnter(dropTargetDragEvent);
  }
  
  public void dragExit(DropTargetEvent dropTargetEvent) {}
  public void dragOver(DropTargetDragEvent dropTargetDragEvent) {}
  public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent){}
  
  
  public synchronized void drop(DropTargetDropEvent dropTargetDropEvent) {
    DrJavaRoot.drop(dropTargetDropEvent);
  }
}

