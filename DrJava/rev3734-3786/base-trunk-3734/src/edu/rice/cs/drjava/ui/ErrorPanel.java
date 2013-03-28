

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.SingleDisplayModel;

import edu.rice.cs.drjava.model.compiler.CompilerError;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.HighlightManager;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.util.text.SwingDocument;


import java.util.Hashtable;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;


public abstract class ErrorPanel extends TabbedPanel implements OptionConstants {
  
  protected static final SimpleAttributeSet NORMAL_ATTRIBUTES = _getNormalAttributes();
  protected static final SimpleAttributeSet BOLD_ATTRIBUTES = _getBoldAttributes();
  
  
  protected int _numErrors;
  protected JCheckBox _showHighlightsCheckBox;
  
  
  
  protected SingleDisplayModel _model;
  
  private JScrollPane _scroller;
  
  
  private JPanel _leftPanel;
  
  
  private JPanel _rightPanel;
  
  private JPanel _errorNavPanel;
  
  private JPanel _errorNavButtonsPanel;
  
  
  protected JPanel customPanel;
  
  private JButton _nextErrorButton;
  private JButton _prevErrorButton;
  
  
  static DefaultHighlighter.DefaultHighlightPainter _listHighlightPainter =
    new DefaultHighlighter.DefaultHighlightPainter(DrJava.getConfig().getSetting(COMPILER_ERROR_COLOR));
  
  protected static final SimpleAttributeSet _getBoldAttributes() {
    SimpleAttributeSet s = new SimpleAttributeSet();
    StyleConstants.setBold(s, true);
    return s;
  }
  
  protected static final SimpleAttributeSet _getNormalAttributes() {
    SimpleAttributeSet s = new SimpleAttributeSet();
    return s;
  }
  
  public ErrorPanel(SingleDisplayModel model, MainFrame frame, String tabString, String labelString) {
    super(frame, tabString);
    _model = model;
    
    _mainPanel.setLayout(new BorderLayout());
    
    _leftPanel = new JPanel(new BorderLayout());
    
    _errorNavPanel = new JPanel(new GridBagLayout());
    
    
    
    _errorNavButtonsPanel = new JPanel(new BorderLayout());
    
    _nextErrorButton = new JButton(MainFrame.getIcon("Down16.gif"));
    _prevErrorButton = new JButton(MainFrame.getIcon("Up16.gif"));
    
    _nextErrorButton.setMargin(new Insets(0,0,0,0));
    _nextErrorButton.setToolTipText("Go to the next error");
    _prevErrorButton.setMargin(new Insets(0,0,0,0));
    _prevErrorButton.setToolTipText("Go to the previous error");
    
    
    
    
    
    
    
    _errorNavButtonsPanel.add(_prevErrorButton, BorderLayout.NORTH);
    _errorNavButtonsPanel.add(_nextErrorButton, BorderLayout.SOUTH);
    _errorNavButtonsPanel.setBorder(new EmptyBorder(18,5,18,5)); 
    
    
    
    
    _errorNavPanel.add(_errorNavButtonsPanel);
    _showHighlightsCheckBox = new JCheckBox( "Highlight source", true);
    
    
    
    
    
    _scroller = new BorderlessScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    _leftPanel.add(_scroller, BorderLayout.CENTER);
    _leftPanel.add(_errorNavPanel, BorderLayout.EAST);
    
    customPanel = new JPanel(new BorderLayout());
    _rightPanel = new JPanel(new BorderLayout());
    _rightPanel.setBorder(new EmptyBorder(0,5,0,5)); 
    
    _rightPanel.add(new JLabel(labelString, SwingConstants.LEFT), BorderLayout.NORTH);
    _rightPanel.add(customPanel, BorderLayout.CENTER);
    _rightPanel.add(_showHighlightsCheckBox, BorderLayout.SOUTH);
    
    _mainPanel.add(_leftPanel, BorderLayout.CENTER);
    _mainPanel.add(_rightPanel, BorderLayout.EAST);
  }
  
  protected void setErrorListPane(final ErrorListPane elp) {
    _scroller.setViewportView(elp);
    _nextErrorButton.setEnabled(false);
    _nextErrorButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        elp.nextError();
        
        
      }
    });
    _prevErrorButton.setEnabled(false);
    _prevErrorButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        elp.prevError();
        
        
      }
    });
  }
  
  
  public void setListFont(Font f) {
    SimpleAttributeSet set = new SimpleAttributeSet();
    StyleConstants.setFontFamily(set, f.getFamily());
    StyleConstants.setFontSize(set, f.getSize());
    StyleConstants.setBold(set, f.isBold());
    StyleConstants.setItalic(set, f.isItalic());
    
    _updateStyles(set);
    
    getErrorListPane().setFont(f);
    
    SwingDocument doc = (SwingDocument) getErrorListPane().getDocument();
    if (doc instanceof SwingDocument) {
      doc.acquireWriteLock();
      try { ((SwingDocument)doc).setCharacterAttributes(0, doc.getLength() + 1, set, false); }
      finally { doc.releaseWriteLock(); }
    }
  }
  
  
  protected void _updateStyles(AttributeSet newSet) {
    NORMAL_ATTRIBUTES.addAttributes(newSet);
    BOLD_ATTRIBUTES.addAttributes(newSet);
    StyleConstants.setBold(BOLD_ATTRIBUTES, true);  
  }
  
  abstract protected ErrorListPane getErrorListPane();
  
  protected SingleDisplayModel getModel() {
    return _model;
  }
  
  
  abstract protected CompilerErrorModel getErrorModel();
  
  
  public abstract class ErrorListPane extends JEditorPane {
    
    
    private int _selectedIndex;
    
    
    protected Position[] _errorListPositions;
    
    
    protected final Hashtable<Position, CompilerError> _errorTable = new Hashtable<Position, CompilerError>();
    
    
    private HighlightManager.HighlightInfo _listHighlightTag = null;
    
    private HighlightManager _highlightManager = new HighlightManager(this);
    
    protected MouseAdapter defaultMouseListener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        selectNothing();
      }
      public void mouseReleased(MouseEvent e) {
        CompilerError error = _errorAtPoint(e.getPoint());
        
        if (_isEmptySelection() && error != null) getErrorListPane().switchToError(error);
        else  selectNothing();
      }
    };
    



    
    
    public ErrorListPane() {


 
      setContentType("text/rtf");
      setDocument(new SwingDocument());
      
      addMouseListener(defaultMouseListener);
      
      _selectedIndex = 0;
      _errorListPositions = new Position[0];
        
      this.setFont(new Font("Courier", 0, 20));
      
      
      
      
      
      
      setEditable(false);
      
      DrJava.getConfig().addOptionListener(COMPILER_ERROR_COLOR,
                                           new CompilerErrorColorOptionListener());
      
      
      StyleConstants.setForeground(NORMAL_ATTRIBUTES,
                                   DrJava.getConfig().getSetting
                                     (DEFINITIONS_NORMAL_COLOR));
      StyleConstants.setForeground(BOLD_ATTRIBUTES,
                                   DrJava.getConfig().getSetting
                                     (DEFINITIONS_NORMAL_COLOR));
      setBackground(DrJava.getConfig().getSetting(DEFINITIONS_BACKGROUND_COLOR));
      
      
      DrJava.getConfig().addOptionListener(DEFINITIONS_NORMAL_COLOR,
                                           new ForegroundColorListener());
      DrJava.getConfig().addOptionListener(DEFINITIONS_BACKGROUND_COLOR,
                                           new BackgroundColorListener());
      
      
      _showHighlightsCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          DefinitionsPane lastDefPane = _frame.getCurrentDefPane();
          
          if (e.getStateChange() == ItemEvent.DESELECTED) {
            lastDefPane.removeErrorHighlight();
          }
          
          else if (e.getStateChange() == ItemEvent.SELECTED) {   
            getErrorListPane().switchToError(getSelectedIndex());




          }
        }
      });
    }     
    
    
    public boolean shouldShowHighlightsInSource() { return _showHighlightsCheckBox.isSelected(); }
    
    
    public int getSelectedIndex() { return _selectedIndex; }
    
    
    protected CompilerError _errorAtPoint(Point p) {
      int modelPos = viewToModel(p);
      
      if (modelPos == -1) return null;
      
      
      int errorNum = -1;
      for (int i = 0; i < _errorListPositions.length; i++) {
        if (_errorListPositions[i].getOffset() <= modelPos)  errorNum = i;
        else break; 
      }
      
      if (errorNum >= 0) return _errorTable.get(_errorListPositions[errorNum]);
      return null;
    }
    
    
    private int _getIndexForError(CompilerError error) {
      
      if (error == null) throw new IllegalArgumentException("Couldn't find index for null error");
      
      for (int i = 0; i < _errorListPositions.length; i++) {
        CompilerError e= _errorTable.get(_errorListPositions[i]);
        if (error.equals(e))  return i;
      }
      
      throw new IllegalArgumentException("Couldn't find index for error " + error);
    }
    
    
    protected boolean _isEmptySelection() { return getSelectionStart() == getSelectionEnd(); }
    
    
    protected void updateListPane(boolean done) {
      try {
        _errorListPositions = new Position[_numErrors];
        _errorTable.clear();
        
        if (_numErrors == 0) _updateNoErrors(done);
        else _updateWithErrors();
      }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
      
      

    }
    
    abstract protected void _updateNoErrors(boolean done) throws BadLocationException;
    
    abstract protected void _updateWithErrors() throws BadLocationException;
    
    
    protected String _getNumErrorsMessage(String failureName, String failureMeaning) {
      StringBuffer numErrMsg;
      
      
      int numCompErrs = getErrorModel().getNumCompErrors();
      int numWarnings = getErrorModel().getNumWarnings();     
      
      if (!getErrorModel().hasOnlyWarnings()) {
        numErrMsg = new StringBuffer(numCompErrs + " " + failureName);   
        if (numCompErrs > 1) numErrMsg.append("s");
        if (numWarnings > 0) numErrMsg.append(" and " + numWarnings + " warning");          
      }
      
      else  numErrMsg = new StringBuffer(numWarnings + " warning"); 
      
      if (numWarnings > 1) numErrMsg.append("s");
     
      numErrMsg.append(" " + failureMeaning + ":\n");
      return numErrMsg.toString();
    }
    
    
    protected String _getErrorTitle() {
      CompilerErrorModel cem = getErrorModel();
      if (cem.getNumCompErrors() > 1)
        return "--------------\n*** Errors ***\n--------------\n";
      if (cem.getNumCompErrors() > 0)
        return "-------------\n*** Error ***\n-------------\n";
      return "";
    }
      
    
    protected String _getWarningTitle() {
      CompilerErrorModel cem = getErrorModel();
      if (cem.getNumWarnings() > 1)
        return "--------------\n** Warnings **\n--------------\n";
      if (cem.getNumWarnings() > 0)
        return "-------------\n** Warning **\n-------------\n";
      return "";
    }
    
    
    
    
    protected void _updateWithErrors(String failureName, String failureMeaning, SwingDocument doc)
      throws BadLocationException {
      
      String numErrsMsg = _getNumErrorsMessage(failureName, failureMeaning);
      doc.append(numErrsMsg, BOLD_ATTRIBUTES);
      
      _insertErrors(doc);
      setDocument(doc);
      
      
      if (!getErrorModel().hasOnlyWarnings())
        getErrorListPane().switchToError(0);
    }
    
    
    public boolean hasNextError() { return this.getSelectedIndex() + 1 < _numErrors; }
    
    
    public boolean hasPrevError() { return this.getSelectedIndex() > 0; }
    
    
    public void nextError() {
      
      if (hasNextError()) {
        this._selectedIndex += 1;

        getErrorListPane().switchToError(this.getSelectedIndex());
      }
    }
    
    
    public void prevError() {
      
      if (hasPrevError()) {
        this._selectedIndex -= 1;
        getErrorListPane().switchToError(this.getSelectedIndex());
      }
    }
    
    
    protected void _insertErrors(SwingDocument doc) throws BadLocationException {
      CompilerErrorModel cem = getErrorModel();
      int numErrors = cem.getNumErrors();
      
      
      
      
      int errorPositionInListOfErrors = 0;
      
      
      String errorTitle = _getErrorTitle();
      if (cem.getNumWarnings() > 0)   
        doc.append(errorTitle, BOLD_ATTRIBUTES);
      
      for (int errorNum = 0; errorNum < numErrors; errorNum++) {
        int startPos = doc.getLength();
        CompilerError err = cem.getError(errorNum);
        
        if (!err.isWarning()){
          _insertErrorText(err, doc);
          Position pos = doc.createPosition(startPos);
          _errorListPositions[errorPositionInListOfErrors] = pos;
          _errorTable.put(pos, err);
          errorPositionInListOfErrors++;
        }
      }
      
      String warningTitle = _getWarningTitle();
      if (cem.getNumCompErrors() > 0)   
        doc.append(warningTitle, BOLD_ATTRIBUTES);
      
      for (int errorNum = 0; errorNum < numErrors; errorNum++) {
        int startPos = doc.getLength();
        CompilerError err = cem.getError(errorNum);
        
        if (err.isWarning()){
          _insertErrorText(err, doc);
          Position pos = doc.createPosition(startPos);
          _errorListPositions[errorPositionInListOfErrors] = pos;
          _errorTable.put(pos, err);
          errorPositionInListOfErrors++;
        }
      }      
    }
    
    
    protected void _insertErrorText(CompilerError error, SwingDocument doc) throws BadLocationException {
      
      doc.append("File: ", BOLD_ATTRIBUTES);
      String fileAndLineNumber = error.getFileMessage() + "  [line: " + error.getLineMessage() + "]";
      doc.append(fileAndLineNumber + "\n", NORMAL_ATTRIBUTES);
      
      if (error.isWarning()) doc.append(_getWarningText(), BOLD_ATTRIBUTES);
      else doc.append(_getErrorText(), BOLD_ATTRIBUTES);
      
      doc.append(error.message(), NORMAL_ATTRIBUTES);
      doc.append("\n", NORMAL_ATTRIBUTES);
    }
    
    
    protected String _getWarningText() { return "Warning: "; }
    
    
    protected String _getErrorText() { return "Error: "; }
    
    
    protected void _removeListHighlight() {
      if (_listHighlightTag != null) {
        _listHighlightTag.remove();
        _listHighlightTag = null;
      }
      
      
    }
    
    
    public void selectNothing() {
      
      _removeListHighlight();
      
      
      _frame.getCurrentDefPane().removeErrorHighlight();
    }
    
    
    public void selectItem(CompilerError error) {

      try {
        
        int i = _getIndexForError(error);
        
        _selectedIndex = i;

        _removeListHighlight();
        
        int startPos = _errorListPositions[i].getOffset();

        
        
        
        
        int endPos;
        if (i + 1 >= (_numErrors)) endPos = getDocument().getLength();   
        else { 
          endPos = _errorListPositions[i + 1].getOffset();

          CompilerError nextError = _errorTable.get(_errorListPositions[i+1]);

          if (!error.isWarning() && nextError.isWarning()) endPos = endPos - _getWarningTitle().length();

        }            
        

        
        try {
          _listHighlightTag = _highlightManager.addHighlight(startPos, endPos, _listHighlightPainter);
          
          
          
          Rectangle startRect;
          if (i == 0)  startRect = modelToView(0);
          
          else startRect = modelToView(startPos);
          
          Rectangle endRect = modelToView(endPos - 1);
          
          if (startRect != null && endRect != null) {
            
            
            startRect.add(endRect);
            
            
            
            scrollRectToVisible(startRect);
            _updateScrollButtons();
          }
          else {

            
            _removeListHighlight();
          }
        }
        catch (BadLocationException badBadLocation) { }
        
      }
      catch (IllegalArgumentException iae) {
        
        
        
        _removeListHighlight();
      }
    }
    
    protected void _updateScrollButtons() {
      if (hasNextError()) {
        _nextErrorButton.setEnabled(true);
      }
      else {
        _nextErrorButton.setEnabled(false);
      }
      if (hasPrevError()) {
        _prevErrorButton.setEnabled(true);
      }
      else {
        _prevErrorButton.setEnabled(false);
      }
    }
    
    
    void switchToError(CompilerError error) {

      if (error == null) return;
      
      SingleDisplayModel model = getModel();
      
      DefinitionsPane prevPane = _frame.getCurrentDefPane();
      prevPane.removeErrorHighlight();  
      OpenDefinitionsDocument prevDoc = prevPane.getOpenDefDocument();
      
      if (error.file() != null) {
        try {
          OpenDefinitionsDocument doc = model.getDocumentForFile(error.file());
          CompilerErrorModel errorModel = getErrorModel();
          
          Position pos = errorModel.getPosition(error); 

          

          
          if (! prevDoc.equals(doc)) model.setActiveDocument(doc);
          else model.refreshActiveDocument();
          

          
          DefinitionsPane defPane = _frame.getCurrentDefPane();
          
          if (pos != null) {
            int errPos = pos.getOffset();
            if (errPos >= 0 && errPos <= doc.getLength()) {
              defPane.centerViewOnOffset(errPos);
              
              
              defPane.getErrorCaretListener().updateHighlight(errPos);
            }
            
          }
          
          
          
          

          defPane.requestFocusInWindow();
          defPane.getCaret().setVisible(true);
        }
        catch (IOException ioe) {
          
        }
      }

      
      getErrorListPane().selectItem(error); 
    }
    
    
    
    void switchToError(int index) {
      if ((index >= 0) && (index < _errorListPositions.length)) {
        Position pos = _errorListPositions[index];
        CompilerError error= _errorTable.get(pos);
        switchToError(error);
      }
    }
    
    
    private class CompilerErrorColorOptionListener implements OptionListener<Color> {
      
      public void optionChanged(OptionEvent<Color> oce) {
        _listHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(oce.value);
        if (_listHighlightTag != null) {
          _listHighlightTag.refresh(_listHighlightPainter);
        }
      }
    }
    
    
    private class ForegroundColorListener implements OptionListener<Color> {
      public void optionChanged(OptionEvent<Color> oce) {
        StyleConstants.setForeground(NORMAL_ATTRIBUTES, oce.value);
        StyleConstants.setForeground(BOLD_ATTRIBUTES, oce.value);
        
        
        Document doc = getErrorListPane().getDocument();
        if (doc instanceof SwingDocument) {
          SimpleAttributeSet set = new SimpleAttributeSet();
          set.addAttribute(StyleConstants.Foreground, oce.value);
          SwingDocument sdoc = (SwingDocument) doc;
          sdoc.acquireWriteLock();
          try { sdoc.setCharacterAttributes(0, sdoc.getLength(), set, false); }
          finally { sdoc.releaseWriteLock(); }
        
        }
      }
    }
    
    
    private class BackgroundColorListener implements OptionListener<Color> {
      public void optionChanged(OptionEvent<Color> oce) {
        setBackground(oce.value);
        ErrorListPane.this.repaint();
      }
    }
  }
}