

package edu.rice.cs.drjava.ui;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.ref.WeakReference;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.LayeredHighlighter;
import java.awt.event.*;
import java.awt.*;

import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import edu.rice.cs.drjava.model.MovingDocumentRegion;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.RegionManager;
import edu.rice.cs.drjava.model.RegionManagerListener;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.drjava.config.OptionConstants;


public class FindResultsPanel extends RegionsTreePanel<MovingDocumentRegion> {

  

  protected final String _searchString;
  protected final boolean _searchAll;
  protected final boolean _searchSelectionOnly;
  protected final boolean _matchCase;
  protected final boolean _wholeWord;
  protected final boolean _noComments;
  protected final boolean _noTestCases;
  protected final WeakReference<OpenDefinitionsDocument> _doc;
  protected final FindReplacePanel _findReplace;
  protected final MovingDocumentRegion _region; 
    
  protected JButton _findAgainButton;
  protected JButton _goToButton;
  protected JButton _bookmarkButton;
  protected JButton _removeButton;
  protected JComboBox _colorBox;
  protected int _lastIndex;
  
  
  private LinkedList<Pair<Option<Color>, OptionListener<Color>>> _colorOptionListeners = 
    new LinkedList<Pair<Option<Color>, OptionListener<Color>>>();
  
  
  public FindResultsPanel(MainFrame frame, RegionManager<MovingDocumentRegion> regionManager, MovingDocumentRegion region, String title, 
                          String searchString, boolean searchAll, boolean searchSelectionOnly, boolean matchCase, boolean wholeWord, 
                          boolean noComments, boolean noTestCases, WeakReference<OpenDefinitionsDocument> doc, 
                          FindReplacePanel findReplace) {
    super(frame, title, regionManager);
    

    _region = region;
    _searchString = searchString;
    _searchAll    = searchAll;
    _searchSelectionOnly = searchSelectionOnly;
    _matchCase    = matchCase;
    _wholeWord    = wholeWord;
    _noComments   = noComments;
    _noTestCases  = noTestCases;
    _doc          = doc;
    _findReplace  = findReplace;
    
    
    StringBuilder sb = new StringBuilder();
    sb.append("<html>Find '").append(title);
    if (!title.equals(_searchString)) sb.append("...");
    sb.append("'");
    if (_searchAll) sb.append(" in all files");
    else if (_searchSelectionOnly) sb.append(" only in original selection.");
    sb.append(".");
    if (_matchCase) sb.append("<br>Case must match.");
    if (_wholeWord) sb.append("<br>Whole words only.");
    if (_noComments) sb.append("<br>No comments or strings.");
    if (_noTestCases) sb.append("<br>No test cases.");
    sb.append("</html>");
    _findAgainButton.setToolTipText(sb.toString());

    
    _regionManager.addListener(new RegionManagerListener<MovingDocumentRegion>() {      
      public void regionAdded(MovingDocumentRegion r) { addRegion(r); }
      public void regionChanged(MovingDocumentRegion r) { 
        regionRemoved(r);
        regionAdded(r);
      }
      public void regionRemoved(MovingDocumentRegion r) { removeRegion(r); }
    });
    
    for(int i = 0; i < OptionConstants.FIND_RESULTS_COLORS.length; ++i) {
      final OptionListener<Color> listener = new FindResultsColorOptionListener(i);
      final Pair<Option<Color>, OptionListener<Color>> pair = 
        new Pair<Option<Color>, OptionListener<Color>>(OptionConstants.FIND_RESULTS_COLORS[i], listener);
      _colorOptionListeners.add(pair);
      DrJava.getConfig().addOptionListener(OptionConstants.FIND_RESULTS_COLORS[i], listener);
    }
  }
  
  class ColorComboRenderer extends JPanel implements ListCellRenderer {
    private Color _color = DrJava.getConfig().getSetting(OptionConstants.FIND_RESULTS_COLORS[_colorBox.getSelectedIndex()]);
    private DefaultListCellRenderer _defaultRenderer = new DefaultListCellRenderer();
    private final Dimension _size = new Dimension(0, 20);  
    private final CompoundBorder _compoundBorder = 
      new CompoundBorder(new MatteBorder(2, 10, 2, 10, Color.white), new LineBorder(Color.black));
    
    public ColorComboRenderer() {
      super();
      setBackground(_color);
      setBorder(_compoundBorder);
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int row, boolean sel, boolean hasFocus) {
      JComponent renderer;
      if (value instanceof Color) {
        _color = (Color) value;
        renderer = this;
      }
      else {
        JLabel l = (JLabel) _defaultRenderer.getListCellRendererComponent(list, value, row, sel, hasFocus);
        l.setHorizontalAlignment(JLabel.CENTER);
        renderer = l;
      }
      
      renderer.setPreferredSize(_size);
      return renderer;
    }
    
    public void paint(Graphics g) {
      setBackground(_color);
      setBorder(_compoundBorder);
      super.paint(g);
    }
  }
  
  
  protected JComponent[] makeButtons() {    
    Action findAgainAction = new AbstractAction("Find Again") {
      public void actionPerformed(ActionEvent ae) { _findAgain(); }
    };
    _findAgainButton = new JButton(findAgainAction);

    Action goToAction = new AbstractAction("Go to") {
      public void actionPerformed(ActionEvent ae) { goToRegion(); }
    };
    _goToButton = new JButton(goToAction);
    
    Action bookmarkAction = new AbstractAction("Bookmark") {
      public void actionPerformed(ActionEvent ae) { _bookmark(); }
    };
    _bookmarkButton = new JButton(bookmarkAction);
    
    Action removeAction = new AbstractAction("Remove") {
      public void actionPerformed(ActionEvent ae) { _remove(); }  
    };
    _removeButton = new JButton(removeAction);
    
    
    final JPanel highlightPanel = new JPanel();
    final Color normalColor = highlightPanel.getBackground();
    highlightPanel.add(new JLabel("Highlight:"));
    
    
    int smallestIndex = 0;
    int smallestUsage = DefinitionsPane.FIND_RESULTS_PAINTERS_USAGE[smallestIndex];
    for(_lastIndex = 0; _lastIndex < OptionConstants.FIND_RESULTS_COLORS.length; ++_lastIndex) {
      if (DefinitionsPane.FIND_RESULTS_PAINTERS_USAGE[_lastIndex] < smallestUsage) {
        smallestIndex = _lastIndex;
        smallestUsage = DefinitionsPane.FIND_RESULTS_PAINTERS_USAGE[smallestIndex];
      }
    }
    _lastIndex = smallestIndex;
    ++DefinitionsPane.FIND_RESULTS_PAINTERS_USAGE[_lastIndex];
    _colorBox = new JComboBox();    
    for (int i = 0; i < OptionConstants.FIND_RESULTS_COLORS.length; ++i) {
      _colorBox.addItem(DrJava.getConfig().getSetting(OptionConstants.FIND_RESULTS_COLORS[i]));
    }
    _colorBox.addItem("None");
    _colorBox.setRenderer(new ColorComboRenderer());
    _colorBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (_lastIndex<OptionConstants.FIND_RESULTS_COLORS.length) {
          --DefinitionsPane.FIND_RESULTS_PAINTERS_USAGE[_lastIndex];
        }
        _lastIndex = _colorBox.getSelectedIndex();
        if (_lastIndex<OptionConstants.FIND_RESULTS_COLORS.length) {
          ++DefinitionsPane.FIND_RESULTS_PAINTERS_USAGE[_lastIndex];
          highlightPanel.setBackground(DrJava.getConfig().getSetting(OptionConstants.FIND_RESULTS_COLORS[_lastIndex]));
        }
        else highlightPanel.setBackground(normalColor);
        
        _frame.refreshFindResultsHighlightPainter(FindResultsPanel.this, 
                                                  DefinitionsPane.FIND_RESULTS_PAINTERS[_lastIndex]);
      }
    });
    _colorBox.setMaximumRowCount(OptionConstants.FIND_RESULTS_COLORS.length + 1);
    _colorBox.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuCanceled(PopupMenuEvent e) { _colorBox.revalidate(); }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { _colorBox.revalidate(); }
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) { _colorBox.revalidate(); }
    });
    _colorBox.setSelectedIndex(_lastIndex);
    _frame.refreshFindResultsHighlightPainter(FindResultsPanel.this, 
                                              DefinitionsPane.FIND_RESULTS_PAINTERS[_lastIndex]);
    
    updateButtons();
    return new JComponent[] { _findAgainButton, _goToButton, _bookmarkButton, _removeButton, highlightPanel, _colorBox};
  }
  
  
  public LayeredHighlighter.LayerPainter getSelectedPainter() {
    return DefinitionsPane.FIND_RESULTS_PAINTERS[_lastIndex];
  }
  
  
  private void _findAgain() {
    _updateButtons();   
    OpenDefinitionsDocument odd = null;
    if (_searchAll) odd = _model.getActiveDocument();
    else if (_doc != null) { odd = _doc.get(); }
    if (odd != null) {

      _regionManager.clearRegions();
      assert _rootNode == _regTreeModel.getRoot();
      _rootNode.removeAllChildren();
      _docToTreeNode.clear();
      _regionToTreeNode.clear();
      _regTreeModel.nodeStructureChanged(_rootNode);


      _findReplace.findAll(_searchString, _searchAll, _searchSelectionOnly, _matchCase, _wholeWord, _noComments, _noTestCases, odd, 
                           _regionManager, _region, this);
      _regTree.scrollRowToVisible(0);  
      _requestFocusInWindow();
    }
  }
  
  
  private void _bookmark() {  
    updateButtons();
    RegionManager<MovingDocumentRegion> bm = _model.getBookmarkManager();
    for (MovingDocumentRegion r: getSelectedRegions()) {
      OpenDefinitionsDocument doc = r.getDocument();
      int start = r.getStartOffset();
      int end = r.getEndOffset();
      Collection<MovingDocumentRegion> conflictingRegions = bm.getRegionsOverlapping(doc, start, end);
      for (MovingDocumentRegion cr: conflictingRegions) bm.removeRegion(cr);

      int lineStart = r.getLineStartOffset();
      int lineEnd = r.getLineEndOffset();
      bm.addRegion(new MovingDocumentRegion(doc, start, end, lineStart, lineEnd));
    }
    _frame.showBookmarks();
  }
  
  
  protected void performDefaultAction() { goToRegion(); }
  
  
  protected void _updateButtons() {
    ArrayList<MovingDocumentRegion> regs = getSelectedRegions();
    OpenDefinitionsDocument odd = null;
    if (_doc != null) { odd = _doc.get(); }
    _findAgainButton.setEnabled(odd != null || _searchAll);
    _goToButton.setEnabled(regs.size() == 1);
    _bookmarkButton.setEnabled(regs.size() > 0);
    _removeButton.setEnabled(regs.size() > 0);
  }
  
  
  protected AbstractAction[] makePopupMenuActions() {
    AbstractAction[] acts = new AbstractAction[] {
      new AbstractAction("Go to") { public void actionPerformed(ActionEvent e) { goToRegion(); } },
        new AbstractAction("Bookmark") { public void actionPerformed(ActionEvent e) { _bookmark(); } },
          new AbstractAction("Remove") { public void actionPerformed(ActionEvent e) { _remove(); } }
    };
    return acts;
  }
  
  
  protected void goToRegion() {
    ArrayList<MovingDocumentRegion> r = getSelectedRegions();
    
    if (r.size() == 1) {
      _frame.removeCurrentLocationHighlight();
      _frame.goToRegionAndHighlight(r.get(0));
    }
  }
  
  
  protected void _close() {

    _regionManager.clearRegions();  
    _model.removeFindResultsManager(_regionManager);  
    _frame.removeCurrentLocationHighlight();
    freeResources();
    super._close();  
  }
  
  
  public void freeResources() {
    _docToTreeNode.clear();
    _regionToTreeNode.clear();
    _model.removeFindResultsManager(_regionManager);  
    for (Pair<Option<Color>, OptionListener<Color>> p: _colorOptionListeners) {
      DrJava.getConfig().removeOptionListener(p.first(), p.second());
    }
    if (_lastIndex < OptionConstants.FIND_RESULTS_COLORS.length) {
      --DefinitionsPane.FIND_RESULTS_PAINTERS_USAGE[_lastIndex];
    }
  }

  
  public boolean isSearchAll() { return _searchAll; }
  
  
  public OpenDefinitionsDocument getDocument() { return _doc.get(); }

  
  public void disableFindAgain() {
    _doc.clear(); 
    updateButtons(); 
  }
  
  
  private class FindResultsColorOptionListener implements OptionListener<Color> {
    private int _index;
    public FindResultsColorOptionListener(int i) { _index = i; }
    public void optionChanged(OptionEvent<Color> oce) {
      int pos = _colorBox.getSelectedIndex();
      _colorBox.removeItemAt(_index);
      _colorBox.insertItemAt(oce.value, _index);
      _colorBox.setSelectedIndex(pos);
      if (pos == _index) {
        _frame.refreshFindResultsHighlightPainter(FindResultsPanel.this, DefinitionsPane.FIND_RESULTS_PAINTERS[_index]);
      }
    }
  }
}
