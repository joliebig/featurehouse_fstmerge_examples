

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.drjava.platform.PlatformFactory;


public class DrJavaErrorWindow extends JDialog {
  
  public static final String SF_ADD_BUG_URL = "http://sourceforge.net/tracker/?func=add&group_id=44253&atid=438935/";  

  
  public static final String SF_LINK_NAME = "http://sourceforge.net/projects/drjava";
  
  
  private JEditorPane _errorInfo;
  
  private JTextArea _stackTrace;
  
  private JLabel _indexLabel;
  
  private JScrollPane _stackTraceScroll;
  
  private JPanel _bottomPanel;
  
  private JPanel _buttonPanel;
  
  private JButton _copyButton;
  
  private JButton _closeButton;
  
  private JButton _nextButton;
  
  private JButton _prevButton;
  
  private JButton _dismissButton;
  
  private int _errorCount;
  
  private Throwable _error;
  
  private int _errorIndex;
  
  private static JFrame _parentFrame = new JFrame();
  
  private static boolean _parentChanged = true;
  
  
  public static void setFrame(JFrame f) { _parentFrame = f; _parentChanged = true; }
  
  
  public static JFrame getFrame() { return _parentFrame; }
  
  
  private static DrJavaErrorWindow _singletonInstance;
  
  
  public static synchronized DrJavaErrorWindow singleton() {
    if (_parentChanged) {
      _singletonInstance = new DrJavaErrorWindow();
      _parentChanged = false;
    }
    return _singletonInstance;
  }
  
  
  private DrJavaErrorWindow() {
    super(_parentFrame, "DrJava Errors");

    this.setSize(600,400);
    setLocationRelativeTo(_parentFrame);

    
    
    _stackTrace = new JTextArea();
    _stackTrace.setEditable(false);

    _prevButton = new JButton(_prevAction);
    _nextButton = new JButton(_nextAction);
    _copyButton = new JButton(_copyAction);
    _dismissButton = new JButton(_dismissAction);
    _closeButton = new JButton(_closeAction);

    _bottomPanel = new JPanel(new BorderLayout());
    _buttonPanel = new JPanel();
    _buttonPanel.add(_prevButton);
    _buttonPanel.add(_nextButton);
    _buttonPanel.add(_copyButton);
    _buttonPanel.add(_dismissButton);
    _buttonPanel.add(_closeButton);
    _indexLabel = new JLabel();
    _bottomPanel.add(_indexLabel, BorderLayout.CENTER);
    _bottomPanel.add(_buttonPanel, BorderLayout.EAST);

    _stackTraceScroll = new BorderlessScrollPane(_stackTrace,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    _errorInfo = _errorInfo = new JEditorPane("text/html", HEADER_HTML+NO_ERRORS_HTML);
    _errorInfo.setEditable(false);
    _errorInfo.setBackground(getContentPane().getBackground());    
    JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    cp.add(_errorInfo, BorderLayout.NORTH);
    cp.add(_stackTraceScroll, BorderLayout.CENTER);
    cp.add(_bottomPanel, BorderLayout.SOUTH);    
    getRootPane().setDefaultButton(_closeButton);
    init();
  }
  
  
  public void setVisible(boolean b) {
    if (b) {
      init();
    }
    super.setVisible(b);
  }
  
  
  private void init() {
    _errorCount = DrJavaErrorHandler.getErrorCount();
    if (_errorCount>0) {
      _error = DrJavaErrorHandler.getError(0);
      _errorIndex = 0;
    }
    else {
      _error = null;
      _errorIndex = -1;
    }
    _prevAction.setEnabled(false);
    _nextAction.setEnabled(_errorCount>1);
    _dismissAction.setEnabled(_errorCount>0);
    _copyAction.setEnabled(_errorCount>0);
    updateErrorInfo();
  }

  
  private void updateErrorInfo() {
    getContentPane().remove(_errorInfo);
    if (_error!=null) {
      StringBuilder b = new StringBuilder();
      if (_error instanceof DrJavaErrorHandler.LoggedCondition) {
        b.append("Logged condition: ");
        b.append(_error.getMessage());
        b.append('\n');
        boolean first = true;
        for(StackTraceElement ste: _error.getStackTrace()) {
          if (first) { first = false; continue;  }
          b.append("\tat ");
          b.append(ste);
          b.append('\n');
        }
      }
      else {
        b.append(StringOps.getStackTrace(_error));
        if (_error instanceof UnexpectedException) {
          Throwable t = ((UnexpectedException)_error).getCause();
          b.append("\nCaused by:\n");
          b.append(StringOps.getStackTrace(t));
        }
      }
      
      b.append("\n\nSystem Properties:\n");
      b.append("DrJava Version ");
      b.append(edu.rice.cs.drjava.Version.getBuildTimeString());
      b.append("\n");
      java.util.Properties props = System.getProperties();
      int size = props.size();
      java.util.Iterator entries = props.entrySet().iterator();
      while(entries.hasNext()) {
        java.util.Map.Entry entry = (java.util.Map.Entry)entries.next();
        b.append(entry.getKey());
        b.append(" = ");
        if (entry.getKey().equals("line.separator")) {
          b.append("\"");
          String ls = (String)entry.getValue();
          for(int i=0; i<ls.length(); ++i) {
            int ch = ls.charAt(i);
            b.append("\\u");
            String hexString = "0000"+Integer.toHexString(ch);
            b.append(hexString.substring(hexString.length()-4));
          }
          b.append("\"");
        }
        else {
          b.append(entry.getValue());
        }
        b.append("\n");
      }

      _stackTrace.setText(b.toString());
      _stackTrace.setCaretPosition(0);
      
      b = new StringBuilder();
      b.append(HEADER_HTML);
      b.append(_errorCount);
      b.append(" error");
      b.append(((_errorCount>1)?"s":""));
      b.append(" occured!<br>");
      b.append(ERRORS_FOOTER_HTML);
      _errorInfo = new JEditorPane("text/html", b.toString());
      _errorInfo.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              PlatformFactory.ONLY.openURL(e.getURL());
            } catch(Exception ex) {  }
          }
        }
      });
      _errorInfo.setEditable(false);
      _errorInfo.setBackground(getContentPane().getBackground());
      _indexLabel.setText("Error "+(_errorIndex+1)+" of "+(_errorCount));
    }
    else {
      _errorInfo = new JEditorPane("text/html", HEADER_HTML+NO_ERRORS_HTML);
      _errorInfo.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              PlatformFactory.ONLY.openURL(e.getURL());
            } catch(Exception ex) {  }
          }
        }
      });
      _errorInfo.setEditable(false);
      _errorInfo.setBackground(getContentPane().getBackground());
      _stackTrace.setText("");
      _indexLabel.setText("");
    }
    getContentPane().add(_errorInfo, BorderLayout.NORTH);
    validate();
  }
  
  
  private Action _closeAction = new AbstractAction("Close") {
    public void actionPerformed(ActionEvent e) {
      DrJavaErrorWindow.this.dispose();
    }
  };
  
  
  private Action _prevAction = new AbstractAction("Previous") {
    public void actionPerformed(ActionEvent e) {
      if (_errorIndex>0) {
        --_errorIndex;
        _error = DrJavaErrorHandler.getError(_errorIndex);
        if (_errorIndex==0) { setEnabled(false); }
        if (_errorCount>1) { _nextAction.setEnabled(true); }
        updateErrorInfo();
      }
    }
  };
  
  
  private Action _nextAction = new AbstractAction("Next") {
    public void actionPerformed(ActionEvent e) {
      if (_errorIndex<_errorCount-1) {
        ++_errorIndex;
        _error = DrJavaErrorHandler.getError(_errorIndex);
        if (_errorIndex==_errorCount-1) { setEnabled(false); }
        if (_errorCount>1) { _prevAction.setEnabled(true); }
        updateErrorInfo();
      }
    }
  };
  
  
  private Action _dismissAction = new AbstractAction("Dismiss") {
    public void actionPerformed(ActionEvent e) {
      DrJavaErrorHandler.clearErrors();
      _errorCount = 0;
      _error = null;
      _errorIndex = -1;
      setEnabled(false);
      _prevAction.setEnabled(false);
      _nextAction.setEnabled(false);
      _copyAction.setEnabled(false);
      updateErrorInfo();
      JButton errorsButton = DrJavaErrorHandler.getButton();
      if (errorsButton!=null) { errorsButton.setVisible(false); }
      _closeAction.actionPerformed(e);
    }
  };

  
  private Action _copyAction = new AbstractAction("Copy This Error") {
    public void actionPerformed(ActionEvent e) {
      _stackTrace.grabFocus();
      _stackTrace.getActionMap().get(DefaultEditorKit.selectAllAction).actionPerformed(e);
      _stackTrace.getActionMap().get(DefaultEditorKit.copyAction).actionPerformed(e);
    }
  };

  
  private final String HEADER_HTML =
    "<html><font size=\"-1\" face=\"sans-serif, Arial, Helvetica, Geneva\"><b>";
  private final String ERRORS_FOOTER_HTML = 
    "Please submit a bug report containing the information below " +
    "and an account of the actions that caused the bug (if known) to " +
    "<a href=\"" + SF_ADD_BUG_URL + "\"><b>" + SF_LINK_NAME + "</b></a>.<br>" +
    "You may wish to save all your work and restart DrJava.<br>" +
    "Thanks for your help in making DrJava better!</b></font></p></html>";
  private final String NO_ERRORS_HTML =
    "No errors occurred!<br>" +
    "Thanks for using DrJava!</b></font></p></html>";
}