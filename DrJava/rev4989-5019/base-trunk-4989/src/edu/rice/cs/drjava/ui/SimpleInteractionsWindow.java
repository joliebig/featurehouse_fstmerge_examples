

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;

import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.SwingFrame;



public class SimpleInteractionsWindow extends SwingFrame {
  
  private final SimpleInteractionsModel _model;
  private final InteractionsDJDocument _adapter;
  private final InteractionsPane _pane;
  private final InteractionsController _controller;
  
  public SimpleInteractionsWindow() { this("Interactions Window"); }
  
  public SimpleInteractionsWindow(String title) {
    super(title);
    setSize(600, 400);
    
    _adapter = new InteractionsDJDocument();
    
    _model = new SimpleInteractionsModel(_adapter);
    _pane = new InteractionsPane(_adapter) {
      public int getPromptPos() { return _model.getDocument().getPromptPos(); }
    };
    _controller = new InteractionsController(_model, _adapter, _pane, new Runnable() { public void run() { } });
    
    _pane.setFont(Font.decode("monospaced"));
    
    _model.addListener(new InteractionsListener() {
      public void interactionStarted() { _pane.setEditable(false); }
      public void interactionEnded() {
        _controller.moveToPrompt();
        _pane.setEditable(true);
      }
      public void interpreterResetting() { _pane.setEditable(false); }
      public void interactionErrorOccurred(int offset, int length) { _pane.highlightError(offset, length); }
      public void interpreterReady(File wd) {
        _controller.moveToPrompt();
        _pane.setEditable(true);
      }
      public void interpreterExited(int status) { }
      public void interpreterChanged(boolean inProgress) { _pane.setEditable(inProgress); }
      public void interpreterResetFailed(Throwable t) { interpreterReady(FileOps.NULL_FILE); }
      public void interactionIncomplete() {
        int caretPos = _pane.getCaretPosition();
        _controller.getConsoleDoc().insertNewline(caretPos);
      }
    });
    
    JScrollPane scroll = new JScrollPane(_pane);
    getContentPane().add(scroll);
    
    
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) { close(); }
    });
    
    initDone(); 
  }
  
  
  protected void close() { System.exit(0); }
  
  
  public InteractionsController getController() { return _controller; }
  
  
  public static void main(String[] args) {
    SimpleInteractionsWindow w = new SimpleInteractionsWindow();
    if (args.length > 0 && args[0].equals("-debug")) {
      


    }
    w.setVisible(true);
  }
}
