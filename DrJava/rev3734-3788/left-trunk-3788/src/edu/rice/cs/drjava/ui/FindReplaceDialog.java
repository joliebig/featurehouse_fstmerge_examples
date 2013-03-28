

package edu.rice.cs.drjava.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.FindReplaceMachine;
import edu.rice.cs.drjava.model.FindResult;

import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.AbstractDocumentInterface;
import edu.rice.cs.util.text.SwingDocument;
import edu.rice.cs.util.UnexpectedException;



class FindReplaceDialog extends TabbedPanel {

  private JButton _findNextButton;
  private JButton _findPreviousButton;
  private JButton _replaceButton;
  private JButton _replaceFindNextButton;
  private JButton _replaceFindPreviousButton;
  private JButton _replaceAllButton;

  private JTextPane _findField;
  private JTextPane _replaceField;

  private JLabel _findLabelBot; 

  private JCheckBox _ignoreCommentsAndStrings;
  private JCheckBox _matchCase;
  private JCheckBox _searchAllDocuments;
  private JCheckBox _matchWholeWord;

  private FindReplaceMachine _machine;
  private SingleDisplayModel _model;
  private DefinitionsPane _defPane = null;
  private boolean _caretChanged;
  
  
  private CaretListener _caretListener = new CaretListener() {
    public void caretUpdate(CaretEvent e) {
      Utilities.invokeAndWait(new Runnable() {
        public void run() {
          _replaceAction.setEnabled(false);
          _replaceFindNextAction.setEnabled(false);
          _replaceFindPreviousAction.setEnabled(false);
          _machine.positionChanged();
          _caretChanged = true;
        }
      });
    }
  };
  
  
  private Action _findNextAction = new AbstractAction("Find Next") {
    public void actionPerformed(ActionEvent e) { findNext(); }
  };
  
  private Action _findPreviousAction =  new AbstractAction("Find Previous") {
    public void actionPerformed(ActionEvent e) { findPrevious(); }
  };
  
  private Action _doFindAction = new AbstractAction("Do Find") {
    public void actionPerformed(ActionEvent e) { _doFind(); }
  };
                                                            
  private Action _replaceAction = new AbstractAction("Replace") {
    public void actionPerformed(ActionEvent e) {
      _updateMachine();
      _machine.setFindWord(_findField.getText());
      String replaceWord = _replaceField.getText();
      _machine.setReplaceWord(replaceWord);
      _frame.clearStatusMessage();

      
      boolean replaced = _machine.replaceCurrent();
      if (replaced) {
        _selectReplacedItem(replaceWord.length());
      }
      _replaceAction.setEnabled(false);
      _replaceFindNextAction.setEnabled(false);
      _replaceFindPreviousAction.setEnabled(false);
      _replaceButton.requestFocusInWindow();
    }
  };

  private Action _replaceFindNextAction = new AbstractAction("Replace/Find Next") {
    public void actionPerformed(ActionEvent e) {
      if (getSearchBackwards() == true) {
        _machine.positionChanged();
        findNext();
      }
      _updateMachine();
      _machine.setFindWord(_findField.getText());
      String replaceWord = _replaceField.getText();
      _machine.setReplaceWord(replaceWord);
      _frame.clearStatusMessage(); 
      
      
      boolean replaced = _machine.replaceCurrent();
      
      if (replaced) {
        _selectReplacedItem(replaceWord.length());
        findNext();
        _replaceFindNextButton.requestFocusInWindow();
      }
      else {
        _replaceAction.setEnabled(false);
        _replaceFindNextAction.setEnabled(false);
        _replaceFindPreviousAction.setEnabled(false);
        Toolkit.getDefaultToolkit().beep();
        _frame.setStatusMessage("Replace failed.");
      }
    }
  };
  
  private Action _replaceFindPreviousAction = new AbstractAction("Replace/Find Previous") {
    public void actionPerformed(ActionEvent e) {
      if (getSearchBackwards() == false) {
        _machine.positionChanged();
        findPrevious();
      }
      _updateMachine();
      _machine.setFindWord(_findField.getText());
      String replaceWord = _replaceField.getText();
      _machine.setReplaceWord(replaceWord);
      _frame.clearStatusMessage(); 
      
      
      boolean replaced = _machine.replaceCurrent();
      
      if (replaced) {
        _selectReplacedItem(replaceWord.length());
        findPrevious();
        _replaceFindPreviousButton.requestFocusInWindow();
      }
      else {
        _replaceAction.setEnabled(false);
        _replaceFindNextAction.setEnabled(false);
        _replaceFindPreviousAction.setEnabled(false);
        Toolkit.getDefaultToolkit().beep();
        _frame.setStatusMessage("Replace failed.");
      }
    }
  };

  
  private Action _replaceAllAction = new AbstractAction("Replace All") {
    public void actionPerformed(ActionEvent e) {
      _updateMachine();
      _machine.setFindWord(_findField.getText());
      _machine.setReplaceWord(_replaceField.getText());
      _frame.clearStatusMessage();
      int count = _machine.replaceAll();
      Toolkit.getDefaultToolkit().beep();
      _frame.setStatusMessage("Replaced " + count + " occurrence" + ((count == 1) ? "" :
                                                                           "s") + ".");
      _replaceAction.setEnabled(false);
      _replaceFindNextAction.setEnabled(false);
      _replaceFindPreviousAction.setEnabled(false);
    }
  };
  
  
  
  
  
  
  
  Action _standardNewlineAction = new TextAction("NewLine Action") {
    public void actionPerformed(ActionEvent e) {
      JTextComponent c = getTextComponent(e);
      String text = c.getText();
      int caretPos = c.getCaretPosition();
      String textBeforeCaret = text.substring(0, caretPos);
      String textAfterCaret = text.substring(caretPos);
      c.setText(textBeforeCaret.concat("\n").concat(textAfterCaret));
      c.setCaretPosition(caretPos+1);
  }
};    


  
    
            
  
  public FindReplaceDialog(MainFrame frame, SingleDisplayModel model) {
    super(frame, "Find/Replace");
    _model = model;
    _machine = new FindReplaceMachine(_model, _model.getDocumentIterator());
    _updateMachine();
    
    
    
    _findNextButton = new JButton(_findNextAction);
    _findPreviousButton = new JButton(_findPreviousAction);
    _replaceButton = new JButton(_replaceAction);
    _replaceFindNextButton = new JButton(_replaceFindNextAction);
    _replaceFindPreviousButton = new JButton(_replaceFindPreviousAction);
    _replaceAllButton = new JButton(_replaceAllAction);

    _replaceAction.setEnabled(false);
    _replaceFindNextAction.setEnabled(false);
    _replaceFindPreviousAction.setEnabled(false);

    
    
    _findField = new JTextPane(new DefaultStyledDocument());
    _replaceField = new JTextPane(new SwingDocument());
    
    
    int tabForward = KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS;
    int tabBackward = KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS;
    _findField.setFocusTraversalKeys(tabForward, null);
    _replaceField.setFocusTraversalKeys(tabForward, null);
    _findField.setFocusTraversalKeys(tabBackward, null);
    _replaceField.setFocusTraversalKeys(tabBackward, null);
    
    
    KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
    KeyStroke ctrlEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK);
    KeyStroke ctrlTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.CTRL_MASK);
    InputMap findIM = _findField.getInputMap();
    InputMap replaceIM = _replaceField.getInputMap();
    findIM.put(enter, "Do Find");
    findIM.put(ctrlEnter, "Insert Newline");
    findIM.put(ctrlTab, "Insert Tab");
    replaceIM.put(enter, "Insert Newline");
    replaceIM.put(ctrlEnter, "Insert Newline");
    replaceIM.put(ctrlTab, "Insert Tab");
    
    Action insertTabAction = new DefaultEditorKit.InsertTabAction();
    ActionMap findAM = _findField.getActionMap();
    ActionMap replaceAM = _replaceField.getActionMap();
    findAM.put("Do Find", _doFindAction);
    findAM.put("Insert Newline", _standardNewlineAction);
    findAM.put("Insert Tab", insertTabAction);
    replaceAM.put("Insert Newline", _standardNewlineAction);
    replaceAM.put("Insert Tab", insertTabAction);
    
    
    new ForegroundColorListener(_findField);
    new BackgroundColorListener(_findField);
    new ForegroundColorListener(_replaceField);
    new BackgroundColorListener(_replaceField);
    Font font = DrJava.getConfig().getSetting(OptionConstants.FONT_MAIN);
    setFieldFont(font);
    
    
    
    
    JLabel _replaceLabelTop = new JLabel("Replace", SwingConstants.RIGHT);
    JLabel _replaceLabelBot = new JLabel("With", SwingConstants.RIGHT);
    
    JPanel replaceLabelPanelTop = new JPanel(new BorderLayout(5,5));
    JPanel replaceLabelPanelBot = new JPanel(new BorderLayout(5,5));
    JPanel replaceLabelPanel = new JPanel(new GridLayout(2,1));
    
    replaceLabelPanelTop.add(_replaceLabelTop, BorderLayout.SOUTH);
    replaceLabelPanelBot.add(_replaceLabelBot, BorderLayout.NORTH);
    
    replaceLabelPanel.add(replaceLabelPanelTop);
    replaceLabelPanel.add(replaceLabelPanelBot);
    
    
    
    JLabel _findLabelTop = new JLabel("Find", SwingConstants.RIGHT);
    _findLabelBot = new JLabel("Next", SwingConstants.RIGHT);
    
    JPanel findLabelPanelTop = new JPanel(new BorderLayout(5,5));
    JPanel findLabelPanelBot = new JPanel(new BorderLayout(5,5));
    JPanel findLabelPanel = new JPanel(new GridLayout(2,1));
    
    findLabelPanelTop.add(_findLabelTop, BorderLayout.SOUTH);
    findLabelPanelBot.add(_findLabelBot, BorderLayout.NORTH);
    
    findLabelPanel.add(findLabelPanelTop);
    findLabelPanel.add(findLabelPanelBot);

    
    
    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(1,0,5,0));
    buttons.add(_findNextButton);
    buttons.add(_findPreviousButton);
    buttons.add(_replaceFindNextButton);
    buttons.add(_replaceFindPreviousButton);
    buttons.add(_replaceButton);
    buttons.add(_replaceAllButton);
   
    
    
    boolean matchCaseSelected = DrJava.getConfig().getSetting(OptionConstants.FIND_MATCH_CASE);
    _matchCase = new JCheckBox("Match Case", matchCaseSelected);
    _machine.setMatchCase(matchCaseSelected);
    _matchCase.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
        _machine.setMatchCase(isSelected);
        DrJava.getConfig().setSetting(OptionConstants.FIND_MATCH_CASE, isSelected);
        _findField.requestFocusInWindow();
      }
    });
    
    boolean searchAllSelected = DrJava.getConfig().getSetting(OptionConstants.FIND_ALL_DOCUMENTS);
    _searchAllDocuments = new JCheckBox("Search All Documents", searchAllSelected);
    _machine.setSearchAllDocuments(searchAllSelected);
    _searchAllDocuments.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
        _machine.setSearchAllDocuments(isSelected);
        DrJava.getConfig().setSetting(OptionConstants.FIND_ALL_DOCUMENTS, isSelected);
        _findField.requestFocusInWindow();
      }
    });
    
    boolean matchWordSelected = DrJava.getConfig().getSetting(OptionConstants.FIND_WHOLE_WORD);
    _matchWholeWord = new JCheckBox("Whole Word", matchWordSelected);
    if (matchWordSelected) { _machine.setMatchWholeWord(); }
    else { _machine.setFindAnyOccurrence(); }
    _matchWholeWord.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
        if (isSelected) { _machine.setMatchWholeWord(); }
        else { _machine.setFindAnyOccurrence(); }
        DrJava.getConfig().setSetting(OptionConstants.FIND_WHOLE_WORD, isSelected);
        _findField.requestFocusInWindow();
      }
    });
    
    boolean ignoreCommentsSelected = DrJava.getConfig().getSetting(OptionConstants.FIND_NO_COMMENTS_STRINGS);
    _ignoreCommentsAndStrings = new JCheckBox("No Comments/Strings", ignoreCommentsSelected);
    _machine.setIgnoreCommentsAndStrings(ignoreCommentsSelected);
    _ignoreCommentsAndStrings.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
        _machine.setIgnoreCommentsAndStrings(isSelected);
        DrJava.getConfig().setSetting(OptionConstants.FIND_NO_COMMENTS_STRINGS, isSelected);
        _findField.requestFocusInWindow();
      }
    });
    
    
    
    

    
    this.removeAll(); 

    
    _closePanel = new JPanel(new BorderLayout());
    _closePanel.add(_closeButton, BorderLayout.NORTH);

    JPanel _lowerCheckPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    _lowerCheckPanel.add(_matchWholeWord); 
    _lowerCheckPanel.add(_ignoreCommentsAndStrings);
    _lowerCheckPanel.setMaximumSize(new Dimension(1000, 40));

    JPanel _matchCaseAndAllDocsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    _matchCase.setPreferredSize(_matchWholeWord.getPreferredSize());
    _matchCaseAndAllDocsPanel.add(_matchCase);
    _matchCaseAndAllDocsPanel.add(_searchAllDocuments);
    _matchCaseAndAllDocsPanel.setMaximumSize(new Dimension(1000, 40));

    BorderlessScrollPane _findPane = new BorderlessScrollPane(_findField);
    BorderlessScrollPane _replacePane = new BorderlessScrollPane(_replaceField);
    _findPane.setHorizontalScrollBarPolicy(BorderlessScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    _replacePane.setHorizontalScrollBarPolicy(BorderlessScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    JPanel findPanel = new JPanel(new BorderLayout(5,5));
    findPanel.add(findLabelPanel, BorderLayout.WEST);
    findPanel.add(_findPane, BorderLayout.CENTER);

    
    JPanel replacePanel = new JPanel(new BorderLayout(5,5));
    replacePanel.add(replaceLabelPanel, BorderLayout.WEST);
    replacePanel.add(_replacePane, BorderLayout.CENTER);

        
    
    JPanel leftPanel = new JPanel(new GridLayout(1,2,5,5));
    leftPanel.add(findPanel);
    leftPanel.add(replacePanel);

    
    Box optionsPanel = new Box(BoxLayout.Y_AXIS);
    optionsPanel.add(_matchCaseAndAllDocsPanel);
    optionsPanel.add(_lowerCheckPanel);
    optionsPanel.add(Box.createGlue());


    
    JPanel midPanel = new JPanel(new BorderLayout(5,5));
    midPanel.add(leftPanel, BorderLayout.CENTER);
    midPanel.add(optionsPanel, BorderLayout.EAST);
    
    
    
     JPanel _rightPanel = new JPanel(new BorderLayout(5, 5));
    _rightPanel.add(midPanel, BorderLayout.CENTER);
    _rightPanel.add(_closePanel, BorderLayout.EAST); 
    
    JPanel newPanel = new JPanel();
    newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
    newPanel.add(_rightPanel);
    newPanel.add(Box.createVerticalStrut(5));
    newPanel.add(buttons);
    newPanel.add(Box.createVerticalStrut(5));
    
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.add(Box.createHorizontalStrut(5));
    this.add(newPanel);

    
    
    
    
    _findField.getDocument().addDocumentListener(new DocumentListener() {
      
      
      public void changedUpdate(DocumentEvent e) { _updateHelper(); }

      
      public void insertUpdate(DocumentEvent e) { _updateHelper(); }
      
      
      public void removeUpdate(DocumentEvent e) { _updateHelper(); }
      
      private void _updateHelper() {
        Utilities.invokeLater(new Runnable() {
          public void run() {

            updateFirstDocInSearch();
            _replaceAction.setEnabled(false);
            _replaceFindNextAction.setEnabled(false);
            _replaceFindPreviousAction.setEnabled(false);
            _machine.positionChanged();
            if (_findField.getText().equals("")) _replaceAllAction.setEnabled(false);
            else                                 _replaceAllAction.setEnabled(true);
            updateUI();
          }
        });
      }
    });  
    
  }
    
    

  
  public boolean requestFocusInWindow() {
    super.requestFocusInWindow();
    _findField.selectAll();
    return _findField.requestFocusInWindow();
  }

  
  JTextPane getFindField() { return _findField; }

  
  void findNext() { 
    _machine.setSearchBackwards(false);
    _findLabelBot.setText("Next");
    _doFind();
  }
  
  
  void findPrevious() {
    _machine.setSearchBackwards(true);
    _findLabelBot.setText("Prev");
    _doFind();
  }
  
  
  void beginListeningTo(DefinitionsPane defPane) {
    if (_defPane==null) {
      

      _displayed = true;
      _defPane = defPane;
      _defPane.addCaretListener(_caretListener);
      _caretChanged = true;
      
      _updateMachine();
      _machine.setFindWord(_findField.getText());
      _machine.setReplaceWord(_replaceField.getText());
      _frame.clearStatusMessage(); 
      if (!_machine.onMatch() || _findField.getText().equals("")) {
        _replaceAction.setEnabled(false);
        _replaceFindNextAction.setEnabled(false);
        _replaceFindPreviousAction.setEnabled(false);
      }
      else {
        _replaceAction.setEnabled(true);
        _replaceFindNextAction.setEnabled(true);
        _replaceFindPreviousAction.setEnabled(true);
        _machine.setLastFindWord();
      }

      if (_findField.getText().equals("")) _replaceAllAction.setEnabled(false);
      else                                 _replaceAllAction.setEnabled(true);

      _frame.clearStatusMessage();
    }
    else
      throw new UnexpectedException(new RuntimeException("FindReplaceDialog should not be listening to anything"));
  }

  
  public void stopListening() {
    if (_defPane != null) {
      _defPane.removeCaretListener(_caretListener);
      _defPane = null;
      _displayed = false;
      _frame.clearStatusMessage();
    } 
  }

  
  private void _doFind() {
    if (_findField.getText().length() > 0) {
      _updateMachine();
      _machine.setFindWord(_findField.getText());
      _machine.setReplaceWord(_replaceField.getText());
      _frame.clearStatusMessage(); 
      
      
      
      
      FindResult fr = _machine.findNext();
      OpenDefinitionsDocument doc = fr.getDocument();
      OpenDefinitionsDocument matchDoc = _model.getODDForDocument(doc);
      OpenDefinitionsDocument openDoc = _defPane.getOpenDefDocument();
      
      final int pos = fr.getFoundOffset();
      
      
      if (pos != -1) { 
        Caret c = _defPane.getCaret();
        c.setDot(c.getDot());
        
        if (! matchDoc.equals(openDoc)) _model.setActiveDocument(matchDoc);  
        else _model.refreshActiveDocument();  
        
        _defPane.setCaretPosition(pos);
        _caretChanged = true;
        _updateMachine();
      }
      else {  
        _model.refreshActiveDocument();
      }
      
      if (fr.getWrapped() && !_machine.getSearchAllDocuments()) {
        Toolkit.getDefaultToolkit().beep();
        if (!_machine.getSearchBackwards()) _frame.setStatusMessage("Search wrapped to beginning.");
        else  _frame.setStatusMessage("Search wrapped to end.");
      }
      
      if (fr.getAllDocsWrapped() && _machine.getSearchAllDocuments()) {
        Toolkit.getDefaultToolkit().beep();
        _frame.setStatusMessage("Search wrapped around all documents.");
      }
      
      if (pos >= 0) {  
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            _selectFoundItem();
            _replaceAction.setEnabled(true);
            _replaceFindNextAction.setEnabled(true);
            _replaceFindPreviousAction.setEnabled(true);
            _machine.setLastFindWord();
          }});
      }
      
      
      else {
        Toolkit.getDefaultToolkit().beep();
        StringBuffer statusMessage = new StringBuffer("Search text \"");
        if (_machine.getFindWord().length() <= 50) statusMessage.append(_machine.getFindWord());
        else statusMessage.append(_machine.getFindWord().substring(0, 49) + "...");
        statusMessage.append("\" not found.");
        _frame.setStatusMessage(statusMessage.toString());
      }
    }
    
    if (!DrJava.getConfig().getSetting(OptionConstants.FIND_REPLACE_FOCUS_IN_DEFPANE).booleanValue()) {
      _findField.requestFocusInWindow();
    }
  }

  protected void _close() {
    _defPane.requestFocusInWindow();
    if (_displayed) stopListening();
    super._close();
    
  }

  public void setSearchBackwards(boolean b) { _machine.setSearchBackwards(b); }
  public boolean getSearchBackwards() { return _machine.getSearchBackwards(); }

  
  public void setFieldFont(Font f) {
    _findField.setFont(f);
    _replaceField.setFont(f);
  }
  
  
  public void updateFirstDocInSearch() {
    _machine.setFirstDoc(_model.getActiveDocument());
  }






































  
  private void _updateMachine() {
    if (_caretChanged) {
      OpenDefinitionsDocument doc = _model.getActiveDocument();
      _machine.setDocument(doc);
      if (_machine.getFirstDoc() == null) _machine.setFirstDoc(doc);

      _machine.setPosition(_defPane.getCaretPosition());
      _caretChanged = false;
    }
  }












  
  private void _selectReplacedItem(int length) {
    int from, to;
    to = _machine.getCurrentOffset();
    if (_machine.getSearchBackwards()) from = to + length;
    else                               from = to - length;
    _selectFoundItem(from, to);
  }


  
  private void _selectFoundItem() {
    int position = _machine.getCurrentOffset();
    int to, from;
    to = position;
    if (!_machine.getSearchBackwards()) from = position - _machine.getFindWord().length();
    else from = position + _machine.getFindWord().length();
    _selectFoundItem(from, to);
  }

  
  private void _selectFoundItem(int from, int to) {
    _defPane.centerViewOnOffset(from);
    _defPane.select(from, to);

    
    
    _defPane.getCaret().setSelectionVisible(true);

  }


  






  




















  
  
  
  public DefinitionsPane getDefPane() { return _defPane; }
  public JButton getFindNextButton() {return _findNextButton; }
  

}
