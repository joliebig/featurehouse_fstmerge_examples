

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.util.text.ConsoleDocument;


public class ConsoleController extends AbstractConsoleController  {
  
  
  protected volatile ConsoleDocument _consoleDoc;

  public ConsoleController(final ConsoleDocument consoleDoc, InteractionsDJDocument swingDoc) {
    super(swingDoc, new InteractionsPane("CONSOLE_KEYMAP", swingDoc) {  
      public int getPromptPos() { return consoleDoc.getPromptPos(); }
    });
    _consoleDoc = consoleDoc;
    _pane.setEditable(false);
    _init();
  }

  
  public ConsoleDocument getConsoleDoc() { return _consoleDoc; }

  protected void _setupModel() {
    _consoleDoc.setBeep(_pane.getBeep());  
  }
}

