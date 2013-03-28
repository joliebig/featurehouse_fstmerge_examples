

package edu.rice.cs.drjava.ui;

import java.util.Vector;

import java.util.Enumeration;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.debug.*;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.UnexpectedException;


public class BreakpointsPanel extends TabbedPanel {
  private JPanel _leftPane;
  
  private DefaultMutableTreeNode _breakpointRootNode;
  private DefaultTreeModel _bpTreeModel;
  private JTree _bpTree;
  
  private JPopupMenu _breakpointPopupMenu;
  
  private final SingleDisplayModel _model;
  private final MainFrame _frame;
  private final Debugger _debugger;
  private DefinitionsPane _defPane = null;
  
  private JPanel _buttonPanel;
  private JButton _goToButton;
  private JButton _enableDisableButton;
  private JButton _removeButton;
  private JButton _removeAllButton;
  
  private DefaultTreeCellRenderer dtcr;
  
  
  public BreakpointsPanel(MainFrame frame) {
    super(frame, "Breakpoints");
    
    this.setLayout(new BorderLayout());
    
    _frame = frame;
    _model = frame.getModel();
    _debugger = _model.getDebugger();
    
    
    this.removeAll(); 

    
    _closePanel = new JPanel(new BorderLayout());
    _closePanel.add(_closeButton, BorderLayout.NORTH);
    
    _leftPane = new JPanel(new BorderLayout());
    _setupBreakpointTree();
    
    this.add(_leftPane, BorderLayout.CENTER);
    
    _buttonPanel = new JPanel(new BorderLayout());
    _setupButtonPanel();
    this.add(_buttonPanel, BorderLayout.EAST);
    
    _debugger.addListener(new BreakpointsPanelListener());
    
    
    _setColors(_bpTree);
  }
  
  
  private static void _setColors(Component c) {
    new ForegroundColorListener(c);
    new BackgroundColorListener(c);
  }
  
  protected void _close() {
    _defPane.requestFocusInWindow();
    if (_displayed) stopListening();
    super._close();
  }
  
  
  void beginListeningTo(DefinitionsPane defPane) {
    if (_defPane==null) {
      _displayed = true;
      _defPane = defPane;      
      _updateButtons();
    }
    else
      throw new UnexpectedException(new RuntimeException("BreakpointsPanel should not be listening to anything"));
  }

  
  public void stopListening() {
    if (_defPane != null) {
      _defPane = null;
      _displayed = false;
    } 
  }
  
  
  private void _setupBreakpointTree() {
    _breakpointRootNode = new DefaultMutableTreeNode("Breakpoints");
    _bpTreeModel = new DefaultTreeModel(_breakpointRootNode);
    _bpTree = new BPTree(_bpTreeModel);
    _bpTree.setEditable(false);
    _bpTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    _bpTree.setShowsRootHandles(true);
    _bpTree.setRootVisible(false);
    _bpTree.putClientProperty("JTree.lineStyle", "Angled");
    _bpTree.setScrollsOnExpand(true);
    _bpTree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        _updateButtons();
      }
    });
    
    dtcr = new BreakPointRenderer();
    dtcr.setOpaque(false);
    _setColors(dtcr);
    _bpTree.setCellRenderer(dtcr);
    
    _leftPane.add(new JScrollPane(_bpTree));
    
    _initPopup();
  }
  
  
  private void _updateButtons() {
    try {
      Breakpoint bp = _getSelectedBreakpoint();
      boolean enable = (bp != null);
      _goToButton.setEnabled(enable);
      _enableDisableButton.setEnabled(enable);
      _removeButton.setEnabled(enable);
      if (enable) {
        if (bp.isEnabled()) {
          _enableDisableButton.setText("Disable");
        }
        else {
          _enableDisableButton.setText("Enable");
        }
      }
    }
    catch (DebugException de) {
      _goToButton.setEnabled(false);
      _enableDisableButton.setEnabled(false);
      _removeButton.setEnabled(false);
    }
    _removeAllButton.setEnabled((_breakpointRootNode!=null) && (_breakpointRootNode.getDepth()>0));
  }
  
  
  static class BreakPointRenderer extends DefaultTreeCellRenderer {
    
    public void setBackground(Color c) {
      this.setBackgroundNonSelectionColor(c);
    }
    
    public void setForeground(Color c) {
      this.setTextNonSelectionColor(c);
    }
    
    private BreakPointRenderer() {
      this.setTextSelectionColor(Color.black);
      setLeafIcon(null);
      setOpenIcon(null);
      setClosedIcon(null);
    }
    
    
    public Component getTreeCellRendererComponent
      (JTree tree, Object value, boolean selected, boolean expanded,
       boolean leaf, int row, boolean hasFocus) {
      Component renderer = super.getTreeCellRendererComponent
        (tree, value, selected, expanded, leaf, row, hasFocus);
      
      if (renderer instanceof JComponent) {
        ((JComponent) renderer).setOpaque(false);
      }
      
      _setColors(renderer);
      return renderer;
    }
  }
  
  
  private void _setupButtonPanel() {
    JPanel mainButtons = new JPanel();
    JPanel closeButtonPanel = new JPanel(new BorderLayout());
    mainButtons.setLayout( new GridLayout(0,1));
    
    Action enableDisableAction = new AbstractAction("Disable") {
      public void actionPerformed(ActionEvent ae) {
        _enableDisableBreakpoint();
      }
    };
    _enableDisableButton = new JButton(enableDisableAction);
    
    Action goToAction = new AbstractAction("Go to") {
      public void actionPerformed(ActionEvent ae) {
        _goToBreakpoint();
      }
    };
    _goToButton = new JButton(goToAction);
    
    Action removeAction = new AbstractAction("Remove") {
      public void actionPerformed(ActionEvent ae) {
        _removeBreakpoint();
      }
    };
    _removeButton = new JButton(removeAction);
    
    Action removeAllAction = new AbstractAction("Remove All") {
      public void actionPerformed(ActionEvent ae) {
        try {
          _debugger.removeAllBreakpoints();
        }
        catch (DebugException de) {
          _frame._showDebugError(de);
        }
      }
    };
    _removeAllButton = new JButton(removeAllAction);
    
    closeButtonPanel.add(_closeButton, BorderLayout.NORTH);
    mainButtons.add(_goToButton);
    mainButtons.add(_enableDisableButton);
    mainButtons.add(_removeButton);
    mainButtons.add(_removeAllButton);
    _buttonPanel.add(mainButtons, BorderLayout.CENTER);
    _buttonPanel.add(closeButtonPanel, BorderLayout.EAST);
  }
  
  
  private void _initPopup() {
    _breakpointPopupMenu = new JPopupMenu("Breakpoint");
    _breakpointPopupMenu.add(new AbstractAction("Go to Breakpoint") {
      public void actionPerformed(ActionEvent e) {
        _goToBreakpoint();
      }
    });
    _breakpointPopupMenu.add(new AbstractAction("Remove Breakpoint") {
      public void actionPerformed(ActionEvent e) {
        _removeBreakpoint();
      }
    });
    _bpTree.addMouseListener(new BreakpointMouseAdapter());
  }
  
  
  private Breakpoint _getSelectedBreakpoint() throws DebugException {
    TreePath path = _bpTree.getSelectionPath();
    if (path == null || path.getPathCount() != 3) {
      return null;
    }
    else {
      DefaultMutableTreeNode lineNode =
        (DefaultMutableTreeNode)path.getLastPathComponent();
      int line = ((BPTreeUserObj) lineNode.getUserObject()).lineNumber();
      DefaultMutableTreeNode classNameNode =
        (DefaultMutableTreeNode) path.getPathComponent(1);
      String className = (String) classNameNode.getUserObject();
      return _debugger.getBreakpoint(line, className);
    }
  }
  
  
  private void _goToBreakpoint() {
    try {
      Breakpoint bp = _getSelectedBreakpoint();
      if (bp != null) {
        _debugger.scrollToSource(bp);
      }
    }
    catch (DebugException de) {
      _frame._showDebugError(de);
    }
  }
  
  
  private void _enableDisableBreakpoint() {
    try {
      Breakpoint bp = _getSelectedBreakpoint();
      if (bp != null) {
        bp.setEnabled(!bp.isEnabled());
        _updateButtons();
      }
    }
    catch (DebugException de) {
      _frame._showDebugError(de);
    }
  }
  
  
  private void _removeBreakpoint() {
    try {
      Breakpoint bp = _getSelectedBreakpoint();
      if (bp != null) _debugger.removeBreakpoint(bp);
    }
    catch (DebugException de) { _frame._showDebugError(de); }
  }
  
  
  class BreakpointsPanelListener implements DebugListener {
    
    
    public void debuggerStarted() { }
    
    
    public void debuggerShutdown() { }
    
    
    public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight) { }
    
    
    public void breakpointSet(final Breakpoint bp) {



      DefaultMutableTreeNode bpDocNode = new DefaultMutableTreeNode(bp.getClassName());
      
      
      
      Enumeration documents = _breakpointRootNode.children();
      while (documents.hasMoreElements()) {
        DefaultMutableTreeNode doc = (DefaultMutableTreeNode)documents.nextElement();
        if (doc.getUserObject().equals(bpDocNode.getUserObject())) {
          
          
          
          
          Enumeration lineNumbers = doc.children();
          while (lineNumbers.hasMoreElements()) {
            DefaultMutableTreeNode lineNumber = (DefaultMutableTreeNode)lineNumbers.nextElement();
            
            
            if (((BPTreeUserObj)lineNumber.getUserObject()).lineNumber() > bp.getLineNumber()) {
              
              
              DefaultMutableTreeNode newBreakpoint =
                new DefaultMutableTreeNode(new BPTreeUserObj(bp.getLineNumber(), bp.isEnabled()));
              _bpTreeModel.insertNodeInto(newBreakpoint, doc, doc.getIndex(lineNumber));
              
              
              _bpTree.scrollPathToVisible(new TreePath(newBreakpoint.getPath()));
              return;
            }
          }
          
          DefaultMutableTreeNode newBreakpoint =
            new DefaultMutableTreeNode(new BPTreeUserObj(bp.getLineNumber(), bp.isEnabled()));
          _bpTreeModel.insertNodeInto(newBreakpoint, doc, doc.getChildCount());
          
          
          _bpTree.scrollPathToVisible(new TreePath(newBreakpoint.getPath()));
          return;
        }
      }
      
      _bpTreeModel.insertNodeInto(bpDocNode, _breakpointRootNode, _breakpointRootNode.getChildCount());
      DefaultMutableTreeNode newBreakpoint =
        new DefaultMutableTreeNode(new BPTreeUserObj(bp.getLineNumber(), bp.isEnabled()));
      _bpTreeModel.insertNodeInto(newBreakpoint, bpDocNode, bpDocNode.getChildCount());
      
      
      TreePath pathToNewBreakpoint = new TreePath(newBreakpoint.getPath());
      _bpTree.scrollPathToVisible(pathToNewBreakpoint);



      
      _updateButtons();
    }
    
    
    public void breakpointReached(final Breakpoint bp) {
      
      Runnable doCommand = new Runnable() {
        public void run() {
          DefaultMutableTreeNode bpDoc = new DefaultMutableTreeNode(bp.getClassName());
          
          
          Enumeration documents = _breakpointRootNode.children();
          while (documents.hasMoreElements()) {
            DefaultMutableTreeNode doc = (DefaultMutableTreeNode)documents.nextElement();
            if (doc.getUserObject().equals(bpDoc.getUserObject())) {
              
              Enumeration lineNumbers = doc.children();
              while (lineNumbers.hasMoreElements()) {
                DefaultMutableTreeNode lineNumber =
                  (DefaultMutableTreeNode)lineNumbers.nextElement();
                if (lineNumber.getUserObject().equals(new Integer(bp.getLineNumber()))) {
                  
                  
                  TreePath pathToNewBreakpoint = new TreePath(lineNumber.getPath());
                  _bpTree.scrollPathToVisible(pathToNewBreakpoint);
                  _bpTree.setSelectionPath(pathToNewBreakpoint);
                }
              }
            }
          }
        }
      };
      Utilities.invokeLater(doCommand);
    }
    
    
    public void breakpointChanged(final Breakpoint bp) {
      
      Runnable doCommand = new Runnable() {
        public void run() {
          DefaultMutableTreeNode bpDocNode = new DefaultMutableTreeNode(bp.getClassName());
          
          
          Enumeration documents = _breakpointRootNode.children();
          boolean found = false;
          while ((!found) && (documents.hasMoreElements())) {
            DefaultMutableTreeNode doc = (DefaultMutableTreeNode)documents.nextElement();
            if (doc.getUserObject().equals(bpDocNode.getUserObject())) {
              
              Enumeration lineNumbers = doc.children();
              while (lineNumbers.hasMoreElements()) {
                DefaultMutableTreeNode lineNumber =
                  (DefaultMutableTreeNode)lineNumbers.nextElement();
                BPTreeUserObj uo = (BPTreeUserObj)lineNumber.getUserObject();
                if (uo.lineNumber()==bp.getLineNumber()) {
                  uo.setEnabled(bp.isEnabled());
                  ((DefaultTreeModel)_bpTree.getModel()).nodeChanged(lineNumber);
                  found = true;
                  break;
                }
              }
            }
          }
          _updateButtons();
        }
      };
      Utilities.invokeLater(doCommand);
    }
    
    
    public void breakpointRemoved(final Breakpoint bp) {
      
      Runnable doCommand = new Runnable() {
        public void run() {
          DefaultMutableTreeNode bpDocNode = new DefaultMutableTreeNode(bp.getClassName());
          
          
          Enumeration documents = _breakpointRootNode.children();
          boolean found = false;
          while ((!found) && (documents.hasMoreElements())) {
            DefaultMutableTreeNode doc = (DefaultMutableTreeNode)documents.nextElement();
            if (doc.getUserObject().equals(bpDocNode.getUserObject())) {
              
              Enumeration lineNumbers = doc.children();
              while (lineNumbers.hasMoreElements()) {
                DefaultMutableTreeNode lineNumber =
                  (DefaultMutableTreeNode)lineNumbers.nextElement();
                if (((BPTreeUserObj)lineNumber.getUserObject()).lineNumber()==bp.getLineNumber()) {
                  _bpTreeModel.removeNodeFromParent(lineNumber);
                  if (doc.getChildCount() == 0) {
                    
                    _bpTreeModel.removeNodeFromParent(doc);
                  }
                  found = true;
                  break;
                }
              }
            }
          }
          _updateButtons();
        }
      };
      Utilities.invokeLater(doCommand);
    }
    
    public void watchSet(final DebugWatchData w) { }
    public void watchRemoved(final DebugWatchData w) { }
    
    
    public void stepRequested() { }
    
    
    public void currThreadSuspended() { }
    
    
    public void currThreadResumed() { }
    
    
    public void threadStarted() { }
    
    
    public void currThreadDied() { }
    
    
    public void nonCurrThreadDied() {  }
    
    
    public void currThreadSet(DebugThreadData thread) { }
  }
  
  
  private class BreakpointMouseAdapter extends RightClickMouseAdapter {
    protected void _popupAction(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      TreePath path = _bpTree.getPathForLocation(x, y);
      if (path != null && path.getPathCount() == 3) {
        _bpTree.setSelectionRow(_bpTree.getRowForLocation(x, y));
        _breakpointPopupMenu.show(e.getComponent(), x, y);
      }
    }
    
    public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
        _goToBreakpoint();
      }
    }
  }
  
  private class BPTree extends JTree {
    public BPTree(DefaultTreeModel s) {
      super(s);
    }
    
    public void setForeground(Color c) {
      super.setForeground(c);
      if (dtcr != null) dtcr.setTextNonSelectionColor(c);
    }
    
    public void setBackground(Color c) {
      super.setBackground(c);
      if (BreakpointsPanel.this != null && dtcr != null) dtcr.setBackgroundNonSelectionColor(c);
    }
  }
  
  private class BPTreeUserObj {
    private int _lineNumber;
    private boolean _enabled;
    public int lineNumber() { return _lineNumber; }
    public boolean isEnabled() { return _enabled; }
    public void setEnabled(boolean e) { _enabled = e; }
    public BPTreeUserObj(int l, boolean e) { _lineNumber = l; _enabled = e; }
    public String toString() { return String.valueOf(_lineNumber) + ((_enabled)?"":" (disabled)"); }
  }
}
