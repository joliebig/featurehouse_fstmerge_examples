

package edu.rice.cs.drjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.NoSuchElementException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.OrderedDocumentRegion;
import edu.rice.cs.drjava.model.RegionManager;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.RightClickMouseAdapter;

import edu.rice.cs.plt.lambda.Thunk;


public abstract class RegionsTreePanel<R extends OrderedDocumentRegion> extends TabbedPanel {
  protected JPanel _leftPane;
  
  protected DefaultMutableTreeNode _rootNode;
  protected DefaultTreeModel _regTreeModel;
  public JTree _regTree;
  protected String _title;
  protected RegionManager<R> _regionManager;
  
  protected JPopupMenu _regionPopupMenu;
  
  protected final SingleDisplayModel _model;
  protected final MainFrame _frame;
  
  protected JPanel _buttonPanel;
  
  protected DefaultTreeCellRenderer dtcr;
  
  protected boolean _hasNextPrevButtons = true;
  
  protected JButton _prevButton;
  
  protected JButton _nextButton;
   
  protected R _lastSelectedRegion = null;
  
  
  






  
  
  protected final IChangeState DEFAULT_STATE = new DefaultState();

  protected IChangeState _changeState = DEFAULT_STATE;
  
  
  protected volatile HashMap<OpenDefinitionsDocument, DefaultMutableTreeNode> _docToTreeNode = 
    new HashMap<OpenDefinitionsDocument, DefaultMutableTreeNode>();
  
  
  protected volatile IdentityHashMap<R, DefaultMutableTreeNode> _regionToTreeNode = 
    new IdentityHashMap<R, DefaultMutableTreeNode>();
  
  




  
  
  public RegionsTreePanel(MainFrame frame, String title, RegionManager<R> regionManager) {
    this(frame, title, regionManager, true);
  }
  
  
  public RegionsTreePanel(MainFrame frame, String title, RegionManager<R> regionManager,
                          boolean hasNextPrevButtons) {
    super(frame, title);
    _title = title;
    _regionManager = regionManager;
    _hasNextPrevButtons = hasNextPrevButtons;
    setLayout(new BorderLayout());
    
    _lastSelectedRegion = null;
    
    _frame = frame;
    _model = frame.getModel();
    
    removeAll(); 
    
    _changeState = DEFAULT_STATE;
    
    
    _closePanel = new JPanel(new BorderLayout());
    _closePanel.add(_closeButton, BorderLayout.NORTH);
    
    _leftPane = new JPanel(new BorderLayout());
    _setupRegionTree();
    
    this.add(_leftPane, BorderLayout.CENTER);
    
    _buttonPanel = new JPanel(new BorderLayout());


    
    _setupButtonPanel();
    this.add(_buttonPanel, BorderLayout.EAST);
    updateButtons();
    
    
    _setColors(_regTree);
  }
  
  
  private static void _setColors(Component c) {
    new ForegroundColorListener(c);
    new BackgroundColorListener(c);
  }
  
  
  @Override
  protected void _close() {

    super._close();
    updateButtons();
  }
  





  


  
  
  public boolean requestFocusInWindow() {
    assert EventQueue.isDispatchThread();
    updatePanel();  
    return super.requestFocusInWindow();
  }




















  
  













  
  
  protected void updatePanel() {
    





    _regTreeModel.reload();

    expandTree();
    repaint();
  }
  
  
  protected boolean _requestFocusInWindow() {
    updatePanel();
    updateButtons();
    return super.requestFocusInWindow();
  }
  
  
  private void _setupRegionTree() {
    _rootNode = new DefaultMutableTreeNode(_title);
    _regTreeModel = new DefaultTreeModel(_rootNode);
    _regTree = new RegionTree(_regTreeModel);
    _regTree.setEditable(false);
    _regTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    _regTree.setShowsRootHandles(true);
    _regTree.setRootVisible(false);
    _regTree.putClientProperty("JTree.lineStyle", "Angled");
    _regTree.setScrollsOnExpand(true);
    _regTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) { updateButtons(); }
    });
    _regTree.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) { performDefaultAction(); } } 
    });
    
    
    dtcr = new RegionRenderer();
    dtcr.setOpaque(false);

    _regTree.setCellRenderer(dtcr);
    
    _leftPane.add(new JScrollPane(_regTree));
    
    _initPopup();
    
    ToolTipManager.sharedInstance().registerComponent(_regTree);
  }
  
  
  protected void updateButtons() { _updateButtons(); }
  
  protected void _updateButtons() { }
  
  
  public void expandAll() {
    TreeNode root = (TreeNode)_regTree.getModel().getRoot();
    
    
    expandRecursive(_regTree, new TreePath(root), true);
  }

  
  public void collapseAll() {
    TreeNode root = (TreeNode)_regTree.getModel().getRoot();
    
    
    expandRecursive(_regTree, new TreePath(root), false);
  }
  
  
  protected void _remove() {   
    int[] rows = _regTree.getSelectionRows();

    int len = rows.length;
    int row = (len > 0) ? rows[0] : 0;
    _frame.removeCurrentLocationHighlight();
    for (R r: getSelectedRegions()) {
      _regionManager.removeRegion(r); 
    }
    int rowCount = _regTree.getRowCount();
    if (rowCount == 0) return; 
    

    if (row >= rowCount) row = Math.max(0, rowCount - 1);  
    _requestFocusInWindow();
    _regTree.scrollRowToVisible(row);
    
    
    _regTree.setSelectionRow(row);

    
    if (_regTree.getLeadSelectionPath().getPathCount() < 2) _regTree.setSelectionRow(row + 1);

  }
  
  private void expandRecursive(JTree tree, TreePath parent, boolean expand) {
    
    TreeNode node = (TreeNode)parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration<?> e=node.children(); e.hasMoreElements(); ) {
        TreeNode n = (TreeNode)e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        expandRecursive(tree, path, expand);
      }
    }
    
    
    if (expand) {
      tree.expandPath(parent);
    } else {
      tree.collapsePath(parent);
    }
  }
  
  
  class RegionRenderer extends DefaultTreeCellRenderer {
    
    










    
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, 
                                                  boolean leaf, int row, boolean hasFocus) {
       super.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, leaf, row, hasFocus);

      

      

      
      
      Thunk<String> tooltip = null;
      if (DrJava.getConfig().getSetting(OptionConstants.SHOW_CODE_PREVIEW_POPUPS).booleanValue()) {
        if (leaf) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
          final Object o = node.getUserObject();
          
          if (o instanceof RegionTreeUserObj) {
            tooltip = new Thunk<String>() {
              public String value() {
                @SuppressWarnings("unchecked")
                RegionTreeUserObj<R> userObject = (RegionTreeUserObj<R>) o;
                R r = userObject.region();
            
                OpenDefinitionsDocument doc = r.getDocument();
                try {
                  int lnr = doc.getLineOfOffset(r.getStartOffset()) + 1;
                  int startOffset = doc._getOffset(lnr - 3);
                  if (startOffset < 0) { startOffset = 0; }
                  int endOffset = doc._getOffset(lnr + 3);
                  if (endOffset < 0) { endOffset = doc.getLength() - 1; }
                  
                  
                  String s = doc.getText(startOffset, endOffset - startOffset);
                  
                  
                  int rStart = r.getStartOffset() - startOffset;
                  if (rStart < 0) { rStart = 0; }
                  int rEnd = r.getEndOffset() - startOffset;
                  if (rEnd > s.length()) { rEnd = s.length(); }
                  if ((rStart <= s.length()) && (rEnd >= rStart)) {
                    String t1 = StringOps.encodeHTML(s.substring(0, rStart));
                    String t2 = StringOps.encodeHTML(s.substring(rStart,rEnd));
                    String t3 = StringOps.encodeHTML(s.substring(rEnd));
                    s = t1 + "<font color=#ff0000>" + t2 + "</font>" + t3;
                  }
                  else {
                    s = StringOps.encodeHTML(s);
                  }
                  return "<html><pre>" + s + "</pre></html>";
                }
                catch(javax.swing.text.BadLocationException ble) { return "";  }
              }
            };
            setText(node.getUserObject().toString());
            setIcon(null);

          }
        }
      }
      setToolTipText(tooltip);
      return  this;
    }
    
    
    public void setToolTipText(Thunk<String> text) {
      Object oldText = getClientProperty(TOOL_TIP_TEXT_KEY);
      putClientProperty(TOOL_TIP_TEXT_KEY, text);
      ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
      if (text != null) {
        if (oldText == null) {
          toolTipManager.registerComponent(this);
        }
      } else {
        toolTipManager.unregisterComponent(this);
      }
    }
    
    
    @SuppressWarnings("unchecked")
    public String getToolTipText() {
      Object o = getClientProperty(TOOL_TIP_TEXT_KEY);
      if (o instanceof Thunk) {
        String s = ((Thunk<String>)o).value();
        putClientProperty(TOOL_TIP_TEXT_KEY, s);
        return s;
      }
      return (String)o;
    }
  }
  
  
  protected void performDefaultAction() { }
  
  
  protected JComponent[] makeButtons() {  return new JComponent[0];  }
  
  
  private void _setupButtonPanel() {
    JPanel mainButtons = new JPanel();
    JPanel emptyPanel = new JPanel();
    JPanel closeButtonPanel = new JPanel(new BorderLayout());
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    mainButtons.setLayout(gbLayout);
    
    JComponent[] buts = makeButtons();
    
    closeButtonPanel.add(_closeButton, BorderLayout.NORTH);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    
    if (_hasNextPrevButtons) {
      _prevButton = new JButton(new AbstractAction("Previous") {
        public void actionPerformed(ActionEvent ae) {
          goToPreviousRegion();
        }
      });
      _nextButton = new JButton(new AbstractAction("Next") {
        public void actionPerformed(ActionEvent ae) {
          goToNextRegion();
        }
      });
      mainButtons.add(_prevButton);
      gbLayout.setConstraints(_prevButton, c);
      mainButtons.add(_nextButton);
      gbLayout.setConstraints(_nextButton, c);
      updateNextPreviousRegionButtons(null);
    }
    for (JComponent b: buts) { mainButtons.add(b); }
    mainButtons.add(emptyPanel);
    
    for (JComponent b: buts) { gbLayout.setConstraints(b, c); }
    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.SOUTH;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.weighty = 1.0;
    
    gbLayout.setConstraints(emptyPanel, c);
    
    _buttonPanel.add(mainButtons, BorderLayout.CENTER);
    _buttonPanel.add(closeButtonPanel, BorderLayout.EAST);
  }
  
  
  protected AbstractAction[] makePopupMenuActions() { return null; }
  
  
  private void _initPopup() {
    _regionPopupMenu = new JPopupMenu(_title);
    AbstractAction[] acts = makePopupMenuActions();
    if (acts != null) {
      for (AbstractAction a: acts) {
        _regionPopupMenu.add(a);
      }
      _regTree.addMouseListener(new RegionMouseAdapter());
    }
  }
  
  
  DefaultMutableTreeNode getNode(OpenDefinitionsDocument doc) { return _docToTreeNode.get(doc); }
  
    
  DefaultMutableTreeNode getNode(R region) { return _regionToTreeNode.get(region); }
  
  
  protected ArrayList<R> getSelectedRegions() {
    ArrayList<R> regs = new ArrayList<R>();
    TreePath[] paths = _regTree.getSelectionPaths();  
    if (paths != null) {
      for (TreePath path: paths) {
        if (path != null && path.getPathCount() == 3) {
          DefaultMutableTreeNode lineNode = (DefaultMutableTreeNode)path.getLastPathComponent();
          @SuppressWarnings("unchecked") 
          R r = ((RegionTreeUserObj<R>) lineNode.getUserObject()).region();
          regs.add(r);
        }
      }
    }
    return regs;
  }
  
  
  protected void goToRegion() {
    ArrayList<R> r = getSelectedRegions();
    if (r.size() == 1) {
      updateNextPreviousRegionButtons(r.get(0));
      _frame.scrollToDocumentAndOffset(_lastSelectedRegion.getDocument(), _lastSelectedRegion.getStartOffset(), false);
    }
  }
  
  
  protected void updateNextPreviousRegionButtons() {
    updateNextPreviousRegionButtons(_lastSelectedRegion);
  }
  
  
  protected void updateNextPreviousRegionButtons(R lastSelectedRegion) {
    _lastSelectedRegion = lastSelectedRegion;
    if (_hasNextPrevButtons) {
      int count = _regionManager.getRegionCount();
      if (count>0) {
        if (_lastSelectedRegion==null) {
          
          _prevButton.setEnabled(false); 
          _nextButton.setEnabled(true); 
        }
        else {
          
          _prevButton.setEnabled(getPrevRegionInTree(_lastSelectedRegion)!=null);
          _nextButton.setEnabled(getNextRegionInTree(_lastSelectedRegion)!=null);
        }
      }
    }
  }

  
  public void goToPreviousRegion() {
    assert EventQueue.isDispatchThread();
    
    int count = _regionManager.getRegionCount();
    if (count>0) {
      R newRegion = null; 
      if (_lastSelectedRegion!=null) {
        
        newRegion = getPrevRegionInTree(_lastSelectedRegion);
      }
      else {
        
        newRegion = _regionManager.getRegions().get(0);
      }
      if (newRegion!=null) {
        
        updateNextPreviousRegionButtons(newRegion);
        selectRegion(_lastSelectedRegion);
        _frame.scrollToDocumentAndOffset(_lastSelectedRegion.getDocument(),
                                         _lastSelectedRegion.getStartOffset(), false);
      }
    }
  }
  
  
  protected R getPrevRegionInTree(R r) {
    DefaultMutableTreeNode regionNode = _regionToTreeNode.get(r);
    if (regionNode != null) {
      DefaultMutableTreeNode prevSibling = regionNode.getPreviousSibling();
      if (prevSibling!=null) {
        
        
        
        
        
        
        
        @SuppressWarnings("unchecked")
        RegionTreeUserObj<R> userObject = (RegionTreeUserObj<R>) prevSibling.getUserObject();
        return userObject.region();
      }
      else {
        
        
        
        
        
        
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)regionNode.getParent();
        if (parent!=null) {
          DefaultMutableTreeNode parentsPrevSibling = parent.getPreviousSibling();
          if (parentsPrevSibling!=null) {
            try {
              DefaultMutableTreeNode olderCousin = (DefaultMutableTreeNode)parentsPrevSibling.getLastChild();
              if (olderCousin!=null) {
                @SuppressWarnings("unchecked")
                RegionTreeUserObj<R> userObject = (RegionTreeUserObj<R>) olderCousin.getUserObject();
                return userObject.region();
              }
            }
            catch(NoSuchElementException nsee) {
              throw new UnexpectedException(nsee, "Document node without children, shouldn't exist");
            }
          }
        }
      }
    }
    return null;
  }

  
  public void goToNextRegion() {
    int count = _regionManager.getRegionCount();
    if (count>0) {
      R newRegion = null; 
      if (_lastSelectedRegion!=null) {
        
        newRegion = getNextRegionInTree(_lastSelectedRegion);
      }
      else {
        
        newRegion = _regionManager.getRegions().get(0);
      }
      if (newRegion!=null) {
        
        updateNextPreviousRegionButtons(newRegion);
        selectRegion(_lastSelectedRegion);
        _frame.scrollToDocumentAndOffset(_lastSelectedRegion.getDocument(),
                                         _lastSelectedRegion.getStartOffset(), false);
      }
    }
  }
  
  
  protected R getNextRegionInTree(R r) {
    DefaultMutableTreeNode regionNode = _regionToTreeNode.get(r);
    if (regionNode != null) {
      DefaultMutableTreeNode nextSibling = regionNode.getNextSibling();
      if (nextSibling!=null) {
        
        
        
        
        
        
        
        @SuppressWarnings("unchecked")
        RegionTreeUserObj<R> userObject = (RegionTreeUserObj<R>) nextSibling.getUserObject();
        return userObject.region();
      }
      else {
        
        
        
        
        
        
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)regionNode.getParent();
        if (parent!=null) {
          DefaultMutableTreeNode parentsNextSibling = parent.getNextSibling();
          if (parentsNextSibling!=null) {
            try {
              DefaultMutableTreeNode youngerCousin = (DefaultMutableTreeNode)parentsNextSibling.getFirstChild();
              if (youngerCousin!=null) {
                @SuppressWarnings("unchecked")
                RegionTreeUserObj<R> userObject = (RegionTreeUserObj<R>) youngerCousin.getUserObject();
                return userObject.region();
              }
            }
            catch(NoSuchElementException nsee) {
              throw new UnexpectedException(nsee, "Document node without children, shouldn't exist");
            }
          }
        }
      }
    }
    return null;
  }
  
  
  public void addRegion(final R r) {
    try {

      DefaultMutableTreeNode docNode;
      OpenDefinitionsDocument doc = r.getDocument();
      


      docNode = _docToTreeNode.get(doc);
      if (docNode == null) {
        
        docNode = new DefaultMutableTreeNode(doc.getRawFile());
        _regTreeModel.insertNodeInto(docNode, _rootNode, _rootNode.getChildCount());
        
        _docToTreeNode.put(doc, docNode);




      }

      






      @SuppressWarnings("unchecked")
      Enumeration<DefaultMutableTreeNode> regionNodes = docNode.children();
      
      
      int startOffset = r.getStartOffset();
      for (int index = 0; true ; index++) {  
        
        if (! regionNodes.hasMoreElements()) { 

          insertNewRegionNode(r, docNode, index);




          break;
        }
        DefaultMutableTreeNode node = regionNodes.nextElement();
        
        @SuppressWarnings("unchecked")
        RegionTreeUserObj<R> userObject = (RegionTreeUserObj<R>) node.getUserObject();
        R nodeRegion = userObject.region();
        int nodeOffset = nodeRegion.getStartOffset();
        














        if (nodeOffset >= startOffset) {
          insertNewRegionNode(r, docNode, index);

          break;
        }
      }

      _changeState.updateButtons();
    }
    catch(Exception e) { DrJavaErrorHandler.record(e); throw new UnexpectedException(e); }
  }

  private void insertNewRegionNode(R r, DefaultMutableTreeNode docNode, int pos) {

    DefaultMutableTreeNode newRegionNode = new DefaultMutableTreeNode(makeRegionTreeUserObj(r));
    
    _regTreeModel.insertNodeInto(newRegionNode, docNode, pos);
    
    
    _regionToTreeNode.put(r, newRegionNode);
    
    
    _changeState.scrollPathToVisible(new TreePath(newRegionNode.getPath()));
    _changeState.setLastAdded(newRegionNode);
  }       
  
  
  public void expandTree() {
    int ct = _regTree.getRowCount();
    for (int i = ct - 1; i >= 0; i--) _regTree.expandRow(i);
  }
    
  
  public void removeRegion(final R r) {

    assert EventQueue.isDispatchThread();
    _changeState.setLastAdded(null);
    
    if ((_lastSelectedRegion!=null) && (_lastSelectedRegion.equals(r))) {
      
      R newLast = getPrevRegionInTree(_lastSelectedRegion);
      if (newLast==null) newLast = getNextRegionInTree(_lastSelectedRegion);
      _lastSelectedRegion = newLast;
      if (_lastSelectedRegion!=null) {
        selectRegion(_lastSelectedRegion);
      }
    }
    
    DefaultMutableTreeNode regionNode = _regionToTreeNode.get(r);

    if (regionNode != null) {


      _regionToTreeNode.remove(r);
      

      DefaultMutableTreeNode parent = (DefaultMutableTreeNode) regionNode.getParent();  
      _regTreeModel.removeNodeFromParent(regionNode);

      
      if (parent.getChildCount() == 0) {
        
        OpenDefinitionsDocument doc = r.getDocument();  
        _docToTreeNode.remove(doc);
        _regTreeModel.removeNodeFromParent(parent);

      }
    }

    _changeState.updateButtons();

    closeIfEmpty();
  }
  
  
  protected void selectRegion(final R r) {
    assert EventQueue.isDispatchThread();
    DefaultMutableTreeNode regionNode = _regionToTreeNode.get(r);
    if (regionNode != null) {
      _regTree.setSelectionPath(new TreePath(regionNode.getPath()));
    }
  }
  
  
  protected void closeIfEmpty() {
    if (_regionManager.getDocuments().isEmpty()) _close(); 
  }
  
  
  public void reload(R startRegion, R endRegion) {
    SortedSet<R> tail = _regionManager.getTailSet(startRegion);
    Iterator<R> iterator = tail.iterator();
    
    while (iterator.hasNext()) {
      R r = iterator.next();
      if (r.compareTo(endRegion) > 0) break; 

      _regTreeModel.reload(getNode(r));
    }
  }
  


















  
  
  protected class RegionMouseAdapter extends RightClickMouseAdapter {
    protected void _popupAction(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      TreePath path = _regTree.getPathForLocation(x, y);
      if (path != null && path.getPathCount() == 3) {
        _regTree.setSelectionRow(_regTree.getRowForLocation(x, y));
        _regionPopupMenu.show(e.getComponent(), x, y);
      }
    }
    
    public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
        performDefaultAction();
      }
    }
  }
  
  
  protected RegionTreeUserObj<R> makeRegionTreeUserObj(R r) { return new RegionTreeUserObj<R>(r); }
  
  protected class RegionTree extends JTree {
    
    public RegionTree(DefaultTreeModel s) { super(s); }  
    
    public void setForeground(Color c) {
      super.setForeground(c);
      if (dtcr != null) dtcr.setTextNonSelectionColor(c);
    }
    
    public void setBackground(Color c) {
      super.setBackground(c);
      if (RegionsTreePanel.this != null && dtcr != null) dtcr.setBackgroundNonSelectionColor(c);
    }
  }
  
  
  protected static class RegionTreeUserObj<R extends OrderedDocumentRegion> {
    protected R _region;
    public int lineNumber() { return _region.getDocument().getLineOfOffset(_region.getStartOffset()) + 1; }
    public R region() { return _region; }
    public RegionTreeUserObj(R r) { _region = r; }

    
    public String toString() {
      final StringBuilder sb = new StringBuilder(120);
      sb.append("<html>");
      sb.append(lineNumber());
      sb.append(": ");
      String text = _region.getString(); 
      int len = text.length();
      if (text.lastIndexOf('\n') != len - 1) sb.append(StringOps.flatten(text));  
      else sb.append(text);  
      sb.append("</html>");

      return sb.toString();
    }
  }
  
  
  protected interface IChangeState {
    public void scrollPathToVisible(TreePath tp);
    public void updateButtons();
    public void setLastAdded(DefaultMutableTreeNode node);
    public void switchStateTo(IChangeState newState);
  }
  
  
  protected class DefaultState implements IChangeState {
    public void scrollPathToVisible(TreePath tp) {
      _regTree.scrollPathToVisible(tp);
    }
    public void updateButtons() {
      RegionsTreePanel.this.updateButtons();
      RegionsTreePanel.this.updateNextPreviousRegionButtons();
    }
    public void setLastAdded(DefaultMutableTreeNode node) { }
    public void switchStateTo(IChangeState newState) {
      _changeState = newState;
    }
    protected DefaultState() { }
  }
}
