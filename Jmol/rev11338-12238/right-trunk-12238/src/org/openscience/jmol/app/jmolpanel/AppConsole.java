
package org.openscience.jmol.app.jmolpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import javax.swing.text.Position;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JScrollPane;

import java.util.Vector;

import org.jmol.api.JmolAppConsoleInterface;
import org.jmol.api.JmolViewer;
import org.jmol.console.JmolConsole;
import org.jmol.i18n.GT;
import org.jmol.util.Logger;
import org.jmol.util.CommandHistory;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;

public final class AppConsole extends JmolConsole implements JmolAppConsoleInterface, EnterListener{
  
  protected ConsoleTextPane console;
  private JButton varButton, haltButton, closeButton, clearButton, 
                  questButton, helpButton, undoButton, redoButton;
  
  private JButton checkButton;
  protected JButton stepButton;
  private JButton topButton;

  
  

  public AppConsole() {
    
    
    
    
  }
  
  public JmolAppConsoleInterface getAppConsole(Viewer viewer, Component display) {
    return new AppConsole(viewer, display instanceof DisplayPanel ? 
        ((DisplayPanel)display).getFrame() 
        : display instanceof JFrame ? (JFrame) display : null);
  }

  public AppConsole(JmolViewer viewer, JFrame frame) {
    super(viewer, frame, GT._("Jmol Script Console") + " " + Viewer.getJmolVersion(), false);
    layoutWindow(getContentPane());
    setSize(645, 400);
    setLocationRelativeTo(frame);
  }

  JButton setButton(String s) {
    JButton b = new JButton(s);
    b.addActionListener(this);
    buttonPanel.add(b);
    return b;
  }
  
  JPanel buttonPanel = new JPanel();
  
  void layoutWindow(Container container) {
    console = new ConsoleTextPane(this);    
    console.setPrompt();
    console.setDragEnabled(true);
    JScrollPane consolePane = new JScrollPane(console);
        
    
    
    editButton = setButton(GT._("Editor"));
    checkButton = setButton(GT._("Check"));
    topButton = setButton(GT._("Top"));
    stepButton = setButton(GT._("Step"));


    
    
    

    varButton = setButton(GT._("Variables"));
    clearButton = setButton(GT._("Clear"));
    haltButton = setButton(GT._("Halt"));

    historyButton = setButton(GT._("History"));
    stateButton = setButton(GT._("State"));

    helpButton = setButton(GT._("Help"));
    closeButton = setButton(GT._("Close"));
    undoButton = setButton(GT._("Undo"));
    redoButton = setButton(GT._("Redo"));

    undoButton.setEnabled(false);
    redoButton.setEnabled(false);


    

  
    JPanel buttonPanelWrapper = new JPanel();
    buttonPanelWrapper.setLayout(new BorderLayout());
    buttonPanelWrapper.add(buttonPanel, BorderLayout.CENTER);

    JSplitPane spane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT,
        consolePane, buttonPanelWrapper);
    consolePane.setMinimumSize(new Dimension(300,300));
    consolePane.setPreferredSize(new Dimension(5000,5000));
    buttonPanelWrapper.setMinimumSize(new Dimension(60,60));
    buttonPanelWrapper.setMaximumSize(new Dimension(1000,60));
    buttonPanelWrapper.setPreferredSize(new Dimension(60,60));
    spane.setDividerSize(0);
    spane.setResizeWeight(0.95);
    container.add(spane);

  
    

  }

  public void sendConsoleEcho(String strEcho) {
    if (strEcho != null && !isError) {      
      console.outputEcho(strEcho);
    }
    setError(false);
  }

  boolean isError = false;
  private void setError(boolean TF) {
    isError = TF;
  }
  
  public void sendConsoleMessage(String strStatus) {
    if (strStatus == null) {
      console.clearContent(null);
      console.outputStatus("");
    } else if (strStatus.indexOf("ERROR:") >= 0) {
      console.outputError(strStatus);
      setError(true);
    } else if (!isError) {
      console.outputStatus(strStatus);
    }
  }

  public void enterPressed() {
    executeCommandAsThread(null);
  }
  
  class ExecuteCommandThread extends Thread {

    String strCommand;
    ExecuteCommandThread (String command) {
      strCommand = command;
      this.setName("appConsoleExecuteCommandThread");
    }
    
    public void run() {
      
      try {
        
        while (console.checking) {
            try {
              Thread.sleep(100); 
            } catch (Exception e) {
              break; 
            }
        }

        executeCommand(strCommand);
      } catch (Exception ie) {
        Logger.error("execution command interrupted!",ie);
      }
    }
  }
   
  ExecuteCommandThread execThread;
  
  protected void execute(String strCommand) {
    
    executeCommandAsThread(strCommand);
  }
  
  void executeCommandAsThread(String strCommand){ 
    if (strCommand == null)
      strCommand = console.getCommandString().trim();
    if (strCommand.equalsIgnoreCase("undo")) {
      undoRedo(false);
      console.appendNewline();
      console.setPrompt();
      return;
    } else if (strCommand.equalsIgnoreCase("redo")) {
      undoRedo(true);
      console.appendNewline();
      console.setPrompt();
      return;
    } else if (strCommand.equalsIgnoreCase("exitJmol")) {
      System.exit(0);
    } else if (strCommand.length() == 0) {
      strCommand = "!resume";
    }
      
    if (strCommand.length() > 0) {
      execThread = new ExecuteCommandThread(strCommand);
      execThread.start();
      
      
      
      
    }
  }

  static int MAXUNDO = 50;
  String[] undoStack = new String[MAXUNDO];
  int undoPointer = 0;
  boolean undoSaved = false;
 
  public void zap() {
    undoClear();
  }
  
  private void undoClear() {
    for (int i = 0; i < MAXUNDO; i++)
      undoStack[i] = null;
    undoPointer = 0;
    undoButton.setEnabled(false);
    redoButton.setEnabled(false);
  }
  
  void undoSetEnabled() {
    undoButton.setEnabled(undoPointer > 0 && undoStack[undoPointer - 1] != null);
    redoButton.setEnabled(undoPointer + 1 < MAXUNDO && undoStack[undoPointer + 1] != null);
  }
  
  void undoRedo(boolean isRedo) {
    
    
    if (!viewer.getBooleanProperty("undo"))
      return;
    if (!undoSaved) 
      undoSave();
    String state = undoStack[undoPointer];
    int ptr = undoPointer + (isRedo ? 1 : -1);
    if (ptr == MAXUNDO)
      ptr--;
    if (ptr < 0)
      ptr = 0;
    
    state = undoStack[ptr];
    if (state != null) {
      state += CommandHistory.NOHISTORYATALL_FLAG;
      setError(false);
      viewer.evalStringQuiet(state);
      undoPointer = ptr;
    }
    undoSetEnabled();
  }
  
  void undoSave() {
    if (!viewer.getBooleanProperty("undo"))
      return;
    
    undoPointer++;
    if (undoPointer == MAXUNDO) {
      for (int i = 1; i < MAXUNDO; i++)
        undoStack[i - 1] = undoStack[i];
      undoPointer--;
    }
    
    for (int i = undoPointer; i < MAXUNDO; i++)
      undoStack[i] = null;
    Logger.startTimer();
    undoStack[undoPointer] = (String) viewer.getProperty("readable", "stateInfo",
        null);
    if (Logger.checkTimer(null) > 1000) {
      viewer.setBooleanProperty("undo", false);
      Logger.info("command processing slow; undo disabled");
      undoClear();
    } else {
      undoSetEnabled();
    }
    undoSaved = true;
  }
  
  void executeCommand(String strCommand) {
    boolean doWait;
    console.appendNewline();
    console.setPrompt();
    if (strCommand.length() == 0) {
      console.grabFocus();
      return;
    }
    if (strCommand.charAt(0) != '!' && viewer.getBooleanProperty("executionPaused"))
      strCommand = "!" + strCommand;
    if (strCommand.charAt(0) != '!' && !isError) {
      undoSave();
    }
    setError(false);
    undoSaved = false;

    String strErrorMessage = null;
    doWait = (strCommand.indexOf("WAITTEST ") == 0);
    if (doWait) { 
      
      Vector info = (Vector) viewer
          .scriptWaitStatus(strCommand.substring(5),
              "+fileLoaded,+scriptStarted,+scriptStatus,+scriptEcho,+scriptTerminated");
      
      for (int i = 0; i < info.size(); i++) {
        Vector statusRecordSet = (Vector) info.get(i);
        for (int j = 0; j < statusRecordSet.size(); j++) {
          Vector statusRecord = (Vector) statusRecordSet.get(j);
          Logger.info("msg#=" + statusRecord.get(0) + " " + statusRecord.get(1)
              + " intInfo=" + statusRecord.get(2) + " stringInfo="
              + statusRecord.get(3));
        }
      }
      console.appendNewline();
    } else {
      boolean isScriptExecuting = viewer.isScriptExecuting();
      strErrorMessage = "";
      if (viewer.checkHalt(strCommand))
        strErrorMessage = (isScriptExecuting ? "script execution halted with "
            + strCommand : "no script was executing");
      
      if (strErrorMessage.length() > 0) {
        console.outputError(strErrorMessage);
      } else {
        viewer.script(strCommand + (strCommand.indexOf("\0##") >= 0 ? "" : JmolConstants.SCRIPT_EDITOR_IGNORE));
      }
    }
    if (strCommand.indexOf("\0##") < 0)
      console.grabFocus();
  }

  protected void clearContent(String text) {
    console.clearContent(text);
  }

  public void actionPerformed(ActionEvent e) {
    console.grabFocus(); 
    Object source = e.getSource();

    if (source == topButton) {
      if (scriptEditor != null)
        scriptEditor.gotoTop();
      return;
    }
    if (source == checkButton) {
      if (scriptEditor != null)
        scriptEditor.checkScript();
    }
    if (source == stepButton) {
      if (scriptEditor != null)
        scriptEditor.doStep();
      return;
    }

    
    
    
    if (source == closeButton) {
      setVisible(false);
      return;
    }
    if (source == haltButton) {
      viewer.haltScriptExecution();
      return;
    }
    if (source == questButton) {
      execute("!?");
      return;
    }
    if (source == varButton) {
      execute("!show variables");
      return;
    }
    if (source == clearButton) {
      console.clearContent(null);
      return;
    }
    if (source == undoButton) {
      undoRedo(false);
      return;
    }
    if (source == redoButton) {
      undoRedo(true);
      return;
    }
    if (source == helpButton) {
        URL url = this.getClass().getClassLoader()
            .getResource("org/openscience/jmol/Data/guide/ch04.html");
        HelpDialog hd = new HelpDialog(null, url);
        hd.setVisible(true);
    }
    super.actionPerformed(e);
  }
  
  class ConsoleTextPane extends JTextPane {

    private ConsoleDocument consoleDoc;
    private EnterListener enterListener;
    
    boolean checking = false;
    
    ConsoleTextPane(AppConsole appConsole) {
      super(new ConsoleDocument());
      consoleDoc = (ConsoleDocument)getDocument();
      consoleDoc.setConsoleTextPane(this);
      this.enterListener = (EnterListener) appConsole;
    }

    public String getCommandString() {
      String cmd = consoleDoc.getCommandString();
      return cmd;
    }

    public void setPrompt() {
      consoleDoc.setPrompt();
    }

    public void appendNewline() {
      consoleDoc.appendNewline();
    }

    public void outputError(String strError) {
      consoleDoc.outputError(strError);
    }

    public void outputErrorForeground(String strError) {
      consoleDoc.outputErrorForeground(strError);
    }

    public void outputEcho(String strEcho) {
      consoleDoc.outputEcho(strEcho);
    }

    public void outputStatus(String strStatus) {
      consoleDoc.outputStatus(strStatus);
    }

    public void enterPressed() {
      if (enterListener != null)
        enterListener.enterPressed();
    }
    
    public void clearContent(String text) {
      consoleDoc.clearContent();
      if (text != null)
        consoleDoc.outputEcho(text);  
      setPrompt();
    }
    
     

    
    protected void processKeyEvent(KeyEvent ke) {
      
      

      int kcode = ke.getKeyCode();
      int kid = ke.getID();
      if (kid == KeyEvent.KEY_PRESSED) {
        if (kcode == KeyEvent.VK_TAB) {
          ke.consume();
          if (consoleDoc.isAtEnd()) {
            String cmd = completeCommand(consoleDoc.getCommandString());
            if (cmd != null)
              try {
                consoleDoc.replaceCommand(cmd, false);
              } catch (BadLocationException e) {
                
              }
            nTab++;
            
            return;
          }
        }
        nTab = 0;
      }
      if (kcode == KeyEvent.VK_UP && kid == KeyEvent.KEY_PRESSED
          && !ke.isControlDown()) {
        recallCommand(true);
      } else if (kcode == KeyEvent.VK_DOWN && kid == KeyEvent.KEY_PRESSED
          && !ke.isControlDown()) {
        recallCommand(false);
      } else if ((kcode == KeyEvent.VK_DOWN || kcode == KeyEvent.VK_UP)
          && kid == KeyEvent.KEY_PRESSED && ke.isControlDown()) {
        
        
        
        
        super.processKeyEvent(new KeyEvent((Component) ke.getSource(), kid, ke
            .getWhen(), 0, 
            kcode, ke.getKeyChar(), ke.getKeyLocation()));
      } else {
        
        super.processKeyEvent(ke);
        
        
        
        

        
        if (kid == KeyEvent.KEY_RELEASED
            && ke.getModifiers() < 2
            && (kcode > KeyEvent.VK_DOWN && kcode < 400 || kcode == KeyEvent.VK_BACK_SPACE))
          checkCommand();
      }
    }
    
    
    void recallCommand(boolean up) {
      String cmd = viewer.getSetHistory(up ? -1 : 1);
      
      if (cmd == null) {
        return;
      }
      boolean isError = false;
      try {
        if (cmd.endsWith(CommandHistory.ERROR_FLAG)) {
          isError = true;
          cmd = cmd.substring(0, cmd.indexOf(CommandHistory.ERROR_FLAG));
        }
        cmd = TextFormat.trim(cmd, ";");
        consoleDoc.replaceCommand(cmd, isError);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }  
     
    synchronized void checkCommand() {
      String strCommand = consoleDoc.getCommandString();
      if (strCommand.length() == 0 || strCommand.charAt(0) == '!'
          || viewer.isScriptExecuting() || viewer.getBooleanProperty("executionPaused"))
        return;
      checking = true;
      consoleDoc
          .colorCommand(viewer.scriptCheck(strCommand) instanceof String ? 
             consoleDoc.attError : consoleDoc.attUserInput);
      checking = false;
    }
  }

  protected String completeCommand(String thisCmd) {
    return super.completeCommand(thisCmd);
  }

  public Object getMyMenuBar() {
    return null;
  }

  public String getText() {
    return console.getText();
  }

  class ConsoleDocument extends DefaultStyledDocument {

    private ConsoleTextPane consoleTextPane;

    SimpleAttributeSet attError;
    SimpleAttributeSet attEcho;
    SimpleAttributeSet attPrompt;
    SimpleAttributeSet attUserInput;
    SimpleAttributeSet attStatus;

    ConsoleDocument() {
      super();

      attError = new SimpleAttributeSet();
      StyleConstants.setForeground(attError, Color.red);

      attPrompt = new SimpleAttributeSet();
      StyleConstants.setForeground(attPrompt, Color.magenta);

      attUserInput = new SimpleAttributeSet();
      StyleConstants.setForeground(attUserInput, Color.black);

      SimpleAttributeSet attEcho;
      attEcho = new SimpleAttributeSet();
      StyleConstants.setForeground(attEcho, Color.blue);
      StyleConstants.setBold(attEcho, true);

      attStatus = new SimpleAttributeSet();
      StyleConstants.setForeground(attStatus, Color.black);
      StyleConstants.setItalic(attStatus, true);
    }

    void setConsoleTextPane(ConsoleTextPane consoleTextPane) {
      this.consoleTextPane = consoleTextPane;
    }

    private Position positionBeforePrompt; 
    private Position positionAfterPrompt;  
    private int offsetAfterPrompt;         

    boolean isAtEnd() {
      return consoleTextPane.getCaretPosition() == getLength();
    }
    
    void clearContent() {
      try {
        super.remove(0, getLength());
      } catch (BadLocationException exception) {
        Logger.error("Could not clear script window content", exception);
      }
    }
    
    void setPrompt() {
      try {
        super.insertString(getLength(), "$ ", attPrompt);
        setOffsetPositions();
        consoleTextPane.setCaretPosition(offsetAfterPrompt);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }

    void setOffsetPositions() {
      try {
        offsetAfterPrompt = getLength();
        positionBeforePrompt = createPosition(offsetAfterPrompt - 2);
        
        
        positionAfterPrompt = createPosition(offsetAfterPrompt - 1);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }

    void setNoPrompt() {
      try {
        offsetAfterPrompt = getLength();
        positionAfterPrompt = positionBeforePrompt = createPosition(offsetAfterPrompt);
        consoleTextPane.setCaretPosition(offsetAfterPrompt);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }

    
    
    
    void outputBeforePrompt(String str, SimpleAttributeSet attribute) {
      try {
        int pt = consoleTextPane.getCaretPosition();
        Position caretPosition = createPosition(pt);
        pt = positionBeforePrompt.getOffset();
        super.insertString(pt, str+"\n", attribute);
        
        offsetAfterPrompt += str.length() + 1;
        positionBeforePrompt = createPosition(offsetAfterPrompt - 2);
        positionAfterPrompt = createPosition(offsetAfterPrompt - 1);
        
        pt = caretPosition.getOffset();
        consoleTextPane.setCaretPosition(pt);
      } catch (Exception e) {
        e.printStackTrace();
        consoleTextPane.setCaretPosition(getLength());
      }
    }

    void outputError(String strError) {
      outputBeforePrompt(strError, attError);
    }

    void outputErrorForeground(String strError) {
      try {
        super.insertString(getLength(), strError+"\n", attError);
        consoleTextPane.setCaretPosition(getLength());
      } catch (BadLocationException e) {
        e.printStackTrace();

      }
    }

    void outputEcho(String strEcho) {
      outputBeforePrompt(strEcho, attEcho);
    }

    void outputStatus(String strStatus) {
      outputBeforePrompt(strStatus, attStatus);
    }

    void appendNewline() {
      try {
        super.insertString(getLength(), "\n", attUserInput);
        consoleTextPane.setCaretPosition(getLength());
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }

    
    
    public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException {
      int ichNewline = str.indexOf('\n');
      if (ichNewline != 0) {
        if (offs < offsetAfterPrompt) {
          offs = getLength();
        }
        super.insertString(offs, str, a == attError ? a : attUserInput);
        consoleTextPane.setCaretPosition(offs+str.length());
      }
      if (ichNewline >= 0) {
        consoleTextPane.enterPressed();
      }
    }

    String getCommandString() {
      String strCommand = "";
      try {
        int cmdStart = positionAfterPrompt.getOffset();
        strCommand =  getText(cmdStart, getLength() - cmdStart);
        while (strCommand.length() > 0 && strCommand.charAt(0) == ' ')
          strCommand = strCommand.substring(1);
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
      return strCommand;
    }

    public void remove(int offs, int len)
      throws BadLocationException {
      if (offs < offsetAfterPrompt) {
        len -= offsetAfterPrompt - offs;
        if (len <= 0)
          return;
        offs = offsetAfterPrompt;
      }
      super.remove(offs, len);

    }

    public void replace(int offs, int length, String str, AttributeSet attrs)
      throws BadLocationException {
      if (offs < offsetAfterPrompt) {
        if (offs + length < offsetAfterPrompt) {
          offs = getLength();
          length = 0;
        } else {
          length -= offsetAfterPrompt - offs;
          offs = offsetAfterPrompt;
        }
      }
      super.replace(offs, length, str, attrs);

    }

     
    void replaceCommand(String newCommand, boolean isError) throws BadLocationException {
      if (positionAfterPrompt == positionBeforePrompt)
        return;
      replace(offsetAfterPrompt, getLength() - offsetAfterPrompt, newCommand,
          isError ? attError : attUserInput);
    }

    void colorCommand(SimpleAttributeSet att) {
      if (positionAfterPrompt == positionBeforePrompt)
        return;
      setCharacterAttributes(offsetAfterPrompt, getLength() - offsetAfterPrompt, att, true);
    }
  }

}

interface EnterListener {
  public void enterPressed();
}

