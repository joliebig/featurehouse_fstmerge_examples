

package edu.rice.cs.util.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

import java.io.Serializable;

import edu.rice.cs.util.Lambda;
import edu.rice.cs.util.text.ConsoleDocument;
import edu.rice.cs.util.text.EditDocumentInterface;


public class PopupConsole implements Serializable {
  
  private static final String INPUT_ENTERED_NAME = "Input Entered";
  private static final String INSERT_NEWLINE_NAME = "Insert Newline";
    
  private JTextArea _inputBox;
  
  
  private Action _insertNewlineAction = new AbstractAction() {
    public void actionPerformed(ActionEvent e) {
      JTextArea source = (JTextArea)e.getSource();
      source.insert("\n", source.getCaretPosition());
    }
  };
  
  private String _title;
  private Component _parentComponent;
  
  
  private EditDocumentInterface _doc;
  
  
  private ConsoleDocument _console;
  
  private Runnable _interruptCommand;
  private Lambda<Object,String> _insertTextCommand;
  
  
  private final Object commandLock = new Object();
  
  
  private boolean monitorNotified = false; 
  
  
  private volatile boolean inputAborted = false;
  
  
  public PopupConsole(Component owner, EditDocumentInterface doc, ConsoleDocument console, JTextArea inputBox, 
                      String title) {
    setParent(owner);
    _doc = doc;
    _console = console;
    setInputBox(inputBox);
    setTitle(title);
  }
  
  private String _consoleLine;
  
  public String getConsoleInput() { 
    Utilities.invokeAndWait(new Runnable() {
      public void run() {
        Frame parentFrame = JOptionPane.getFrameForComponent(_parentComponent);
        if (parentFrame.isVisible()) _consoleLine = showDialog(parentFrame);
        else _consoleLine = silentInput();
      }
    });
    return _consoleLine + "\n";
  }
  
  
  public void interruptConsole() {
    synchronized (commandLock) { if (_interruptCommand != null) _interruptCommand.run(); }
  }
  
  
  public void insertConsoleText(String txt) {
    synchronized (commandLock) {
      if (_insertTextCommand != null) _insertTextCommand.apply(txt); 
      else {
        throw new IllegalStateException("Console not ready for text insertion");
      }
    }
  }
  
  
  public void waitForConsoleReady() throws InterruptedException {
    synchronized (commandLock) { while (_interruptCommand == null) commandLock.wait(); }
  }
  
  public boolean isConsoleReady() {
    synchronized (commandLock) { return _interruptCommand != null; }
  }
  
  public void setInputBox(JTextArea inputBox) {
    if (inputBox == null) _inputBox = new InputBox();
    else _inputBox = inputBox;
    
    InputMap im = _inputBox.getInputMap();
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,Event.SHIFT_MASK), INSERT_NEWLINE_NAME);
    ActionMap am = _inputBox.getActionMap();
    am.put(INSERT_NEWLINE_NAME, _insertNewlineAction);
  }
  
  public JTextArea getInputBox() { return _inputBox; }
  
  public void setParent(Component c) { _parentComponent = c; }
  
  public Component getParent() { return _parentComponent; }
  
  public void setTitle(String title) {
    if (title == null) _title = "Console";
    else _title = title;
  }
  
  public String getTitle() { return _title; }
  
  
  private String showDialog(Frame parentFrame) {
    final JDialog dialog = createDialog(_inputBox, parentFrame);
    synchronized (commandLock) {
      _interruptCommand = new Runnable() {
        public void run() { dialog.setVisible(false); }
      };
      _insertTextCommand = new Lambda<Object,String>() {
        public Object apply(String input) {
          _inputBox.insert(input, _inputBox.getCaretPosition());
          return null;
        }
      };
      commandLock.notifyAll();  
    }
    
    dialog.setVisible(true);
    dialog.dispose();
    
    synchronized (commandLock) {
      _interruptCommand = null;
      _insertTextCommand = null;
    }
    
    String input = _inputBox.getText();
    if (inputAborted) {
      inputAborted = false;
      throw new IllegalStateException("System.in aborted");
    }
    _doc.append(input + "\n", _console.SYSTEM_IN_STYLE);
    _console.append(input + "\n", _console.SYSTEM_IN_STYLE);
    return input;
  }
  
  
  
  private JDialog createDialog(JTextArea inputBox, Frame parentFrame) {
    
    final JDialog dialog = new JDialog(parentFrame, _title, true);
    
    inputBox.setText("");

    Container cp = dialog.getContentPane();
    cp.add(new JScrollPane(inputBox), BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JLabel label = new JLabel("<html>Enter a full line of input.<br>" +
                              "Hit SHIFT+&lt;Enter&gt; to insert a newline character.&nbsp</html>");
    buttonPanel.add(label);
    
    Action inputEnteredAction = new AbstractAction("Done") {
      public void actionPerformed(ActionEvent e) { dialog.setVisible(false); }
    };    
    
    JButton doneButton = new JButton(inputEnteredAction);

    buttonPanel.add(doneButton);
    dialog.getRootPane().setDefaultButton(doneButton);
    
    Action inputAbortedAction = new AbstractAction("Abort") {
      public void actionPerformed(ActionEvent e) {
        inputAborted = true;
        PopupConsole.this.interruptConsole();
      }
    };
    
    JButton abortButton = new JButton(inputAbortedAction);

    buttonPanel.add(abortButton);
    
    cp.add(buttonPanel, BorderLayout.SOUTH);
    
    inputBox.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), INPUT_ENTERED_NAME);
    inputBox.getActionMap().put(INPUT_ENTERED_NAME, inputEnteredAction);
    
    buttonPanel.setSize(500, 100);
    dialog.setSize(500, 100);
    dialog.setLocationRelativeTo(parentFrame);
    return dialog;
  }
  
  
  
  
  
  
  
  
  
  
  private String silentInput() {
    final Object monitor = new Object();
    monitorNotified = false;
    final StringBuffer input = new StringBuffer();   
    
    synchronized (commandLock) {
      _insertTextCommand = new Lambda<Object,String>() {
        public Object apply(String s) {
          input.append(s);
          return null;
        }
      };
      
      _interruptCommand = new Runnable() {
        public void run() {
          
          _insertTextCommand = null;
          _interruptCommand = null;

          synchronized (monitor) { 
            monitorNotified = true;
            monitor.notify();   
          }
        }
      };
      commandLock.notifyAll();  
    }
    synchronized (monitor) { 
      try { while (! monitorNotified) monitor.wait(); }   
      catch (InterruptedException e) { }
    }
    synchronized (commandLock) { return input.toString(); }
  }
  

  



















}
