

package edu.rice.cs.drjava.ui;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.IDocumentRegion;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.swing.RightClickMouseAdapter;


public abstract class RegionsListPanel<R extends IDocumentRegion> extends TabbedPanel {
  protected JPanel _leftPane;
  
  protected JList _list;
  protected DefaultListModel _listModel;
  protected String _title;
  
  protected final SingleDisplayModel _model;
  protected final MainFrame _frame;
  
  protected JPanel _buttonPanel;
  
  
  public RegionsListPanel(MainFrame frame, String title) {
    super(frame, title);
    _title = title;
    this.setLayout(new BorderLayout());
    
    _frame = frame;
    _model = frame.getModel();
    
    this.removeAll(); 
    
    
    _closePanel = new JPanel(new BorderLayout());
    _closePanel.add(_closeButton, BorderLayout.NORTH);
    
    _leftPane = new JPanel(new BorderLayout());
    _setupRegionList();
    
    this.add(_leftPane, BorderLayout.CENTER);
    
    _buttonPanel = new JPanel(new BorderLayout());
    _setupButtonPanel();
    this.add(_buttonPanel, BorderLayout.EAST);
    updateButtons();
    
    
    _setColors(_list);
    
    _list.addMouseListener(new RegionMouseAdapter());
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
  
  
  private void _setupRegionList() {
    _listModel = new DefaultListModel();
    _list = new JList(_listModel) {
      public String getToolTipText(MouseEvent evt) {
        
        int index = locationToIndex(evt.getPoint());
        
        @SuppressWarnings("unchecked") RegionListUserObj<R> node = (RegionListUserObj<R>)getModel().getElementAt(index);
        R r = node.region();
        String tooltip = null;
        
        OpenDefinitionsDocument doc = r.getDocument();
        try {
          int lnr = doc.getLineOfOffset(r.getStartOffset())+1;
          int startOffset = doc._getOffset(lnr - 3);
          if (startOffset < 0) { startOffset = 0; }
          int endOffset = doc._getOffset(lnr + 3);
          if (endOffset < 0) { endOffset = doc.getLength()-1; }
          
          
          String s = doc.getText(startOffset, endOffset-startOffset);
          
          
          int rStart = r.getStartOffset() - startOffset;
          if (rStart < 0) { rStart = 0; }
          int rEnd = r.getEndOffset() - startOffset;
          if (rEnd>s.length()) { rEnd = s.length(); }
          if ((rStart <= s.length()) && (rEnd >= rStart)) {
            String t1 = StringOps.encodeHTML(s.substring(0, rStart));
            String t2 = StringOps.encodeHTML(s.substring(rStart, rEnd));
            String t3 = StringOps.encodeHTML(s.substring(rEnd));
            s = t1 + "<font color=#ff0000>" + t2 + "</font>" + t3;
          }
          else {
            s = StringOps.encodeHTML(s);
          }
          tooltip = "<html><pre>" + s + "</pre></html>";
        }
        catch(javax.swing.text.BadLocationException ble) { tooltip = null;  }
        return tooltip;
      }
    };
    _list.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    _list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) { updateButtons(); }
    });            
    _list.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) performDefaultAction(); } 
    });
    _list.setFont(DrJava.getConfig().getSetting(OptionConstants.FONT_DOCLIST));
    
    _leftPane.add(new JScrollPane(_list));
    ToolTipManager.sharedInstance().registerComponent(_list);
  }
  
  
  protected void updateButtons() {
  }
  
  
  protected void performDefaultAction() {
  }
  
  
  protected JComponent[] makeButtons() {        
    return new JComponent[0];    
  }
  
  
  private void _setupButtonPanel() {
    JPanel mainButtons = new JPanel();
    JPanel emptyPanel = new JPanel();
    JPanel closeButtonPanel = new JPanel(new BorderLayout());
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    mainButtons.setLayout(gbLayout);
    
    JComponent[] buts = makeButtons();
    
    closeButtonPanel.add(_closeButton, BorderLayout.NORTH);    
    for (JComponent b: buts) { mainButtons.add(b); }
    mainButtons.add(emptyPanel);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    
    for (JComponent b: buts) { gbLayout.setConstraints(b, c); }
    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.SOUTH;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.weighty = 1.0;
    
    gbLayout.setConstraints(emptyPanel, c);
    
    _buttonPanel.add(mainButtons, BorderLayout.CENTER);
    _buttonPanel.add(closeButtonPanel, BorderLayout.EAST);
  }
  
  
  protected ArrayList<R> getSelectedRegions() {
    ArrayList<R> regs = new ArrayList<R>();
    int[] indices = _list.getSelectedIndices();
    if (indices != null) {
      for (int index: indices) {
        @SuppressWarnings("unchecked") RegionListUserObj<R> userObj = ((RegionListUserObj<R>)_listModel.elementAt(index));
        R r = userObj.region();
        regs.add(r);
      }
    }
    return regs;
  }
  
  
  protected void goToRegion() {
    ArrayList<R> r = getSelectedRegions();
    if (r.size() == 1) {
      RegionListUserObj<R> userObj = getUserObjForRegion(r.get(0));
      if (userObj != null) { _list.ensureIndexIsVisible(_listModel.indexOf(userObj)); }
      _frame.scrollToDocumentAndOffset(r.get(0).getDocument(), r.get(0).getStartOffset(), false);
    }
  }
  
  
  protected RegionListUserObj<R> getUserObjForRegion(R r) {
    for(int i = 0; i < _listModel.size(); ++i) {
      @SuppressWarnings("unchecked") 
      RegionListUserObj<R> userObj = (RegionListUserObj<R>)_listModel.get(i);
      if ((userObj.region().getStartOffset() == r.getStartOffset()) &&
          (userObj.region().getEndOffset() == r.getEndOffset()) &&
          (userObj.region().getDocument().equals(r.getDocument()))) {
        return userObj;
      }
    }
    return null;
  }
  
  
  public void addRegion(final R r, final int index) {
    assert EventQueue.isDispatchThread();
    




        



        
        RegionListUserObj<R> userObj = makeRegionListUserObj(r);
        _listModel.add(index, userObj);
        _list.ensureIndexIsVisible(_listModel.indexOf(userObj));
        
        updateButtons();



  }
  
  
  public void removeRegion(final R r) {
    









    
    for (int i = 0; i < _listModel.size(); ++i) {
      @SuppressWarnings("unchecked") RegionListUserObj<R> userObj = (RegionListUserObj<R>)_listModel.get(i);
      if (userObj.region() == r) {
        _listModel.removeElementAt(i);
        break;
      }
    }
    
    updateButtons();



  }
  






















  
  
  protected RegionListUserObj<R> makeRegionListUserObj(R r) {
    return new RegionListUserObj<R>(r);
  }
  
  
  protected static class RegionListUserObj<R extends IDocumentRegion> {
    protected R _region;
    public int lineNumber() { return _region.getDocument().getLineOfOffset(_region.getStartOffset())+1; }
    public R region() { return _region; }
    public RegionListUserObj(R r) { _region = r; }
    public String toString() {
      final StringBuilder sb = new StringBuilder();
        sb.append(_region.getDocument().toString());
        sb.append(':');
        sb.append(lineNumber());
        try {
          sb.append(": ");
          int length = Math.min(120, _region.getEndOffset() - _region.getStartOffset());
          sb.append(_region.getDocument().getText(_region.getStartOffset(), length).trim());
        } catch(BadLocationException bpe) {  }        
      return sb.toString();
    }








  }
  
  
  
  protected class RegionMouseAdapter extends RightClickMouseAdapter {
    protected void _popupAction(MouseEvent e) {
      
    }
    
    public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
        performDefaultAction();
      }
    }
  }
  
}
