

package edu.rice.cs.drjava.ui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.ref.WeakReference;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.FindReplaceMachine;
import edu.rice.cs.drjava.model.FindResult;
import edu.rice.cs.drjava.model.ClipboardHistoryModel;
import edu.rice.cs.drjava.model.MovingDocumentRegion;
import edu.rice.cs.drjava.model.RegionManager;

import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.SwingDocument;


class FindReplacePanel extends TabbedPanel implements ClipboardOwner {

  
  public static final char LEFT = '\u'; 
  public static final char RIGHT = '\u'; 
  
  private JButton _findNextButton;
  private JButton _findPreviousButton;
  private JButton _findAllButton;
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
  private JCheckBox _ignoreTestCases;
  private JCheckBox _searchSelectionOnly;
  
  
  
  private FindReplaceMachine _machine;
  private SingleDisplayModel _model;
  private DefinitionsPane _defPane = null;
  private boolean _caretChanged;
  
  private boolean _isFindReplaceActive = false;
  public boolean isFindReplaceActive() {return _isFindReplaceActive;}
  
  
  private CaretListener _caretListener = new CaretListener() {
    public void caretUpdate(CaretEvent e) {
           
      assert EventQueue.isDispatchThread();


          _replaceAction.setEnabled(false);
          _replaceFindNextAction.setEnabled(false);
          _replaceFindPreviousAction.setEnabled(false);
          _machine.positionChanged();
          _caretChanged = true;


    }
  };
  
  
  Action _findNextAction = new AbstractAction("Find Next") {
    public void actionPerformed(ActionEvent e) { findNext(); }
  };
  
  Action _findPreviousAction =  new AbstractAction("Find Previous") {
    public void actionPerformed(ActionEvent e) { findPrevious(); }
  };
  
  private Action _findAllAction =  new AbstractAction("Find All") {
    public void actionPerformed(final ActionEvent e) { _isFindReplaceActive = true; _findAll(); _isFindReplaceActive = false;}
  };
  
  private Action _doFindAction = new AbstractAction("Do Find") {
    public void actionPerformed(ActionEvent e) { _doFind(); }
  };
  
  Action _replaceAction = new AbstractAction("Replace") {
    public void actionPerformed(ActionEvent e) { _replace(); }
  };
  
  Action _replaceFindNextAction = new AbstractAction("Replace/Find Next") {
    public void actionPerformed(ActionEvent e) { _replaceFindNext(); }
  };
  
  Action _replaceFindPreviousAction = new AbstractAction("Replace/Find Previous") {
    public void actionPerformed(ActionEvent e) { _replaceFindPrevious(); };
  };
  
  
  private Action _replaceAllAction = new AbstractAction("Replace All") {
    public void actionPerformed(ActionEvent e) { _replaceAll(); }
  };
  
  
  
  
  
  
  
  Action _standardNewlineAction = new TextAction("Newline Action") {
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
  
  
  
  
  public FindReplacePanel(MainFrame frame, SingleDisplayModel model) {
    super(frame, "Find/Replace");
    _model = model;
    _machine = new FindReplaceMachine(_model, _model.getDocumentIterator(), frame);

    
    
    
    _findNextButton = new JButton(_findNextAction);
    _findPreviousButton = new JButton(_findPreviousAction);
    _findAllButton = new JButton(_findAllAction);
    _replaceButton = new JButton(_replaceAction);
    _replaceFindNextButton = new JButton(_replaceFindNextAction);
    _replaceFindPreviousButton = new JButton(_replaceFindPreviousAction);
    _replaceAllButton = new JButton(_replaceAllAction);
    
    _replaceAction.setEnabled(false);
    _replaceFindNextAction.setEnabled(false);
    _replaceFindPreviousAction.setEnabled(false);
    
    
    
    _findField = new JTextPane(new DefaultStyledDocument());
    _replaceField = new JTextPane(new SwingDocument());
    
    
    AbstractDJPane.disableAltCntlMetaChars(_findField);
    AbstractDJPane.disableAltCntlMetaChars(_replaceField);
    
    
    _findField.addKeyListener(frame._historyListener);
    _findField.addFocusListener(frame._focusListenerForRecentDocs);
    _replaceField.addKeyListener(frame._historyListener);
    _findField.addFocusListener(frame._focusListenerForRecentDocs);
    
    
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
    for(KeyStroke k: DrJava.getConfig().getSetting(OptionConstants.KEY_CUT)) findIM.put(k, "Cut");
    for(KeyStroke k: DrJava.getConfig().getSetting(OptionConstants.KEY_COPY)) findIM.put(k, "Copy");
    replaceIM.put(enter, "Insert Newline");
    replaceIM.put(ctrlEnter, "Insert Newline");
    replaceIM.put(ctrlTab, "Insert Tab");
    for(KeyStroke k: DrJava.getConfig().getSetting(OptionConstants.KEY_CUT)) replaceIM.put(k, "Cut");
    for(KeyStroke k: DrJava.getConfig().getSetting(OptionConstants.KEY_COPY)) replaceIM.put(k, "Copy");
    
    Action insertTabAction = new DefaultEditorKit.InsertTabAction();
    ActionMap findAM = _findField.getActionMap();
    ActionMap replaceAM = _replaceField.getActionMap();
    findAM.put("Do Find", _doFindAction);
    findAM.put("Insert Newline", _standardNewlineAction);
    findAM.put("Insert Tab", insertTabAction);
    findAM.put("Cut", cutAction);
    findAM.put("Copy", copyAction);
    replaceAM.put("Insert Newline", _standardNewlineAction);
    replaceAM.put("Insert Tab", insertTabAction);
    replaceAM.put("Cut", cutAction);
    replaceAM.put("Copy", copyAction);
    
    
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
    buttons.add(_findAllButton);
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
        if(isSelected)
          _searchSelectionOnly.setSelected(false);
        _machine.setSearchAllDocuments(isSelected);
        DrJava.getConfig().setSetting(OptionConstants.FIND_ALL_DOCUMENTS, isSelected);
        _findField.requestFocusInWindow();
      }
    });
    
    boolean searchSelection = DrJava.getConfig().getSetting(OptionConstants.FIND_ONLY_SELECTION);
    _searchSelectionOnly = new JCheckBox("Search Selection Only", searchSelection);
    _machine.setSearchSelectionOnly(searchSelection);
    _searchSelectionOnly.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
        _machine.setSearchSelectionOnly(isSelected);
        if(isSelected) {
          _ignoreTestCases.setSelected(false);
          _searchAllDocuments.setSelected(false);
          _findNextAction.setEnabled(false);
          _findPreviousAction.setEnabled(false);
          _replaceFindNextAction.setEnabled(false);
          _replaceAction.setEnabled(false);
          _replaceFindPreviousAction.setEnabled(false);
        }
        else {
          _findNextAction.setEnabled(true);
          _findPreviousAction.setEnabled(true);
          _replaceFindNextAction.setEnabled(true);
          _replaceAction.setEnabled(true);
          _replaceFindPreviousAction.setEnabled(true);
        }
        DrJava.getConfig().setSetting(OptionConstants.FIND_ONLY_SELECTION, isSelected);
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
    
    boolean ignoreTestCasesSelected = DrJava.getConfig().getSetting(OptionConstants.FIND_NO_TEST_CASES);
    _ignoreTestCases = new JCheckBox("No Test Cases", ignoreTestCasesSelected);
    _machine.setIgnoreTestCases(ignoreTestCasesSelected);
    _ignoreTestCases.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean isSelected = (e.getStateChange() == ItemEvent.SELECTED);
        if(isSelected) 
          _searchSelectionOnly.setSelected(false);
        _machine.setIgnoreTestCases(isSelected);
        DrJava.getConfig().setSetting(OptionConstants.FIND_NO_TEST_CASES, isSelected);
        _findField.requestFocusInWindow();
      }
    });

    
    
    
    
    
    this.removeAll(); 
    
    
    _closePanel = new JPanel(new BorderLayout());
    _closePanel.add(_closeButton, BorderLayout.NORTH);
    
    JPanel _lowerCheckPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    _lowerCheckPanel.add(_matchWholeWord); 
    _lowerCheckPanel.add(_ignoreCommentsAndStrings);
    _lowerCheckPanel.setMaximumSize(new Dimension(200, 40));
    
    JPanel _matchCaseAndAllDocsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    _matchCase.setPreferredSize(_matchWholeWord.getPreferredSize());
    _matchCaseAndAllDocsPanel.add(_matchCase);
    _matchCaseAndAllDocsPanel.add(_searchAllDocuments);
    _matchCaseAndAllDocsPanel.setMaximumSize(new Dimension(200, 40));

    JPanel _ignoreTestCasesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    _ignoreTestCasesPanel.add(_ignoreTestCases);
    _ignoreTestCasesPanel.add(_searchSelectionOnly);
    _ignoreTestCasesPanel.setMaximumSize(new Dimension(200, 40));
    
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
    
    
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    JPanel emptyPanel = new JPanel();
    JPanel optionsPanel = new JPanel(gbLayout);
    optionsPanel.setLayout(gbLayout);
    optionsPanel.add(_matchCaseAndAllDocsPanel);
    optionsPanel.add(_lowerCheckPanel);
    optionsPanel.add(_ignoreTestCasesPanel);
    optionsPanel.add(emptyPanel);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    gbLayout.setConstraints(_matchCaseAndAllDocsPanel, c);
    gbLayout.setConstraints(_lowerCheckPanel, c);
    gbLayout.setConstraints(_ignoreTestCasesPanel, c);
    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.SOUTH;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.weighty = 1.0;
    
    gbLayout.setConstraints(emptyPanel, c);
    
    
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
    
    this.add(newPanel);
    
    
    
    
    _findField.getDocument().addDocumentListener(new DocumentListener() {
      
      
      public void changedUpdate(DocumentEvent e) { _updateHelper(); }
      
      
      public void insertUpdate(DocumentEvent e) { _updateHelper(); }
      
      
      public void removeUpdate(DocumentEvent e) { _updateHelper(); }
      
      private void _updateHelper() {
        assert EventQueue.isDispatchThread();

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
  
  
  public boolean requestFocusInWindow() {
    super.requestFocusInWindow();
    _findField.selectAll();
    return _findField.requestFocusInWindow();
  }
  
  
  JTextPane getFindField() { return _findField; }

  
  private void _findAll() {
    

    
    _findLabelBot.setText("Next");
    String searchStr = _findField.getText();
    String title = searchStr;
    OpenDefinitionsDocument startDoc = _defPane.getOpenDefDocument();
    boolean searchAll = _machine.getSearchAllDocuments();
    boolean searchSelectionOnly = _machine.getSearchSelectionOnly();



    String tabLabel = (title.length() <= 20) ? title : title.substring(0,20);
    RegionManager<MovingDocumentRegion> rm = _model.createFindResultsManager();
    MovingDocumentRegion region = new MovingDocumentRegion(startDoc, 
                                                           _defPane.getSelectionStart(), 
                                                           _defPane.getSelectionEnd(), 
                                                           startDoc._getLineStartPos(_defPane.getSelectionStart()),
                                                           startDoc._getLineEndPos(_defPane.getSelectionEnd()));
    final FindResultsPanel panel = 
      _frame.createFindResultsPanel(rm, region, tabLabel, searchStr, searchAll, searchSelectionOnly, _machine.getMatchCase(), 
                                    _machine.getMatchWholeWord(), _machine.getIgnoreCommentsAndStrings(),
                                    _ignoreTestCases.isSelected(), new WeakReference<OpenDefinitionsDocument>(startDoc),
                                    this);
    findAll(searchStr, searchAll, searchSelectionOnly, _machine.getMatchCase(), _machine.getMatchWholeWord(),
            _machine.getIgnoreCommentsAndStrings(), _ignoreTestCases.isSelected(), startDoc, rm, region, panel);

    panel.requestFocusInWindow();
    EventQueue.invokeLater(new Runnable() { public void run() { panel._regTree.scrollRowToVisible(0); } });
  }
  
  
  public void findAll(String searchStr, final boolean searchAll, final boolean searchSelectionOnly, final boolean matchCase,
                      final boolean wholeWord, final boolean noComments, final boolean noTestCases,
                      final OpenDefinitionsDocument startDoc, final RegionManager<MovingDocumentRegion> rm, final MovingDocumentRegion region,
                      final FindResultsPanel panel) {
    
    _machine.setSearchBackwards(false);

    int searchLen = searchStr.length();
    if (searchLen == 0) return;
    
    _frame.updateStatusField("Finding All");
    OpenDefinitionsDocument oldDoc = _machine.getDocument();
    OpenDefinitionsDocument oldFirstDoc = _machine.getFirstDoc();
    String oldFindWord = _machine.getFindWord();
    boolean oldSearchAll = _machine.getSearchAllDocuments();
    boolean oldSearchSelectionOnly = _machine.getSearchSelectionOnly();
    boolean oldMatchCase = _machine.getMatchCase();
    boolean oldWholeWord = _machine.getMatchWholeWord();
    boolean oldNoComments = _machine.getIgnoreCommentsAndStrings();
    boolean oldNoTestCases = _machine.getIgnoreTestCases();
    int oldPosition = _machine.getCurrentOffset();
    

    _machine.setDocument(startDoc);
    if (_machine.getFirstDoc() == null) _machine.setFirstDoc(startDoc);
    _machine.setSearchAllDocuments(searchAll);
    _machine.setSearchSelectionOnly(searchSelectionOnly);
    _machine.setMatchCase(matchCase);
    if (wholeWord) { _machine.setMatchWholeWord(); }
    else { _machine.setFindAnyOccurrence(); }
    _machine.setIgnoreCommentsAndStrings(noComments);
    _machine.setPosition(startDoc.getCurrentLocation());
    _machine.setIgnoreTestCases(noTestCases);

    _machine.setFindWord(searchStr);
    String replaceStr = _replaceField.getText();
    _machine.setReplaceWord(replaceStr);
    _frame.clearStatusMessage();
    final List<FindResult> results = new ArrayList<FindResult>();
    
    _frame.hourglassOn();
    try {
      
      final int count = _machine.processAll(new Runnable1<FindResult>() {
        public void run(FindResult fr) { results.add(fr); }
      }, region);
      
      _machine.setDocument(oldDoc);
      _machine.setFirstDoc(oldFirstDoc);
      _machine.setFindWord(oldFindWord);
      _machine.setSearchAllDocuments(oldSearchAll);
      _machine.setSearchSelectionOnly(oldSearchSelectionOnly);
      _machine.setMatchCase(oldMatchCase);
      if (oldWholeWord) { _machine.setMatchWholeWord(); }
      else { _machine.setFindAnyOccurrence(); }
      _machine.setIgnoreCommentsAndStrings(oldNoComments);
      _machine.setIgnoreTestCases(oldNoTestCases);
      _machine.setPosition(oldPosition);

      for (FindResult fr: results) {
         
        final OpenDefinitionsDocument doc = fr.getDocument();
        
        if (_model.getActiveDocument() != doc) _model.setActiveDocument(doc);
        else _model.refreshActiveDocument();
        
        int end = fr.getFoundOffset();
        int start = end - searchLen;
        int lineStart = doc._getLineStartPos(start);
        int lineEnd = doc._getLineEndPos(end);
        
        rm.addRegion(new MovingDocumentRegion(doc, start, end, lineStart, lineEnd));                       
      }
      


      if (count > 0) _frame.showFindResultsPanel(panel);
      else { 
        Toolkit.getDefaultToolkit().beep();
        panel.freeResources(); 
      }
      _frame.setStatusMessage("Found " + count + " occurrence" + ((count == 1) ? "" : "s") + ".");


          
      if (searchSelectionOnly) {
        EventQueue.invokeLater(new Runnable() { public void run() { 
          if (_defPane != null) {
            _defPane.requestFocusInWindow();
            _defPane.setSelectionStart(region.getStartOffset());
            _defPane.setSelectionEnd(region.getEndOffset());
          }
        } });
      }          
    }
    finally { 
      _frame.hourglassOff(); 
      
      
      _model.setActiveDocument(startDoc);
    }
  }
  
  
  private void _replaceAll() {
    _frame.updateStatusField("Replacing All");

    _machine.setFindWord(_findField.getText());
    _machine.setReplaceWord(_replaceField.getText());
    _machine.setSearchBackwards(false);
    OpenDefinitionsDocument startDoc = _defPane.getOpenDefDocument();
    MovingDocumentRegion region = new MovingDocumentRegion(startDoc, 
                                                           _defPane.getSelectionStart(), 
                                                           _defPane.getSelectionEnd(), 
                                                           startDoc._getLineStartPos(_defPane.getSelectionStart()),
                                                           startDoc._getLineEndPos(_defPane.getSelectionEnd()));
    _machine.setSelection(region);
    _frame.clearStatusMessage();
    int count = _machine.replaceAll();
    Toolkit.getDefaultToolkit().beep();
    _frame.setStatusMessage("Replaced " + count + " occurrence" + ((count == 1) ? "" : "s") + ".");
    _replaceAction.setEnabled(false);
    _replaceFindNextAction.setEnabled(false);
    _replaceFindPreviousAction.setEnabled(false);
    _model.refreshActiveDocument();  
  }
  
  private void _replaceFindNext() {
    _frame.updateStatusField("Replacing and Finding Next");
    if (isSearchBackwards() == true) {
      _machine.positionChanged();
      findNext();
    }
    _updateMachine();
    _machine.setFindWord(_findField.getText());
    final String replaceWord = _replaceField.getText();
    _machine.setReplaceWord(replaceWord);
    _frame.clearStatusMessage(); 
    
    
    boolean replaced = _machine.replaceCurrent();
    
    if (replaced) {
      _selectFoundOrReplacedItem(replaceWord.length());
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
  
  private void _replaceFindPrevious() {
    _frame.updateStatusField("Replacing and Finding Previous");
    if (isSearchBackwards() == false) {
      _machine.positionChanged();
      findPrevious();
    }
    _updateMachine();
    _machine.setFindWord(_findField.getText());
    final String replaceWord = _replaceField.getText();
    _machine.setReplaceWord(replaceWord);
    _frame.clearStatusMessage(); 
    
    
    boolean replaced = _machine.replaceCurrent();
    
    if (replaced) {
      _selectFoundOrReplacedItem(replaceWord.length());
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
  
  
  void findNext() {
    _frame.updateStatusField("Finding Next");
    _machine.setSearchBackwards(false);
    _findLabelBot.setText("Next");
    _doFind();  
    if (DrJava.getConfig().getSetting(OptionConstants.FIND_REPLACE_FOCUS_IN_DEFPANE).booleanValue()) {
      _defPane.requestFocusInWindow();  
    }
  }
  
  
  void findPrevious() {
    _frame.updateStatusField("Finding Previous");
    _machine.setSearchBackwards(true);
    _findLabelBot.setText("Prev");
    _doFind();
    if (DrJava.getConfig().getSetting(OptionConstants.FIND_REPLACE_FOCUS_IN_DEFPANE).booleanValue()) {
      _defPane.requestFocusInWindow();  
    }
  }
  
  private void _replace() {
    _frame.updateStatusField("Replacing");

    _machine.setFindWord(_findField.getText());
    final String replaceWord = _replaceField.getText();
    _machine.setReplaceWord(replaceWord);
    _frame.clearStatusMessage();
    
    
    boolean replaced = _machine.replaceCurrent();
    if (replaced) _selectFoundOrReplacedItem(replaceWord.length());
    _replaceAction.setEnabled(false);
    _replaceFindNextAction.setEnabled(false);
    _replaceFindPreviousAction.setEnabled(false);
    _replaceButton.requestFocusInWindow();
  }
  
  
  void beginListeningTo(DefinitionsPane defPane) {
    if (_defPane == null) {
      

      _displayed = true;
      _defPane = defPane;
      _defPane.addCaretListener(_caretListener);
      _caretChanged = true;
      
      _updateMachine();
      _machine.setFindWord(_findField.getText());
      _machine.setReplaceWord(_replaceField.getText());
      _frame.clearStatusMessage(); 
      if (! _machine.onMatch() || _findField.getText().equals("")) {
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
      throw new UnexpectedException(new RuntimeException("FindReplacePanel should not be listening to anything"));
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
      final String findWord = _findField.getText();
      _machine.setFindWord(findWord);
      _machine.setReplaceWord(_replaceField.getText());
      _frame.clearStatusMessage(); 
      final boolean searchAll = _machine.getSearchAllDocuments();
      
      
      
      
      _frame.hourglassOn();
      try {
        FindResult fr = _machine.findNext();
        OpenDefinitionsDocument matchDoc = fr.getDocument();

        OpenDefinitionsDocument openDoc = _defPane.getOpenDefDocument();
        final boolean docChanged = matchDoc != openDoc;
        
        final int pos = fr.getFoundOffset();
        
        if (pos >= 0) _model.addToBrowserHistory();  
        
        if (searchAll) {  
          if (docChanged) _model.setActiveDocument(matchDoc);  
          else _model.refreshActiveDocument();  
        } 
        
        if (fr.getWrapped() && ! searchAll) {
          Toolkit.getDefaultToolkit().beep();
          if (! _machine.isSearchBackwards()) _frame.setStatusMessage("Search wrapped to beginning.");
          else _frame.setStatusMessage("Search wrapped to end.");
        }
        
        if (fr.getAllWrapped() && searchAll) {
          Toolkit.getDefaultToolkit().beep();
          _frame.setStatusMessage("Search wrapped around all documents.");
        }
        
        if (pos >= 0) { 


          _defPane.setCaretPosition(pos);
          _caretChanged = true;
          _updateMachine();
          
          final Runnable command = new Runnable() {
            public void run() {
              _selectFoundOrReplacedItem(findWord.length());
              _replaceAction.setEnabled(true);
              _replaceFindNextAction.setEnabled(true);
              _replaceFindPreviousAction.setEnabled(true);
              _machine.setLastFindWord();
              _model.addToBrowserHistory();
              if (DrJava.getConfig().getSetting(OptionConstants.FIND_REPLACE_FOCUS_IN_DEFPANE).booleanValue()) {
                
                _frame.toFront();
                EventQueue.invokeLater(new Runnable() { public void run() { 
                  if (_defPane != null) {
                    _defPane.requestFocusInWindow();
                  }
                } });
              }
            } };
          
          if (docChanged)
            
            EventQueue.invokeLater(command);
          else command.run();
        }
        
        
        else {
          Toolkit.getDefaultToolkit().beep();
          final StringBuilder statusMessage = new StringBuilder("Search text \"");
          if (findWord.length() <= 50) statusMessage.append(findWord);
          else statusMessage.append(findWord.substring(0, 49) + "...");
          statusMessage.append("\" not found.");
          _frame.setStatusMessage(statusMessage.toString());
        }
      }
      finally { _frame.hourglassOff(); }
    }
    
    if (! DrJava.getConfig().getSetting(OptionConstants.FIND_REPLACE_FOCUS_IN_DEFPANE).booleanValue()) {
      _findField.requestFocusInWindow();
    }
  }
  
  @Override
  protected void _close() {
    _defPane.requestFocusInWindow();
    if (_displayed) stopListening();
    super._close();
    
  }
  
  public void setSearchBackwards(boolean b) { _machine.setSearchBackwards(b); }
  public boolean isSearchBackwards() { return _machine.isSearchBackwards(); }
  
  
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
  










  
  
  private void _selectFoundOrReplacedItem(int length) {
    int offset = _machine.getCurrentOffset();
    int from, to;
    
    if (_machine.isSearchBackwards()) {
      from = offset + length;
      
      
      to = offset;
    }
    else {
      from = offset - length;
      to = offset;
    }
    _selectFoundOrReplacedItem(from, to);
  }
  
  









  
  
  private void _selectFoundOrReplacedItem(int from, int to) {
    _defPane.centerViewOnOffset(from);
    _defPane.select(from, to);
    
    
    
    EventQueue.invokeLater(new Runnable() { 
      public void run() { _defPane.getCaret().setSelectionVisible(true); } 
    });

  }
  






  




















  
  
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    
  }
  
  
  Action cutAction = new DefaultEditorKit.CutAction() {
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JTextComponent) {
        JTextComponent tc = (JTextComponent)e.getSource();
        if (tc.getSelectedText() != null) {
          super.actionPerformed(e);
          String s = edu.rice.cs.util.swing.Utilities.getClipboardSelection(FindReplacePanel.this);
          if (s != null && s.length() != 0){ ClipboardHistoryModel.singleton().put(s); }
        }
      }
    }
  };
  
  
  Action copyAction = new DefaultEditorKit.CopyAction() {
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JTextComponent) {
        JTextComponent tc = (JTextComponent)e.getSource();
        if (tc.getSelectedText() != null) {
          super.actionPerformed(e);
          String s = edu.rice.cs.util.swing.Utilities.getClipboardSelection(FindReplacePanel.this);
          if (s != null && s.length() != 0) { ClipboardHistoryModel.singleton().put(s); }
        }
      }
    }
  };  
  
  
  public DefinitionsPane getDefPane() { return _defPane; }
  public JButton getFindNextButton() {return _findNextButton; }
}
