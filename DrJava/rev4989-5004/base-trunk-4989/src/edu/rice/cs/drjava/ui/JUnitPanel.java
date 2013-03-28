

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.drjava.model.junit.JUnitError;
import edu.rice.cs.drjava.model.junit.JUnitErrorModel;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.util.text.SwingDocument;
import edu.rice.cs.util.swing.RightClickMouseAdapter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;


public class JUnitPanel extends ErrorPanel {
  private static final String START_JUNIT_MSG = "Testing in progress.  Please wait ...\n";
  private static final String JUNIT_FINISHED_MSG = "All tests completed successfully.\n";
  private static final String NO_TESTS_MSG = "";
  
  private static final SimpleAttributeSet OUT_OF_SYNC_ATTRIBUTES = _getOutOfSyncAttributes();
  private static final SimpleAttributeSet _getOutOfSyncAttributes() {
    SimpleAttributeSet s = new SimpleAttributeSet();
    s.addAttribute(StyleConstants.Foreground, Color.red.darker());
    s.addAttribute(StyleConstants.Bold, Boolean.TRUE);
    return s;
  }
  
  private static final SimpleAttributeSet TEST_PASS_ATTRIBUTES = _getTestPassAttributes();
  private static final SimpleAttributeSet _getTestPassAttributes() {
    SimpleAttributeSet s = new SimpleAttributeSet();
    s.addAttribute(StyleConstants.Foreground, Color.green.darker());
    return s;
  }
  
  private static final SimpleAttributeSet TEST_FAIL_ATTRIBUTES = _getTestFailAttributes();
  private static final SimpleAttributeSet _getTestFailAttributes() {
    SimpleAttributeSet s = new SimpleAttributeSet();
    s.addAttribute(StyleConstants.Foreground, Color.red);
    return s;
  }
  
  private static final String TEST_OUT_OF_SYNC =
    "The documents being tested have been modified and should be recompiled!\n";
  
  protected JUnitErrorListPane _errorListPane;
  private final MainFrame _mainFrame;      
  private int _testCount;
  private boolean _testsSuccessful;
  
  private JUnitProgressBar _progressBar;
  
  private Action _showStackTraceAction = new AbstractAction("Show Stack Trace") {
    public void actionPerformed(ActionEvent ae) {
      if (_error != null) {
        _displayStackTrace(_error);
      }
    }
  };
  
  private JButton _showStackTraceButton;
  
  
  private JUnitError _error = null;
  private Window _stackFrame = null;
  private JTextArea _stackTextArea;
  private final JLabel _errorLabel = new JLabel();
  private final JLabel _testLabel = new JLabel();
  private final JLabel _fileLabel = new JLabel();
  
  
  public JUnitPanel(SingleDisplayModel model, MainFrame frame) {
    super(model, frame, "Test Output", "Test Progress");
    _mainFrame = frame;
    _testCount = 0;
    _testsSuccessful = true;
    
    _progressBar = new JUnitProgressBar();
    _progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI());
    _showStackTraceButton = new JButton(_showStackTraceAction);
    customPanel.add(_progressBar, BorderLayout.NORTH);
    customPanel.add(_showStackTraceButton, BorderLayout.SOUTH);
    
    _errorListPane = new JUnitErrorListPane();
    setErrorListPane(_errorListPane);
  }
  
  
  public JUnitErrorListPane getErrorListPane() { return _errorListPane; }
  
  protected JUnitErrorModel getErrorModel() { return getModel().getJUnitModel().getJUnitErrorModel(); }
  
  
  protected void _updateStyles(AttributeSet newSet) {
    super._updateStyles(newSet);
    OUT_OF_SYNC_ATTRIBUTES.addAttributes(newSet);
    StyleConstants.setBold(OUT_OF_SYNC_ATTRIBUTES, true);  
    TEST_PASS_ATTRIBUTES.addAttributes(newSet);
    TEST_FAIL_ATTRIBUTES.addAttributes(newSet);
  }
  
  
  public void setJUnitInProgress() {
    _errorListPane.setJUnitInProgress();
  }
  
  
  protected void _close() {
    super._close();
    getModel().getJUnitModel().resetJUnitErrors();
    reset();
  }
  
  
  public void reset() {
    JUnitErrorModel juem = getModel().getJUnitModel().getJUnitErrorModel();
    boolean testsHaveRun = false;
    if (juem != null) {
      _numErrors = juem.getNumErrors();
      testsHaveRun = juem.haveTestsRun();
    } 
    else _numErrors = 0;
    _errorListPane.updateListPane(testsHaveRun); 
    repaint();
  }
  
  
  public void progressReset(int numTests) {
    _progressBar.reset();
    _progressBar.start(numTests);
    _testsSuccessful = true;
    _testCount = 0;
  }
  
  
  public void progressStep(boolean successful) {
    _testCount++;
    _testsSuccessful &= successful;
    _progressBar.step(_testCount, _testsSuccessful);
  }
  
  public void testStarted(String className, String testName) { }
  
  private void _displayStackTrace (JUnitError e) {
    _errorLabel.setText((e.isWarning() ? "Error: " : "Failure: ") +
                        e.message());
    _fileLabel.setText("File: "+(new File(e.fileName())).getName());
    if (!e.testName().equals("")) {
      _testLabel.setText("Test: "+e.testName());
    }
    else {
      _testLabel.setText("");
    }
    _stackTextArea.setText(e.toString());
    _stackTextArea.setCaretPosition(0);
    _frame.setPopupLoc(_stackFrame);
    _stackFrame.setVisible(true);
  }
  
  
  public class JUnitErrorListPane extends ErrorPanel.ErrorListPane {
    private JPopupMenu _popMenu;
    private String _runningTestName;
    private boolean _warnedOutOfSync;
    private static final String JUNIT_WARNING = "junit.framework.TestSuite$1.warning";
    
    
    private final HashMap<String, Position> _runningTestNamePositions;
    
    
    public JUnitErrorListPane() {
      removeMouseListener(defaultMouseListener);
      _popMenu = new JPopupMenu();
      _popMenu.add(_showStackTraceAction);
      _error = null;
      _setupStackTraceFrame();
      addMouseListener(new PopupAdapter());
      _runningTestName = null;
      _runningTestNamePositions = new HashMap<String, Position>();
      _showStackTraceButton.setEnabled(false);
    }
    
    private String _getTestFromName(String name) {
      int paren = name.indexOf('(');
      
      if ((paren > -1) && (paren < name.length())) return name.substring(0, paren);
      
      else throw new IllegalArgumentException("Name does not contain any parens: " + name);
    }
    
    private String _getClassFromName(String name) {
      int paren = name.indexOf('(');
      
      if ((paren > -1) && (paren < name.length())) return name.substring(paren + 1, name.length() - 1);
      else throw new IllegalArgumentException("Name does not contain any parens: " + name);
    }
    
    
    public void testStarted(String name) {
      String testName = _getTestFromName(name);
      String className = _getClassFromName(name);
      String fullName = className + "." + testName;
      if (fullName.equals(JUNIT_WARNING)) return;
      SwingDocument doc = getSwingDocument();
      try {
        int len = doc.getLength();
        
        if (! className.equals(_runningTestName)) {
          _runningTestName = className;
          doc.insertString(len, "  " + className + "\n", NORMAL_ATTRIBUTES);
          len = doc.getLength();
        }
        
        
        doc.insertString(len, "    ", NORMAL_ATTRIBUTES);
        len = doc.getLength();
        doc.insertString(len, testName + "\n", NORMAL_ATTRIBUTES);
        Position pos = doc.createPosition(len);
        _runningTestNamePositions.put(fullName, pos);
        setCaretPosition(len);
      }
      catch (BadLocationException ble) {
        
        throw new UnexpectedException(ble);
      }
    }
    
    
    public void testEnded(String name, boolean wasSuccessful, boolean causedError) {
      String testName = _getTestFromName(name);
      String fullName = _getClassFromName(name) + "." + testName;
      if (fullName.equals(JUNIT_WARNING)) return;
      
      SwingDocument doc = getSwingDocument();
      Position namePos = _runningTestNamePositions.get(fullName);
      AttributeSet set;
      if (! wasSuccessful || causedError) set = TEST_FAIL_ATTRIBUTES;
      else set = TEST_PASS_ATTRIBUTES;
      if (namePos != null) {
        int index = namePos.getOffset();
        int length = testName.length();
        doc.setCharacterAttributes(index, length, set, false);
      }
    }
    
    
    public void setJUnitInProgress() {
      assert EventQueue.isDispatchThread();
      _errorListPositions = new Position[0];
      progressReset(0);
      _runningTestNamePositions.clear();
      _runningTestName = null;
      _warnedOutOfSync = false;
      
      SwingDocument doc = new SwingDocument();

      
      doc.append(START_JUNIT_MSG, BOLD_ATTRIBUTES);
      setDocument(doc);
      selectNothing();
    }
    
    
    protected void _updateWithErrors() throws BadLocationException {
      
      SwingDocument doc = getSwingDocument();

      _updateWithErrors("test", "failed", doc);
    }
    
    
    protected String _getNumErrorsMessage(String failureName, String failureMeaning) {
      StringBuilder numErrMsg;
      
      
      int numCompErrs = getErrorModel().getNumCompErrors();
      int numWarnings = getErrorModel().getNumWarnings();     
      
      if (! getErrorModel().hasOnlyWarnings()) {
        numErrMsg = new StringBuilder(numCompErrs + " " + failureName);   
        if (numCompErrs > 1) numErrMsg.append("s");
        numErrMsg.append(" " + failureMeaning);
        if (numWarnings > 0) numErrMsg.append(" and " + numWarnings + " warning");        
      }
      else  numErrMsg = new StringBuilder(numWarnings + " warning");       
      
      if (numWarnings > 1) numErrMsg.append("s");
      if (numWarnings > 0) numErrMsg.append(" found");
      
      numErrMsg.append(":\n");
      
      return numErrMsg.toString();
    }
    
    protected void _updateWithErrors(String failureName, String failureMeaning, SwingDocument doc)
      throws BadLocationException {
      
      _replaceInProgressText(_getNumErrorsMessage(failureName, failureMeaning));
      
      _insertErrors(doc);
      
      
      switchToError(0);
    }
    
    
    public void _replaceInProgressText(String msg) throws BadLocationException {
      assert ! _mainFrame.isVisible() || EventQueue.isDispatchThread();
      int start = 0;
      if (_warnedOutOfSync) { start = TEST_OUT_OF_SYNC.length(); }
      int len = START_JUNIT_MSG.length();
      SwingDocument doc = getSwingDocument();
      if (doc.getLength() >= len + start) {
        doc.remove(start, len);
        doc.insertString(start, msg, BOLD_ATTRIBUTES);
      }
    }
    
    
    protected String _getWarningText() {  return "Error: "; }
    
    
    protected String _getErrorText() { return "Failure: "; }
    
    
    protected void _updateNoErrors(boolean haveTestsRun) throws BadLocationException {
      

      _replaceInProgressText(haveTestsRun ? JUNIT_FINISHED_MSG : NO_TESTS_MSG);
      
      selectNothing();
      setCaretPosition(0);
    }
    



















    
    private void _setupStackTraceFrame() {
      
      JDialog _dialog = new JDialog(_frame,"JUnit Error Stack Trace",false);
      _stackFrame = _dialog;
      _stackTextArea = new JTextArea();
      _stackTextArea.setEditable(false);
      _stackTextArea.setLineWrap(false);
      JScrollPane scroll = new BorderlessScrollPane(_stackTextArea,
                                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      
      ActionListener closeListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          _stackFrame.setVisible(false);
        }
      };
      JButton closeButton = new JButton("Close");
      closeButton.addActionListener(closeListener);
      JPanel closePanel = new JPanel(new BorderLayout());
      closePanel.setBorder(new EmptyBorder(5,5,0,0));
      closePanel.add(closeButton, BorderLayout.EAST);
      JPanel cp = new JPanel(new BorderLayout());
      _dialog.setContentPane(cp);
      cp.setBorder(new EmptyBorder(5,5,5,5));
      cp.add(scroll, BorderLayout.CENTER);
      cp.add(closePanel, BorderLayout.SOUTH);
      JPanel topPanel = new JPanel(new GridLayout(0,1,0,5));
      topPanel.setBorder(new EmptyBorder(0,0,5,0));
      topPanel.add(_fileLabel);
      topPanel.add(_testLabel);
      topPanel.add(_errorLabel);
      cp.add(topPanel, BorderLayout.NORTH);
      _dialog.setSize(600, 500);
      
    }
    
    
    public void selectItem(DJError error) {
      super.selectItem(error);
      _error = (JUnitError) error;
      _showStackTraceButton.setEnabled(true);
    }
    
    
    
    protected void _removeListHighlight() {
      super._removeListHighlight();
      _showStackTraceButton.setEnabled(false);
    }
    









    
    private class PopupAdapter extends RightClickMouseAdapter {
      
      public void mousePressed(MouseEvent e) {
        if (_selectError(e)) {
          super.mousePressed(e);
        }
      }
      
      
      public void mouseReleased(MouseEvent e) {
        if (_selectError(e)) super.mouseReleased(e);
      }
      
      
      private boolean _selectError(MouseEvent e) {
        
        _error = (JUnitError)_errorAtPoint(e.getPoint());
        
        if (_isEmptySelection() && _error != null) {
          _errorListPane.switchToError(_error);
          return true;
        }
        else {
          selectNothing();
          return false;
        }
      }
      
      
      protected void _popupAction(MouseEvent e) { _popMenu.show(e.getComponent(), e.getX(), e.getY()); }
    }
  }
  
  
  
  static class JUnitProgressBar extends JProgressBar {
    private boolean _hasError = false;
    
    public JUnitProgressBar() {
      super();
      setForeground(getStatusColor());
    }
    
    private Color getStatusColor() {
      if (_hasError) {
        return Color.red;
      }
      else {
        return Color.green;
      }
    }
    
    public void reset() {
      _hasError = false;
      setForeground(getStatusColor());
      setValue(0);
    }
    
    public void start(int total) {
      setMaximum(total);
      reset();
    }
    
    public void step(int value, boolean successful) {
      setValue(value);
      if (!_hasError && !successful) {
        _hasError= true;
        setForeground(getStatusColor());
      }
    }
  }
}
