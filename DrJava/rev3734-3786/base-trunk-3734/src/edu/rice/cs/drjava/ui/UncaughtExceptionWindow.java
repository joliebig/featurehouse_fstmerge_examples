

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.swing.BorderlessScrollPane;


public class UncaughtExceptionWindow extends JDialog {

  
  private JComponent _exceptionInfo;
  
  private JTextArea _stackTrace;
  
  private JScrollPane _stackTraceScroll;
  
  private JPanel _okPanel;
  
  private JPanel _buttonPanel;
  
  private JButton _copyButton;
  
  private JButton _okButton;
  
  private Throwable _exception;

  
  public UncaughtExceptionWindow(JFrame frame, Throwable exception) {
    super(frame, "Unexpected Error");
    System.out.println("Unexpected Window Exception: " + exception);
    _exception = exception;

    this.setSize(600,400);
    setLocationRelativeTo(frame);

    String trace = StringOps.getStackTrace(_exception);
    if (_exception instanceof UnexpectedException) {
      Throwable t = ((UnexpectedException)_exception).getCause();
      trace = trace + "\nCaused by:\n" + StringOps.getStackTrace(t);
    }

    
    
    _stackTrace = new JTextArea(trace);
    msg[1] = exception.toString();
    _exceptionInfo = new JOptionPane(msg,JOptionPane.ERROR_MESSAGE,
                                     JOptionPane.DEFAULT_OPTION,null,
                                     new Object[0]);

    _stackTrace.setEditable(false);

    _copyButton = new JButton(_copyAction);
    _okButton = new JButton(_okAction);

    _okPanel = new JPanel(new BorderLayout());
    _buttonPanel = new JPanel();
    _buttonPanel.add(_copyButton);
    _buttonPanel.add(_okButton);
    _okPanel.add(_buttonPanel, BorderLayout.EAST);

    _stackTraceScroll = new
      BorderlessScrollPane(_stackTrace,
                           JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                           JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    JPanel cp = new JPanel(new BorderLayout(5,5));
    cp.setBorder(new EmptyBorder(5,5,5,5));
    setContentPane(cp);
    cp.add(_exceptionInfo, BorderLayout.NORTH);
    cp.add(_stackTraceScroll, BorderLayout.CENTER);
    cp.add(_okPanel, BorderLayout.SOUTH);
    setVisible(true);
  }

  private Action _okAction = new AbstractAction("OK") {
    public void actionPerformed(ActionEvent e) {
      UncaughtExceptionWindow.this.dispose();
    }
  };

  private Action _copyAction = new AbstractAction("Copy Stack Trace") {
    public void actionPerformed(ActionEvent e) {
      _stackTrace.grabFocus();
      _stackTrace.getActionMap().get(DefaultEditorKit.selectAllAction).
        actionPerformed(e);
      _stackTrace.getActionMap().get(DefaultEditorKit.copyAction).
        actionPerformed(e);
    }
  };

  
  private final String[] msg = {
    "A runtime exception occured!",
    "",
    "Please submit a bug report containing the system information in the Help>About ",
    "window and an account of the actions that caused the bug (if known) to",
    "http://sourceforge.net/projects/drjava.",
    "You may wish to save all your work and restart DrJava.",
    "Thanks for your help in making DrJava better!"};

}