

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.swing.BorderlessScrollPane;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;


public class DrJavaErrorWindow extends JDialog {
  
  public static final String SF_ADD_BUG_URL = "http://sourceforge.net/tracker/?func=add&group_id=44253&atid=438935/";  

  
  public static final String SF_LINK_NAME = "http://sourceforge.net/projects/drjava";
  
  
  private volatile JEditorPane _errorInfo;
  
  private final JTextArea _stackTrace;
  
  private final JLabel _indexLabel;
  
  private final JScrollPane _stackTraceScroll;
  
  private final JPanel _bottomPanel;
  
  private final JPanel _buttonPanel;
  
  private final JButton _copyButton;
  
  private final JButton _okButton;
  
  private final JButton _nextButton;
  
  private final JButton _prevButton;
  
  private final JButton _dismissButton;
  
  private volatile int _errorCount;
  
  private volatile Throwable _error;
  
  private volatile int _errorIndex;
  
  private static volatile JFrame _parentFrame = null;
  
  private static volatile boolean _parentChanged = true;
  
  
  public static void setFrame(JFrame f) { _parentFrame = f; _parentChanged = true; }
  
  
  public static JFrame getFrame() { return _parentFrame; }
  
  
  private static volatile DrJavaErrorWindow _singletonInstance;
  
  
  public static DrJavaErrorWindow singleton() {
    if (_parentChanged) {
      synchronized(DrJavaErrorWindow.class) {
        if (_parentChanged) {
          _singletonInstance = new DrJavaErrorWindow();
          _parentChanged = false;
        }
      }
    }
    return _singletonInstance;
  }
  
  
  private DrJavaErrorWindow() {
    super(_parentFrame, "DrJava Errors");

    this.setSize(600,400);

    
    
    _stackTrace = new JTextArea();
    _stackTrace.setEditable(false);

    _prevButton = new JButton(_prevAction);
    _nextButton = new JButton(_nextAction);
    _copyButton = new JButton(_copyAction);
    _dismissButton = new JButton(_dismissAction);
    _okButton = new JButton(_okAction);

    _bottomPanel = new JPanel(new BorderLayout());
    _buttonPanel = new JPanel();
    _buttonPanel.add(_prevButton);
    _buttonPanel.add(_nextButton);
    _buttonPanel.add(_copyButton);
    _buttonPanel.add(_dismissButton);
    _buttonPanel.add(_okButton);
    _indexLabel = new JLabel();
    _bottomPanel.add(_indexLabel, BorderLayout.CENTER);
    _bottomPanel.add(_buttonPanel, BorderLayout.EAST);

    _stackTraceScroll = new BorderlessScrollPane(_stackTrace,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    _errorInfo = new JEditorPane("text/html", HEADER_HTML+NO_ERRORS_HTML);
    _errorInfo.setEditable(false);
    _errorInfo.setBackground(getContentPane().getBackground());    
    final JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    cp.add(_errorInfo, BorderLayout.NORTH);
    cp.add(_stackTraceScroll, BorderLayout.CENTER);
    cp.add(_bottomPanel, BorderLayout.SOUTH);    
    getRootPane().setDefaultButton(_okButton);
    init();
  }

  protected WindowAdapter _windowListener = new WindowAdapter() {
    public void windowDeactivated(WindowEvent we) {
      DrJavaErrorWindow.this.toFront();
    }
    public void windowClosing(WindowEvent we) {
      DrJavaErrorWindow.this.dispose();
      if (DrJavaErrorHandler.getButton() == null) { System.exit(1); }
    }
  };
  
  
  protected final Runnable1<WindowEvent> CANCEL = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) {
      if (DrJavaErrorHandler.getButton() == null) { System.exit(1); }
    }
  };

  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      init();
      if (_parentFrame != null) {
        edu.rice.cs.drjava.DrJavaRoot.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
      }
      toFront();
    }
    else {
      if (_parentFrame != null) {
        edu.rice.cs.drjava.DrJavaRoot.removeModalWindowAdapter(this);
        _parentFrame.toFront();
      }
    }
    super.setVisible(vis);
  }
  
  
  private void init() {
    _errorCount = DrJavaErrorHandler.getErrorCount();
    if (_errorCount > 0) {
      _error = DrJavaErrorHandler.getError(0);
      _errorIndex = 0;
    }
    else {
      _error = null;
      _errorIndex = -1;
    }
    _prevAction.setEnabled(false);
    _nextAction.setEnabled(_errorCount>1);
    _dismissAction.setEnabled(_errorCount > 0);
    _copyAction.setEnabled(_errorCount > 0);
    updateErrorInfo();
  }

  
  private void updateErrorInfo() {
    getContentPane().remove(_errorInfo);
    if (_error != null) {
      final StringBuilder b = new StringBuilder();
      if (_error instanceof DrJavaErrorHandler.LoggedCondition) {
        b.append("Logged condition: ");
        b.append(_error.getMessage());
        b.append('\n');
        boolean first = true;
        for (StackTraceElement ste: _error.getStackTrace()) {
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
      
      b.append("\n\n");
      b.append(getSystemAndDrJavaInfo());

      _stackTrace.setText(b.toString());
      _stackTrace.setCaretPosition(0);
      
      final StringBuilder b2 = new StringBuilder();
      b2.append(HEADER_HTML);
      b2.append(_errorCount);
      b2.append(" error");
      b2.append(((_errorCount>1)?"s":""));
      b2.append(" occured!<br>");
      b2.append(ERRORS_FOOTER_HTML);
      _errorInfo = new JEditorPane("text/html", b2.toString());
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
      _indexLabel.setText("Error " + (_errorIndex+1) + " of " + (_errorCount));
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
  
  
  public static String getSystemAndDrJavaInfo() {
    final StringBuilder b = new StringBuilder();
    b.append("System Properties:\n");
    b.append("DrJava Version ");
    b.append(edu.rice.cs.drjava.Version.getVersionString());
    b.append('\n');
    b.append("DrJava Build Time ");
    b.append(edu.rice.cs.drjava.Version.getBuildTimeString());
    b.append("\n\n");
    java.util.Properties props = System.getProperties();

    for (Map.Entry<Object, Object> entry : props.entrySet()) { 
      b.append(entry.getKey());
      b.append(" = ");
      if (entry.getKey().equals("line.separator")) {
        b.append("\"");
        String ls = (String)entry.getValue();
        for(int i = 0; i < ls.length(); ++i) {
          int ch = ls.charAt(i);
          b.append("\\u");
          String hexString = "0000" + Integer.toHexString(ch);
          b.append(hexString.substring(hexString.length()-4));
        }
        b.append("\"");
      }
      else {
        b.append(entry.getValue());
      }
      b.append('\n');
    }
    b.append('\n');
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      DrJava.getConfig().saveConfiguration(baos, "DrJava configuration file");
      b.append(baos.toString());
    }
    catch(java.io.IOException ioe) {
      b.append("IOException when trying to print DrJava configuration file");
    }
    
    b.append("\n\nUsed memory: about ");
    b.append(StringOps.memSizeToString(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
    b.append("\nFree memory: about ");
    b.append(StringOps.memSizeToString(Runtime.getRuntime().freeMemory()));
    b.append("\nTotal memory: about ");
    b.append(StringOps.memSizeToString(Runtime.getRuntime().totalMemory()));
    b.append("\nTotal memory can expand to: about ");
    b.append(StringOps.memSizeToString(Runtime.getRuntime().maxMemory()));
    b.append("\n\nNumber of processors/cores: ");
    b.append(Runtime.getRuntime().availableProcessors());
    b.append("\n\n");
    
    
    String infoText = b.toString();
    
    String userHome = System.getProperty("user.home");
    String anonUserHome = "<anonymized user.home>";
    infoText = replaceString(infoText, userHome, anonUserHome);
    
    String userDir = System.getProperty("user.dir");
    String anonUserDir = "<anonymized user.dir>";
    infoText = replaceString(infoText, userDir, anonUserDir);
    
    String userName = System.getProperty("user.name");
    String anonUserName = "<anonymized user.name>";
    infoText = replaceString(infoText, userName, anonUserName);
    
    return infoText;
  }
  
  
  private final Action _okAction = new AbstractAction("OK") {
    public void actionPerformed(ActionEvent e) {
      DrJavaErrorWindow.this.dispose();
      if (DrJavaErrorHandler.getButton() == null) { System.exit(1); }
    }
  };
  
  
  private final Action _prevAction = new AbstractAction("Previous") {
    public void actionPerformed(ActionEvent e) {
      if (_errorIndex > 0) {
        --_errorIndex;
        _error = DrJavaErrorHandler.getError(_errorIndex);
        if (_errorIndex == 0) { setEnabled(false); }
        if (_errorCount>1) { _nextAction.setEnabled(true); }
        updateErrorInfo();
      }
    }
  };
  
  
  private static String replaceString(String text, String orig, String repl) {
    int pos = 0;
    while((pos=text.indexOf(orig,pos)) >= 0) {
      
      text = text.substring(0,pos) + repl + text.substring(pos+orig.length(), text.length());
    }
    return text;
  }
  
  
  private final Action _nextAction = new AbstractAction("Next") {
    public void actionPerformed(ActionEvent e) {
      if (_errorIndex < _errorCount-1) {
        ++_errorIndex;
        _error = DrJavaErrorHandler.getError(_errorIndex);
        if (_errorIndex == _errorCount-1) { setEnabled(false); }
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
      if (errorsButton != null) { errorsButton.setVisible(false); }
      _okAction.actionPerformed(e);
    }
  };

  
  private Action _copyAction = new AbstractAction("Copy This Error") {
    public void actionPerformed(ActionEvent e) {
      _stackTrace.grabFocus();
      _stackTrace.getActionMap().get(DefaultEditorKit.selectAllAction).actionPerformed(e);
      _stackTrace.getActionMap().get(DefaultEditorKit.copyAction).actionPerformed(e);
    }
  };

  
  private static final String HEADER_HTML =
    "<html><font size=\"-1\" face=\"sans-serif, Arial, Helvetica, Geneva\"><b>";
  private static final String ERRORS_FOOTER_HTML = 
    "Please submit a bug report containing the information below " +
    "and an account of the actions that caused the bug (if known) to " +
    "<a href=\"" + SF_ADD_BUG_URL + "\"><b>" + SF_LINK_NAME + "</b></a>.<br>" +
    "You may wish to save all your work and restart DrJava.<br>" +
    "Thanks for your help in making DrJava better!</b></font></p></html>";
  private static final String NO_ERRORS_HTML =
    "No errors occurred!<br>" +
    "Thanks for using DrJava!</b></font></p></html>";
}