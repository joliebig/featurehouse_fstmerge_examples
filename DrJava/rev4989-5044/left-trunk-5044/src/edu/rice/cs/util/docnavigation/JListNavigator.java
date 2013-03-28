

package edu.rice.cs.util.docnavigation;

import java.io.File;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import edu.rice.cs.util.swing.Utilities;




class JListNavigator<ItemT extends INavigatorItem> extends JList implements IDocumentNavigator<ItemT> {
  
  
  protected DefaultListModel _model;
  
  
  private volatile ItemT _current = null;
  


  
  
  private volatile CustomListCellRenderer _renderer;
  
  
  private final ArrayList<INavigationListener<? super ItemT>> navListeners = new ArrayList<INavigationListener<? super ItemT>>();
  
  
  public JListNavigator() { 
    super();
    init(new DefaultListModel());
  }
  
  private void init(DefaultListModel m) {
    _model = m;
    setModel(m);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    addListSelectionListener(new ListSelectionListener() {
      
      public void valueChanged(final ListSelectionEvent e) {



        if (!e.getValueIsAdjusting() && !_model.isEmpty()) {
          @SuppressWarnings("unchecked") final ItemT newItem = (ItemT) getSelectedValue();

          if (_current != newItem) {                                
            final ItemT oldItem = _current;                                
            NodeData<ItemT> oldData = new NodeData<ItemT>() {
              public <Ret> Ret execute(NodeDataVisitor<? super ItemT, Ret> v, Object... p) { 
                return v.itemCase(oldItem, p); 
              }
            };
            NodeData<ItemT> newData = new NodeData<ItemT>() {
              public <Ret> Ret execute(NodeDataVisitor<? super ItemT, Ret> v, Object... p) { 
                return v.itemCase(newItem, p); 
              }
            };
            for(INavigationListener<? super ItemT> listener: navListeners) {
              if (oldItem != null) listener.lostSelection(oldData, isNextChangeModelInitiated());
              if (newItem != null) listener.gainedSelection(newData, isNextChangeModelInitiated());
            }
            setNextChangeModelInitiated(false);
            _current = newItem;

          }
        }


      }
    });
    
    _renderer = new CustomListCellRenderer();
    _renderer.setOpaque(true);
    this.setCellRenderer(_renderer);
  }
  
  
  public void addDocument(ItemT doc) { synchronized(_model) { _model.addElement(doc); } }
  
  
  public void addDocument(ItemT doc, String path) { addDocument(doc); }
  
  
  protected ItemT getFromModel(int i) {
    @SuppressWarnings("unchecked") ItemT result = (ItemT) _model.get(i);
    return result;
  }
  
  
  public ItemT getNext(ItemT doc) { 
    synchronized(_model) {
      int i = _model.indexOf(doc);
      if (i == -1)
        throw new IllegalArgumentException("No such document " + doc.toString() + " found in collection of open documents");
      if ( i + 1 == _model.size()) return doc;
      
      return getFromModel(i + 1);
    }
  }
  
  
  public ItemT getPrevious(ItemT doc) {  
    synchronized(_model) {
      int i = _model.indexOf(doc);
      if (i == -1)
        throw new IllegalArgumentException("No such document " + doc.toString() + " found in collection of open documents");
      if (i == 0) return doc;
      return getFromModel(i - 1);
    }
  }
  
  
  public ItemT getFirst() { synchronized(_model) { return getFromModel(0); } }
  
  
  public ItemT getLast() { synchronized(_model) { return getFromModel(_model.size() - 1); } }
  
  
  public ItemT getCurrent() { return _current; }
  
  
  public Object getModelLock() { return _model; }
  
  
  public ItemT removeDocument(ItemT doc) {
    synchronized(_model) {
      
      int i = _model.indexOf(doc);
      if( i == -1 )
        throw new IllegalArgumentException("Document " + doc + " not found in Document Navigator");
      ItemT result = getFromModel(i);
      _model.remove(i);
      return result;
    }
  }
  
  
  public void refreshDocument(ItemT doc, String path) {
    synchronized(_model) {
      removeDocument(doc);
      addDocument(doc);
    }
  }
  
  
  public void selectDocument(ItemT doc) { 
 assert Utilities.TEST_MODE || EventQueue.isDispatchThread();
    boolean found;

    if (_current == doc) return; 
    found = _model.contains(doc);

    if (found) setSelectedValue(doc, true);   


  }
  
  
  public boolean contains(ItemT doc) { 
    synchronized(_model) { return _model.contains(doc); }
  }
  
  
  public ArrayList<ItemT> getDocuments() { 
    synchronized(_model) {

      @SuppressWarnings("unchecked") Enumeration<ItemT> items = (Enumeration<ItemT>) _model.elements();
      ArrayList<ItemT> result = new ArrayList<ItemT>(_model.size());
      while (items.hasMoreElements()) result.add(items.nextElement());
      return result;                               
    }
  }
  
  
  public ArrayList<ItemT> getDocumentsInBin(String binName) { return new ArrayList<ItemT>(0); }
  
  
  public int getDocumentCount() { return _model.size(); }
  
  
  public boolean isEmpty() { return _model.isEmpty(); }
  
  
  public void addNavigationListener(INavigationListener<? super ItemT> listener) { 
    synchronized(_model) { navListeners.add(listener); }
  }
  
  
  public void removeNavigationListener(INavigationListener<? super ItemT> listener) { 
    synchronized(_model) { navListeners.remove(listener); }
  }
  
  
  public Collection<INavigationListener<? super ItemT>> getNavigatorListeners() { return navListeners; }
  
  
  public void clear() { synchronized(_model) { _model.clear(); } }
  
  
  public <InType, ReturnType> ReturnType execute(IDocumentNavigatorAlgo<ItemT, InType, ReturnType> algo, InType input) {
    return algo.forList(this, input);
  }
  
  
  public Container asContainer() { return this; }
  
  
  public boolean selectDocumentAt(final int x, final int y) {
    synchronized(_model) {
      final int idx = locationToIndex(new java.awt.Point(x,y));
      java.awt.Rectangle rect = getCellBounds(idx, idx);
      if (rect.contains(x, y)) {
        selectDocument(getFromModel(idx));
        return true;
      }
      return false;
    }
  }
  
  
  public boolean isSelectedAt(int x, int y) { return false;





  }
  
  
  public Component getRenderer(){ return _renderer; }
  
  
  public int getSelectionCount() { return 1; } 
  
  
  public boolean isGroupSelected() { return false; }
  
  
  public int getGroupSelectedCount() { return 0; }
  
  
  public java.util.List<File> getSelectedFolders() { return new ArrayList<File>(); }
  
  
  public boolean isDocumentSelected() { return true; }
  
  
  public int getDocumentSelectedCount() { return getSelectionCount(); }
  
  
  @SuppressWarnings("unchecked") public java.util.List<ItemT> getSelectedDocuments() {



    ArrayList<ItemT> l = new ArrayList<ItemT>(1);
    l.add((ItemT)getSelectedValue());
    return l;
  }
  
  
  public boolean isRootSelected() { return false; }
  
  
  public boolean isSelectedInGroup(ItemT i) { return false; }
  
  public void addTopLevelGroup(String name, INavigatorItemFilter<? super ItemT> f) {  }
  
  public boolean isTopLevelGroupSelected() { return false; }
  
  
  public java.util.Set<String> getNamesOfSelectedTopLevelGroup() throws GroupNotSelectedException{
    throw new GroupNotSelectedException("A top level group is not selected");
  }
  
  
  public void requestSelectionUpdate(ItemT doc) {  }
  










  public String toString() { synchronized(_model) { return _model.toString(); } }
  
  
  private static class CustomListCellRenderer extends DefaultListCellRenderer {
    
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
      
      super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
      setText(((INavigatorItem)value).getName());
      return this;
    }
  }
  
  
  public void setNextChangeModelInitiated(boolean b) {
    putClientProperty(MODEL_INITIATED_PROPERTY_NAME, b?Boolean.TRUE:null);
  }
  
  
  public boolean isNextChangeModelInitiated() {
    return getClientProperty(MODEL_INITIATED_PROPERTY_NAME) != null;
  }
}
