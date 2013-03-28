




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
import edu.rice.cs.util.*;
import edu.rice.cs.util.swing.*;

public class JTreeSortNavigator<ItemT extends INavigatorItem> extends JTree 
  implements IDocumentNavigator<ItemT>, TreeSelectionListener, TreeExpansionListener {
  
  
  private DefaultTreeModel _model;
   
  
  private NodeData<ItemT> _current;
  
  
  private HashMap<ItemT, LeafNode<ItemT>> _doc2node = new HashMap<ItemT, LeafNode<ItemT>>();
  
  
  private BidirectionalHashMap<String, InnerNode<?, ItemT>> _path2node = new BidirectionalHashMap<String, InnerNode<?, ItemT>>();
  




  


  
  
  private Vector<INavigationListener<? super ItemT>> navListeners = new Vector<INavigationListener<? super ItemT>>();
  
  
  private CustomTreeCellRenderer _renderer;
  
  private DisplayManager<? super ItemT> _displayManager;
  private Icon _rootIcon;
  
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
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
    addDocument(doc, "");

















  }
  
  public void addDocument(ItemT doc, String path) {
    
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
      
      StringBuffer pathSoFarBuf = new StringBuffer();
      InnerNode<?, ItemT> lastNode = root;
      while (tok.hasMoreTokens()) {
        String element = tok.nextToken();
        pathSoFarBuf.append(element).append('/');
        String pathSoFar = pathSoFarBuf.toString();
        InnerNode<?, ItemT> thisNode;
        
        
        if (!_path2node.containsKey(pathSoFar)) {
          
          
          
          thisNode = new FileNode<ItemT>(new File(pathSoFar));
          insertFolderSortedInto(thisNode, lastNode);
          this.expandPath(new TreePath(lastNode.getPath()));
          
          _path2node.put(pathSoFar, thisNode);
        }
        else {
          
          thisNode = _path2node.getValue(pathSoFar);
        }
        
        lastNode = thisNode;
        
        
      }
      
      
      
      LeafNode<ItemT> child = new LeafNode<ItemT>(doc);
      _doc2node.put(doc, child);
      insertNodeSortedInto(child, lastNode);

      
      this.expandPath(new TreePath(lastNode.getPath()));
      child.setUserObject(doc);
    }
  }
  
  private void addTopLevelGroupToRoot(InnerNode<?, ItemT> parent) {
    
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
    
    
    synchronized (_model) {
      if (((DefaultMutableTreeNode)_model.getRoot()).getIndex(parent) == -1 && _roots.contains(parent)) {
        addTopLevelGroupToRoot(parent);
      }
      int i;
      for (i = 0; i < numChildren; i++ ) {
        parentsKid = ((DefaultMutableTreeNode) parent.getChildAt(i));
        if (parentsKid instanceof InnerNode) {
          
        } else if(parentsKid instanceof LeafNode) {
          oldName = ((LeafNode<?>)parentsKid).getData().getName();
          if ((newName.toUpperCase().compareTo(oldName.toUpperCase()) < 0)) break;
        } else throw new IllegalStateException("found a node in navigator that is not an InnerNode or LeafNode");
      }
      _model.insertNodeInto(child, parent, i);
    }
  }
  
  
  private void insertFolderSortedInto(InnerNode<?, ItemT> child, InnerNode<?, ItemT> parent){
    int numChildren = parent.getChildCount();
    String newName = child.toString();
    String oldName = parent.getUserObject().toString();
    DefaultMutableTreeNode parentsKid;
    
    synchronized (_model) {
      if (((DefaultMutableTreeNode)_model.getRoot()).getIndex(parent) == -1 && _roots.contains(parent)) {
        addTopLevelGroupToRoot(parent);
      }
      
      int countFolders = 0;
      int i;
      for (i = 0; i < numChildren; i++) {
        parentsKid = ((DefaultMutableTreeNode)parent.getChildAt(i));
        if (parentsKid instanceof InnerNode) {
          countFolders++;
          oldName = parentsKid.toString();
          if ((newName.toUpperCase().compareTo(oldName.toUpperCase()) < 0)) break;
        } 
        else if (parentsKid instanceof LeafNode) break;
        
        else throw new IllegalStateException("found a node in navigator that is not an InnerNode or LeafNode");
      }
      _model.insertNodeInto(child, parent, i);
    }
  }
  
  
  public ItemT removeDocument(ItemT doc) {
    synchronized(_model) { 
      LeafNode<ItemT> toRemove = getNodeForDoc(doc);
      if (toRemove == null) return null;
      return removeNode(getNodeForDoc(doc));
    }
  } 
  
  private LeafNode<ItemT> getNodeForDoc(ItemT doc) { 
    synchronized(_model) { return _doc2node.get(doc); }
  }
  
  
  private ItemT removeNode(LeafNode<ItemT> toRemove) {
    DefaultMutableTreeNode parent = (DefaultMutableTreeNode)toRemove.getParent();
    _model.removeNodeFromParent(toRemove);
    _doc2node.remove(toRemove.getData());
    
    cleanFolderNode(parent);
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    return toRemove.getData();
  }
  
  
  private void cleanFolderNode(DefaultMutableTreeNode node) {
    synchronized(_model) {
      if (node instanceof InnerNode && node.getChildCount() == 0) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        _model.removeNodeFromParent(node);
        @SuppressWarnings("unchecked") InnerNode<?, ItemT> typedNode = (InnerNode<?, ItemT>) node;
        _path2node.removeKey(typedNode);
        cleanFolderNode(parent);
      }
    }
  }
  
  
  public void refreshDocument(ItemT doc, String path) {
    
    synchronized (_model) {
      LeafNode<ItemT> node = getNodeForDoc(doc);
      InnerNode<?, ?> oldParent;
      if (node == null) {
        addDocument(doc, path);
        oldParent = null;
      }
      else {
        InnerNode<?, ?> p = (InnerNode<?, ?>) node.getParent();
        oldParent = p;
      }
      
      
      String newPath = path;
      
      if (newPath.length() > 0) {
        if (newPath.substring(0,1).equals("/")) newPath = newPath.substring(1);
        if (!newPath.substring(newPath.length()-1).equals("/")) newPath = newPath + "/";
      }
      
      InnerNode<?, ItemT> newParent = _path2node.getValue(newPath); 
      
      
      
      
      
      
      
      if (newParent == oldParent) { 
        if (!node.toString().equals(doc.getName())) {
          LeafNode<ItemT> newLeaf= new LeafNode<ItemT>(doc);
          _doc2node.put(doc,newLeaf);
          insertNodeSortedInto(newLeaf, newParent);
          _model.removeNodeFromParent(node);
        }
        
      } 
      else {
        removeNode(node);
        addDocument(doc, path);
      }
    }
  }
  
  
  public void setActiveDoc(ItemT doc){
    synchronized (_model) {
      DefaultMutableTreeNode node = _doc2node.get(doc);
      if (node == _current) return;  
      if (this.contains(doc)) {
        TreeNode[] nodes = node.getPath();
        TreePath path = new TreePath(nodes);
        expandPath(path);
        setSelectionPath(path);
        scrollPathToVisible(path);
      }
    }
  }
  
  
  private ItemT getNodeUserObject(DefaultMutableTreeNode n) {
    @SuppressWarnings("unchecked") ItemT result = (ItemT) n.getUserObject();
    return result;
  }
  
  
  public ItemT getNext(ItemT doc) {
    synchronized (_model) {
      DefaultMutableTreeNode node = _doc2node.get(doc);
      if (node == null) return doc; 
      
      DefaultMutableTreeNode next = node.getNextLeaf();
      if (next == null || next == _model.getRoot()) { return doc; }
      else { return getNodeUserObject(next); }
    }
  }
  
  
  public ItemT getPrevious(ItemT doc) {
    synchronized (_model) {
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
    synchronized (_model) { return _doc2node.containsKey(doc); }
  }
  
  
  public Enumeration<ItemT> getDocuments() {
    
    final Vector<ItemT> list = new Vector<ItemT>(); 
    
    synchronized(_model) {
      
      Enumeration e = ((DefaultMutableTreeNode)_model.getRoot()).depthFirstEnumeration();
      
      while(e.hasMoreElements()) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
        if (node.isLeaf() && node != _model.getRoot()) {
          list.add(getNodeUserObject(node));
        }
      }
    }
    return list.elements();
  }
  
  
  public int getDocumentCount() { return _doc2node.size(); }
  
  
  public boolean isEmpty() { return _doc2node.isEmpty(); }
  
  
  public void clear() { 
    synchronized (_model) {
      _doc2node.clear();
      ((DefaultMutableTreeNode)_model.getRoot()).removeAllChildren();
    }
  }
  
  
  public void addNavigationListener(INavigationListener<? super ItemT> listener) {
    synchronized (_model) { navListeners.add(listener); }
  }
  
  
  public void removeNavigationListener(INavigationListener<? super ItemT> listener) {
    synchronized (_model) { navListeners.remove(listener); }
  }
  
  
  public Collection<INavigationListener<? super ItemT>> getNavigatorListeners() { return navListeners; }
  
  
  public <InType, ReturnType> ReturnType execute(IDocumentNavigatorAlgo<ItemT, InType, ReturnType> algo, InType input) {
    return algo.forTree(this, input);
  }
  
  
  public void valueChanged(TreeSelectionEvent e) {
    synchronized (_model) {
      Object treeNode = this.getLastSelectedPathComponent();
      if (treeNode == null || !(treeNode instanceof NodeData)) return;
      @SuppressWarnings("unchecked") NodeData<ItemT> newSelection = (NodeData<ItemT>) treeNode;
      if (_current != newSelection) {
        for(INavigationListener<? super ItemT> listener : navListeners) {
          listener.lostSelection(_current);
          listener.gainedSelection(newSelection);
        }
        _current = newSelection;
      }
    }
  }
  
  
  public Component getRenderer() { return _renderer; }
  
  
  private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean isExpanded,
                                                  boolean leaf, int row, boolean hasFocus) {
      
      super.getTreeCellRendererComponent(tree, value, sel, isExpanded, leaf, row, hasFocus);
      
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      if (node instanceof RootNode && _rootIcon != null) setIcon(_rootIcon);
      
      else if (node instanceof LeafNode) {
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
    synchronized (_model) {
      TreePath path = getPathForLocation(x, y);
      if (path == null) return false;
      else {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        if (node instanceof LeafNode) {
          this.expandPath(path);
          this.setSelectionPath(path);
          this.scrollPathToVisible(path);
          return true;
        } 
        else if (node instanceof InnerNode) {
          this.expandPath(path);
          this.setSelectionPath(path);
          this.scrollPathToVisible(path);
          return true;
        } 
        else if (node instanceof RootNode) {
          this.expandPath(path);
          this.setSelectionPath(path);
          this.scrollPathToVisible(path);
          return true;
        } 
        else return false;
      }
    }
  }
  
  
  public boolean isGroupSelected() {
    synchronized (_model) {
      TreePath p = getSelectionPath();
      TreeNode n = (TreeNode) p.getLastPathComponent();
      return (n instanceof InnerNode);
    }
  }
  
  
  public boolean isTopLevelGroupSelected() {
    synchronized (_model) {
      TreePath p = getSelectionPath();
      TreeNode n = (TreeNode) p.getLastPathComponent();
      return (n instanceof GroupNode);
    }
  }
  
  
  public String getNameOfSelectedTopLevelGroup() throws GroupNotSelectedException {
    synchronized (_model) {
      TreePath p = getSelectionPath();
      TreeNode n = (TreeNode) p.getLastPathComponent();
      
      if (n == _model.getRoot())
        throw new GroupNotSelectedException("there is no top level group for the root of the tree");
      
      while(!_roots.contains(n)) { n = n.getParent(); }
      
      return ((GroupNode<?>)n).getData();
    }
  }
  
  
  public ItemT getCurrent() {
    synchronized (_model) {
      if (_current == null) return null;
      return _current.execute(_leafVisitor);
    }
  }
  
  private NodeDataVisitor<ItemT, ItemT> _leafVisitor = new NodeDataVisitor<ItemT, ItemT>() {
    public ItemT fileCase(File f){ return null; }
    public ItemT stringCase(String s){ return null; }
    public ItemT itemCase(ItemT ini){ return ini; }
  };
  
  
  public boolean isSelectedInGroup(ItemT i) {
    synchronized (_model) {
      TreePath p = getSelectionPath();
      TreeNode n = (TreeNode) p.getLastPathComponent();
      TreeNode l = _doc2node.get(i);
      
      if (n == _model.getRoot()) return true;
      
      while (l.getParent() != _model.getRoot()) {
        if(l.getParent() == n) return true;
        l = l.getParent();
      }
      
      return false;
    }
  }
  
  
  public synchronized void addTopLevelGroup(String name, INavigatorItemFilter<? super ItemT> f){
    if (f == null)
      throw new IllegalArgumentException("parameter 'f' is not allowed to be null");
    GroupNode<ItemT> n = new GroupNode<ItemT>(name, f);
    _roots.add(n);
  }
  
  
  
  
  public synchronized void treeCollapsed(TreeExpansionEvent event) {
    Object o = event.getPath().getLastPathComponent();
    if (o instanceof InnerNode) ((InnerNode<?, ?>)o).setCollapsed(true);
  }
  
  
  public synchronized void treeExpanded(TreeExpansionEvent event) {
    Object o = event.getPath().getLastPathComponent();
    if (o instanceof InnerNode) ((InnerNode<?, ?>)o).setCollapsed(false);
  }
  
  
  public void collapsePaths(String[] paths) {
    HashSet<String> set = new HashSet<String>();
    for (String s : paths) { set.add(s); }
    collapsePaths(set);
  }
  
  void collapsePaths(HashSet<String> paths) {
    synchronized (_model) {
      DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)_model.getRoot();
      
      Enumeration nodes = rootNode.depthFirstEnumeration();
      ArrayList<String> list = new ArrayList<String>();
      while (nodes.hasMoreElements()) {
        DefaultMutableTreeNode tn = (DefaultMutableTreeNode)nodes.nextElement();
        if (tn instanceof InnerNode) {
          TreePath tp = new TreePath(tn.getPath());
          String s = generatePathString(tp);
          boolean shouldCollapse = paths.contains(s);
          if (shouldCollapse) { 
            collapsePath(tp);
          }
        }
      }
    }
  }
  
  
  public String[] getCollapsedPaths() {
    ArrayList<String> list = new ArrayList<String>();
    synchronized (_model) {
      DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)_model.getRoot();
      
      Enumeration nodes = rootNode.depthFirstEnumeration(); 
      while (nodes.hasMoreElements()) {
        DefaultMutableTreeNode tn = (DefaultMutableTreeNode)nodes.nextElement();
        if (tn instanceof InnerNode && ((InnerNode<?, ?>)tn).isCollapsed()) {
          TreePath tp = new TreePath(tn.getPath());
          list.add(generatePathString(tp));
        }
      }
    }
    return list.toArray(new String[list.size()]);
  }
  
  
  public String generatePathString(TreePath tp) {
    String path = "";
    synchronized (_model) {
      TreeNode root = (TreeNode) _model.getRoot();
      
      while (tp != null) {
        TreeNode curr = (TreeNode) tp.getLastPathComponent();
        if (curr == root) path = "./" + path;
        else path = curr + "/" + path;
        tp = tp.getParentPath();
      }
    }
    
    return path;
  }
  
  
  public void requestSelectionUpdate(ItemT ini) {
    synchronized (_model) {
      if (getCurrent() == null) { 
        setActiveDoc(ini);
      }
    }
  }
  


}

