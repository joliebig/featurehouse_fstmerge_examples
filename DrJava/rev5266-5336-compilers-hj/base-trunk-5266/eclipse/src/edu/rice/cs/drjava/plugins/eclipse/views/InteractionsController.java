

package edu.rice.cs.drjava.plugins.eclipse.views;


import java.io.File;
import java.util.Iterator;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;

import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import edu.rice.cs.drjava.plugins.eclipse.DrJavaConstants;
import edu.rice.cs.drjava.plugins.eclipse.EclipsePlugin;
import edu.rice.cs.drjava.plugins.eclipse.repl.EclipseInteractionsModel;
import edu.rice.cs.drjava.plugins.eclipse.util.text.SWTDocumentAdapter;
import edu.rice.cs.drjava.plugins.eclipse.util.text.SWTDocumentAdapter.SWTStyle;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.text.ConsoleDocument;


public class InteractionsController {
  
  
  
  
  protected EclipseInteractionsModel _model;
  
  
  protected SWTDocumentAdapter _adapter;
  
  
  protected InteractionsDocument _doc;
  
  
  protected InteractionsView _view;
  
  
  private String _input;
  
  
  protected Color _colorRed;
  protected Color _colorDarkRed;
  protected Color _colorDarkGreen;
  protected Color _colorDarkBlue;
  protected Color _colorYellow;
  protected Color _colorPurple;
  
  
  protected boolean _enabled;
  
  protected static final String INPUT_ENTERED_NAME = "Input Entered";
  protected static final String INSERT_NEWLINE_NAME = "Insert Newline";
  
  
  private IInputValidator _inputValidator = new IInputValidator() {
    public String isValid(String newText) { return null; }
  };
  
  
  protected InputListener _inputListener = new InputListener() {
    public String getConsoleInput() {



      Display d = _view.getTextPane().getDisplay();
      d.syncExec(new Runnable() {
        public void run() {
          try {
            InputDialog input = new InputDialog(_view.getSite().getShell(), "System.in",
                                                "Please enter a line of input to System.in.",
                                                "", _inputValidator);
            input.open();
            _input = input.getValue();
          }
          catch (Throwable t) {

            _input = "";
            
          }
        }
      });
      _input += "\n";
      _doc.insertBeforeLastPrompt(_input, ConsoleDocument.SYSTEM_IN_STYLE);
      return _input;
    }
  };
  
  
  
  
  protected Preferences.IPropertyChangeListener _preferenceListener;
  protected IPropertyChangeListener _jfacePreferenceListener;
  
  
  protected boolean _promptToReset;
  
  
  protected boolean _promptIfExited;
  
  
  public InteractionsController(EclipseInteractionsModel model,
                                SWTDocumentAdapter adapter,
                                InteractionsView view) {
    _model = model;
    _adapter = adapter;
    _doc = model.getDocument();
    _view = view;
    _enabled = true;
    
    
    Preferences prefs = EclipsePlugin.getDefault().getPluginPreferences();
    
    _preferenceListener = new PrefChangeListener();
    _jfacePreferenceListener = new JFacePrefChangeListener();
    prefs.addPropertyChangeListener(_preferenceListener);
    
    JFaceResources.getFontRegistry().addListener(_jfacePreferenceListener);
    _updateJFacePreferences();
    _updatePreferences();

    
    _view.getTextPane().setCaretOffset(_doc.getLength());
    _addDocumentStyles();
    _setupModel();
    _setupView();
  }
  
  
  public void dispose() {
    _model.dispose();
    _colorRed.dispose();
    _colorDarkRed.dispose();
    _colorDarkGreen.dispose();
    _colorDarkBlue.dispose();
    _colorYellow.dispose();
    
    
    
    Preferences store = EclipsePlugin.getDefault().getPluginPreferences();
    
    store.removePropertyChangeListener(_preferenceListener);
    JFaceResources.getFontRegistry().removeListener(_jfacePreferenceListener);
  }
  
  
  private void _updateJFacePreferences() {
    
    _view.updateFont();
  }
  
  private void _updatePreferences() {
    Preferences store = EclipsePlugin.getDefault().getPluginPreferences();
    
    
    _promptToReset = store.getBoolean(DrJavaConstants.INTERACTIONS_RESET_PROMPT);
    _promptIfExited = store.getBoolean(DrJavaConstants.INTERACTIONS_EXIT_PROMPT);
    boolean privateAccessible = store.getBoolean(DrJavaConstants.ALLOW_PRIVATE_ACCESS);
    
    _model.setPrivateAccessible(privateAccessible);
    
    
    
    
    
    String jvmArgs = store.getString(DrJavaConstants.JVM_ARGS);
    if (jvmArgs.equals("") || !called) {
      _model.setOptionArgs(jvmArgs);
    }
    else {
      String confirmMessage =
        "Specifying the command-line arguments to the Interactions JVM is an\n" +
        "advanced option, and incorrect arguments may cause the Interactions\n" +
        "View to stop responding. Are you sure you want to set this option?\n" +
        "(You must reset the Interactions View before changes will take effect.)";
      if (_view.showConfirmDialog("Setting JVM Arguments", confirmMessage)) {
        _model.setOptionArgs(jvmArgs);
      }
      else {
        store.setValue(DrJavaConstants.JVM_ARGS, "");
      }
    }
    called = true;
  }
  boolean called = false;
  
  
  public EclipseInteractionsModel getInteractionsModel() {
    return _model;
  }
  
  
  public SWTDocumentAdapter getDocumentAdapter() {
    return _adapter;
  }
  
  
  public InteractionsDocument getDocument() {
    return _doc;
  }
  
  
  public InteractionsView getView() {
    return _view;
  }
  
  
  protected void _addDocumentStyles() {
    Display display = _view.getTextPane().getDisplay();
    _colorRed = new Color(display, 255, 0, 0);
    _colorDarkRed = new Color(display, 178, 0, 0);
    _colorDarkGreen = new Color(display, 0, 124, 0);
    _colorDarkBlue = new Color(display, 0, 0, 178);
    _colorYellow = new Color(display, 255, 255, 0);
    _colorPurple = new Color(display, 124, 0, 124);
    
    
    SWTStyle out = new SWTStyle(_colorDarkGreen, 0);
    _adapter.addDocStyle(InteractionsDocument.SYSTEM_OUT_STYLE, out);
    
    
    SWTStyle err = new SWTStyle(_colorRed, 0);
    _adapter.addDocStyle(InteractionsDocument.SYSTEM_ERR_STYLE, err);
    
    
    SWTStyle in = new SWTStyle(_colorPurple, 0);
    _adapter.addDocStyle(InteractionsDocument.SYSTEM_IN_STYLE, in);
    
    
    SWTStyle error = new SWTStyle(_colorDarkRed, SWT.BOLD);
    _adapter.addDocStyle(InteractionsDocument.ERROR_STYLE, error);
    
    
    SWTStyle debug = new SWTStyle(_colorDarkBlue, SWT.BOLD);
    _adapter.addDocStyle(InteractionsDocument.DEBUGGER_STYLE, debug);
  }
  
  
  protected void _setupModel() {
    _adapter.addModifyListener(new DocumentUpdateListener());
    _doc.setBeep(_view.getBeep());
    _model.addInteractionsListener(new EclipseInteractionsListener());
    _model.setInputListener(_inputListener);
  }
  
  
  class DocumentUpdateListener implements ModifyListener {
    public void modifyText(ModifyEvent e) {
      
      StyledText pane = _view.getTextPane();
      int caretPos = pane.getCaretOffset();
      int promptPos = _doc.getPromptPos();
      int docLength = _doc.getLength();
      
      if (_doc.inProgress()) {
        
        
        moveToEnd();
      }
      else {
        
        
        
        if ((caretPos < promptPos) && (promptPos <= docLength)) {
          moveToPrompt();
        }
        else {
          pane.showSelection();
        }
      }
    }
  }
  
  
  protected void _enableInteractionsPane() {
    _enabled = true;
    _view.setBusyCursorShown(false);
    _view.setEditable(true);
  }
  
  
  protected void _disableInteractionsPane() {
    _enabled = false;
    _view.setBusyCursorShown(true);
    _view.setEditable(false);
  }
  
  
  class EclipseInteractionsListener implements InteractionsListener {
    
    public void interactionStarted() { _disableInteractionsPane(); }
    
    public void interactionEnded() {
      _enableInteractionsPane();
      moveToPrompt();
    }
    
    public void interactionErrorOccurred(final int offset, final int length) {
      _view.getTextPane().getDisplay().asyncExec(new Runnable() {
        public void run() { _adapter.highlightRange(offset, length, _colorYellow); }
      });
    }
    
    public void interpreterResetting() { _disableInteractionsPane(); }
    
    public void interpreterReady(File wd) {
      _enableInteractionsPane();
      moveToPrompt();
    }
    
    public void interpreterExited(int status) {
      if (_promptIfExited) {
        String title = "Interactions terminated by System.exit(" + status + ")";
        String msg = "The interactions window was terminated by a call " + "to System.exit(" + status + ").\n" +
          "The interactions window will now be restarted.";
        _view.showInfoDialog(title, msg);
      }
    }
    
    public void interpreterChanged(boolean inProgress) {
      if (inProgress) _disableInteractionsPane();
      else _enableInteractionsPane();
    }
    
    public void interpreterResetFailed(Throwable t) {
      String title = "Interactions Could Not Reset";
      String msg = "The interactions window could not be reset:\n" + t.toString();
      _view.showInfoDialog(title, msg);
      interpreterReady(null);
    }
    
    public void interactionIncomplete() {
      _view.getTextPane().getDisplay().asyncExec(new Runnable() {
        public void run() {





          moveToEnd();
        }
      });
    }
    
    public void slaveJVMUsed() { }
  }
  
  
  protected void _setupView() {
    
    _view.getTextPane().addVerifyKeyListener(new KeyUpdateListener());

    
    
    _setupMenu();
  }
  
  
  protected void _setupMenu() {
    IWorkbenchWindow window = _view.getSite().getWorkbenchWindow();
    final IAction copyAction = ActionFactory.COPY.create(window);
    copyAction.setEnabled(false);
    _view.addSelectionListener(new SelectionAdapter() { 
      public void widgetSelected(SelectionEvent e) {
        copyAction.setEnabled(_view.getTextPane().getSelectionCount() > 0);
      }
    });
    _view.addMenuItem(copyAction);
    
    IAction resetInteractionsAction = new Action() {
      public void run() {
        String title = "Confirm Reset Interactions";
        String message = "Are you sure you want to reset the Interactions View?";
        if (!_promptToReset || _view.showConfirmDialog(title, message)) {
          _model.resetInterpreter(EclipseInteractionsModel.WORKING_DIR);
        }
      }
    };
    resetInteractionsAction.setText("Reset Interactions");
    resetInteractionsAction.setToolTipText("Reset the Interactions View");
    resetInteractionsAction.setImageDescriptor(_getStandardIcon(ActionFactory.DELETE, window));
    _view.addMenuItem(resetInteractionsAction);
    _view.addToolbarItem(resetInteractionsAction);
    
    IAction showClasspathAction = new Action() {
      public void run() {
        String title = "Interpreter Classpath";
        StringBuffer cpBuf = new StringBuffer();
        Iterable<File> classpathElements = _model.getClassPath();
        Iterator<File> files = classpathElements.iterator();
        while(files.hasNext()) {
          cpBuf.append(files.next());
          cpBuf.append("\n");
        }
        _view.showInfoDialog(title, cpBuf.toString());
      }
    };
    showClasspathAction.setText("Show Interpreter Classpath");
    showClasspathAction.setToolTipText("Show the classpath used in the Interactions View");
    _view.addMenuItem(showClasspathAction);
  }
  
  
  private ImageDescriptor _getStandardIcon(ActionFactory f, IWorkbenchWindow w) {
    ActionFactory.IWorkbenchAction a = f.create(w);
    try { return a.getImageDescriptor(); }
    finally { a.dispose(); }
  }
  
  
  class KeyUpdateListener implements VerifyKeyListener {
    public void verifyKey(VerifyEvent event) {
      
      
      
      
      
      
      
      if (!_enabled) return;
      
      
      if (event.keyCode == 13 && event.stateMask == 0) {
        event.doit = evalAction();
      }
      
      else if (event.keyCode == 13 && (event.stateMask & SWT.SHIFT) == 1) {
        event.doit = newLineAction();
      }
      
      else if (event.keyCode == SWT.ARROW_UP) {
        event.doit = historyPrevAction();
      }
      
      else if (event.keyCode == SWT.ARROW_DOWN) {
        event.doit = historyNextAction();
      }
      
      else if (event.keyCode == SWT.ARROW_LEFT) {
        event.doit = moveLeftAction();
      }
      
      else if (event.keyCode == SWT.ARROW_RIGHT) {
        event.doit = moveRightAction();
      }
      
      else if (event.keyCode == SWT.HOME && (event.stateMask & SWT.SHIFT) == 1) {
        event.doit = selectToPromptPosAction();
      }
      
      else if (event.keyCode == SWT.HOME) {
        event.doit = gotoPromptPosAction();
      }
      
      else if (event.keyCode == '\t' && event.stateMask == 0) {
        event.doit = historyReverseSearchAction();
      }
      
      else if (event.keyCode == '\t' && (event.stateMask & SWT.SHIFT) == 1) {
        event.doit = historyForwardSearchAction();
      }
      
    }
  }
  
  
  boolean evalAction() {

    new Thread()
    {
      @Override
      public void run() {
        _model.interpretCurrentInteraction();
      }
    }.start();
    return false;
  }
  
  
  boolean newLineAction() {
    StyledText pane = _view.getTextPane();
    pane.replaceTextRange(pane.getCaretOffset(), 0, "\n");
    return false;
  }
  
  
  boolean historyPrevAction() {
    _doc.recallPreviousInteractionInHistory();
    moveToEnd();
    return false;
  }
  
  
  boolean historyNextAction() {
    _doc.recallNextInteractionInHistory();
    moveToEnd();
    return false;
  }
  
  
  boolean historyReverseSearchAction() {
    _doc.reverseSearchInteractionsInHistory();
    moveToEnd();
    return false;
  }
  
  
  boolean historyForwardSearchAction() {
    _doc.forwardSearchInteractionsInHistory();
    moveToEnd();
    return false;
  }
  
  
  boolean clearCurrentAction() {
    _doc.clearCurrentInteraction();
    return false;
  }
  
  
  boolean gotoPromptPosAction() {
    moveToPrompt();
    return false;
  }
  
  
  boolean selectToPromptPosAction() {
    
    StyledText pane = _view.getTextPane();
    int start = _doc.getPromptPos();
    int end = pane.getCaretOffset();
    if (end < start) {
      int t = start;
      start = end;
      end = t;
    }
    
    pane.setSelection(start, end);
    return false;
  }
  
  
  boolean moveLeftAction() {
    int position = _view.getTextPane().getCaretOffset();
    if (position < _doc.getPromptPos()) {
      moveToPrompt();
      return false;
    }
    else if (position == _doc.getPromptPos()) {
      
      moveToEnd();
      return false;
    }
    else { 
      
      return true;
    }
  }
  
  
  boolean moveRightAction() {
    int position = _view.getTextPane().getCaretOffset();
    if (position < _doc.getPromptPos()) {
      moveToEnd();
      return false;
    }
    else if (position >= _doc.getLength()) {
      
      moveToPrompt();
      return false;
    }
    else { 
      
      return true;
    }
  }
  
  
  void moveToEnd() {
    final StyledText pane = _view.getTextPane();
    pane.getDisplay().syncExec(new Runnable() {
      public void run() {
        pane.setCaretOffset(_doc.getLength());
        pane.showSelection();
      }
    });
  }
  
  
  void moveToPrompt() {
    final StyledText pane = _view.getTextPane();
    pane.getDisplay().syncExec(new Runnable() {
      public void run() {
        pane.setCaretOffset(_doc.getPromptPos());
        pane.showSelection();
      }
    });
  }
  
  
  class JFacePrefChangeListener implements IPropertyChangeListener {
    public void propertyChange(PropertyChangeEvent event) {
      _updateJFacePreferences();
    }
  }
  class PrefChangeListener implements Preferences.IPropertyChangeListener {
    public void propertyChange(Preferences.PropertyChangeEvent event) {
      _updatePreferences();
    }
  }
  
}
